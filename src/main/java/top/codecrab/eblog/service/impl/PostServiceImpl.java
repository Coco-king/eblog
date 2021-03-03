package top.codecrab.eblog.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.codecrab.eblog.common.response.CommentCount;
import top.codecrab.eblog.entity.Comment;
import top.codecrab.eblog.entity.Post;
import top.codecrab.eblog.entity.UserMessage;
import top.codecrab.eblog.mapper.CommentMapper;
import top.codecrab.eblog.mapper.PostMapper;
import top.codecrab.eblog.service.CommentService;
import top.codecrab.eblog.service.PostService;
import top.codecrab.eblog.service.UserCollectionService;
import top.codecrab.eblog.service.UserMessageService;
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

    @Override
    public IPage<PostVo> paging(Page<PostVo> page, Long categoryId, Long userId, Integer level, Boolean recommend, String order) {
        int status = 1;
        if (level == null) level = -1;
        if (StringUtils.isBlank(order)) order = "created";
        Long profileId = ShiroUtil.getProfileId();
        if (profileId != null && profileId == 1) status = -1;

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
        return postMapper.selectOnePost(new QueryWrapper<Post>().eq("p.id", id)
                .ge(profileId == null || profileId != 1, "status", 1));
    }

    @Override
    public void initWeekHot() {
        //编写sql 获取评论时间为上一周的时间点为条件查询
        QueryWrapper<Comment> wrapper = new QueryWrapper<Comment>()
                .ge("created", DateUtil.lastWeek()).ne("post_id", 0)
                .groupBy("post_id");
        List<CommentCount> commentCounts = commentMapper.selectPostCommentCount(wrapper);
        for (CommentCount count : commentCounts) {
            redisUtil.zSet("week:hot:post", count.getPostId(), count.getPostCommentCount());
            redisUtil.hset("week:hot:postInfo:" + count.getPostId(), "id", count.getPostId());
            redisUtil.hset("week:hot:postInfo:" + count.getPostId(), "title", count.getPostTitle());
            redisUtil.hset("week:hot:postInfo:" + count.getPostId(), "postViewCount", count.getPostViewCount());
        }
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

        //删除关联的收藏
        collectionService.removeByMap(MapUtil.of("post_id", id));
        //逻辑删除评论和消息
        commentService.updateStatus(new QueryWrapper<Comment>().eq("post_id", id), -1);
        messageService.updateStatus(new QueryWrapper<UserMessage>().eq("post_id", id), -1);
    }
}
