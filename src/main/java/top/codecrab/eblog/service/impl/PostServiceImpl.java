package top.codecrab.eblog.service.impl;

import java.util.Date;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.codecrab.eblog.common.response.CommentCount;
import top.codecrab.eblog.common.response.Result;
import top.codecrab.eblog.entity.*;
import top.codecrab.eblog.mapper.CommentMapper;
import top.codecrab.eblog.mapper.PostMapper;
import top.codecrab.eblog.service.*;
import top.codecrab.eblog.utils.CommonUtils;
import top.codecrab.eblog.utils.RedisUtil;
import top.codecrab.eblog.utils.ShiroUtil;
import top.codecrab.eblog.vo.PostVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author codecrab
 * @since 2021-02-26
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserCollectionService collectionService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserMessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private WebSocketService socketService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public IPage<PostVo> paging(Page<PostVo> page, Long categoryId, Long userId, Integer level, Boolean recommend, String order) {
        int status = 1;
        if (level == null) level = -1;
        if (StringUtils.isBlank(order)) order = "created";
//        Long profileId = ShiroUtil.getProfileId();
//        if (profileId != null && profileId == 1) status = -1;

        QueryWrapper<Post> wrapper = new QueryWrapper<Post>()
                .eq(categoryId != null, "category_id", categoryId)
                .eq(userId != null, "user_id", userId)
                .eq(recommend != null, "recommend", recommend)
                .eq(level == 0, "level", 0)
                .gt(level > 0, "level", 0).ge("p.status", status)
                .orderByDesc(level > 0, "level").orderByDesc(order);
        return postMapper.selectPosts(page, wrapper);
    }

    @Override
    public PostVo detail(Long id) {
        Long profileId = ShiroUtil.getProfileId();
        PostVo postVo = postMapper.selectOnePost(new QueryWrapper<Post>().eq("p.id", id)
                .ge(/*profileId == null || profileId != 1,*/ "p.status", 0));
        if (postVo == null) return null;
        if (postVo.getStatus() == 0 && !postVo.getUserId().equals(profileId)) return null;
        return postVo;
    }

    @Override
    public void initWeekHot() {
        //编写sql 获取评论时间为上一周的时间点为条件查询
        QueryWrapper<Comment> wrapper = new QueryWrapper<Comment>()
                .ge("created", DateUtil.lastWeek()).ne("post_id", 0)
                .groupBy("post_id");
        List<CommentCount> commentCounts = commentMapper.selectPostCommentCount(wrapper);
        for (CommentCount count : commentCounts) {
            packageWeekHot(count, count.getPostId());
        }
    }

    private void packageWeekHot(CommentCount count, Long postId) {
        redisUtil.zSet("week:hot:post", postId, count.getPostCommentCount());
        redisUtil.hset("week:hot:postInfo:" + postId, "id", postId);
        redisUtil.hset("week:hot:postInfo:" + postId, "title", count.getPostTitle());
        redisUtil.hset("week:hot:postInfo:" + postId, "postViewCount", count.getPostViewCount());
        //设置过期时间一周
        redisUtil.expire("week:hot:post", 7 * 24 * 60 * 60);
        redisUtil.expire("week:hot:postInfo:" + postId, 7 * 24 * 60 * 60);
    }

    /**
     * 把阅读量缓存到redis中
     */
    @Override
    public void cacheViewCount(PostVo postVo, HttpServletRequest request) {
        String ip = CommonUtils.getRemoteHost(request);
        String key = "post:viewCount:" + postVo.getId();
        //如果缓存中存在该文章并且hash的键一样的，就直接return，不用增加
        if (redisUtil.hHasKey(key, ip)) {
            //显示假数据，使页面显示正常，不刷新缓存，等5分钟计时器更新
            postVo.setViewCount(postVo.getViewCount() + 1);
            return;
        }
        //根据不同id，hash的键也就不一样，最后就按照hash的长度来确定增加了多少访问量，过期时间：三个小时
        redisUtil.hset(key, ip, 1, 3 * 60 * 60);
        //同一个ip一天之内没访问过，访问量 + 1
        postVo.setViewCount(postVo.getViewCount() + 1);
    }

    /**
     * 分页查询用户收藏
     */
    @Override
    public IPage<PostVo> selectPageCollection(Page<PostVo> page, QueryWrapper<Post> wrapper) {
        return postMapper.selectPageCollection(page, wrapper);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Post post = this.getById(id);
        Assert.notNull(post, "文章不存在或已被删除");
        Long profileId = ShiroUtil.getProfileId();
        if (profileId == null || profileId != 1) {
            Assert.isTrue(post.getUserId().equals(profileId), "您没有权限删除不属于您的文章");
        }

        //逻辑删除文章
        post.setStatus(-1);
        this.updateById(post);

        //该分类下文章个数减1
        Category category = categoryService.getById(post.getCategoryId());
        Assert.notNull(category, "找不到分类");
        category.setPostCount(category.getPostCount() - 1);
        categoryService.updateById(category);

        //删除关联的收藏
        collectionService.removeByMap(MapUtil.of("post_id", id));
        //逻辑删除评论和消息
        commentService.updateStatus(new QueryWrapper<Comment>().eq("post_id", id), -1);
        messageService.updateStatus(new QueryWrapper<UserMessage>().eq("post_id", id), -1);
    }

    @Override
    @Transactional
    public Result postReply(Long postId, String content) {
        Assert.notNull(postId, "文章不存在或已被删除");
        Assert.notBlank(content, "评论内容不能为空");
        Post post = this.getById(postId);
        Assert.notNull(post, "文章不存在或已被删除");

        Long profileId = ShiroUtil.getProfileId();

        //新建评论
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setParentId(0L);
        comment.setPostId(postId);
        comment.setUserId(profileId);
        comment.setVoteUp(0);
        comment.setVoteDown(0);
        comment.setLevel(0);
        comment.setCreated(new Date());
        comment.setModified(comment.getCreated());
        comment.setStatus(1); //0:未审核
        commentService.save(comment);

        //更新博客评论数
        post.setCommentCount(post.getCommentCount() + 1);
        this.updateById(post);

        //更新本周热议
        CommentCount count = new CommentCount();
        count.setPostId(postId);
        count.setPostTitle(post.getTitle());
        count.setPostViewCount(post.getViewCount());
        count.setPostCommentCount(post.getCommentCount());
        //更新缓存的分数，由于已经是更新好的值，存在直接覆盖就好，不存在添加
        this.packageWeekHot(count, postId);

        //评论的文章所属用户不等于当前用户，再提醒
        if (!post.getUserId().equals(profileId)) {
            UserMessage message = new UserMessage();
            message.setFromUserId(profileId);
            message.setToUserId(post.getUserId());
            message.setPostId(postId);
            message.setCommentId(comment.getId());
            message.setContent(content);
            message.setType(1);
            message.setCreated(new Date());
            message.setModified(message.getCreated());
            message.setStatus(0);//0:未读
            messageService.save(message);

            //即时通知作者
            socketService.sendNotReadCountToUser(message.getToUserId());
            return Result.success().action("/post/" + postId);
        }

        //如果是回复，进行提醒
        if (content.startsWith("@")) {
            //被回复的用户
            String username;
            try {
                username = content.substring(1, content.indexOf(" "));
            } catch (Exception exception) {
                throw new RuntimeException("回复评论的格式有误");
            }
            Assert.notBlank(username, "回复的用户名不能为空");
            User user = userService.getOne(new QueryWrapper<User>().eq("username", username));
            Assert.notNull(user, "用户已注销或不存在");

            UserMessage message = new UserMessage();
            message.setFromUserId(profileId);
            message.setToUserId(user.getId());
            message.setPostId(postId);
            message.setCommentId(comment.getId());
            message.setContent(content);
            message.setType(2);
            message.setCreated(new Date());
            message.setModified(message.getCreated());
            message.setStatus(0);//0:未读
            messageService.save(message);

            //即时通知被@的人
            socketService.sendNotReadCountToUser(message.getToUserId());
        }
        return Result.success().action("/post/" + postId);
    }

    @Override
    @Transactional
    public Result jiedaDelete(Long id) {
        Comment comment = commentService.getById(id);
        Assert.notNull(comment, "找不到该评论或已被删除");
        //文章id
        Long postId = comment.getPostId();
        //当前用户id
        Long profileId = ShiroUtil.getProfileId();
        //表示删除自己的评论
        if (comment.getUserId().equals(profileId)) {
            Post post = this.getById(postId);
            this.deleteReply(comment, postId, post);
            return Result.success();
        }

        Post p = this.getOne(new QueryWrapper<Post>()
                .eq("id", postId).eq("user_id", profileId));
        //表示删除自己文章下的评论
        if (p != null) {
            this.deleteReply(comment, postId, p);
        }
        return Result.fail("您只能删除自己或自己文章下的评论");
    }

    private void deleteReply(Comment comment, Long postId, Post post) {
        post.setCommentCount(post.getCommentCount() - 1);
        this.updateById(post);

        comment.setStatus(-1);
        commentService.updateById(comment);

        //本周热议-1
        CommentCount count = new CommentCount();
        count.setPostId(postId);
        count.setPostTitle(post.getTitle());
        count.setPostViewCount(post.getViewCount());
        count.setPostCommentCount(post.getCommentCount());
        //更新缓存的分数，由于已经是更新好的值，存在直接覆盖就好，不存在添加
        this.packageWeekHot(count, postId);
    }
}
