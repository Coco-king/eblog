package top.codecrab.eblog.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.codecrab.eblog.common.response.Result;
import top.codecrab.eblog.entity.Category;
import top.codecrab.eblog.entity.Comment;
import top.codecrab.eblog.entity.Post;
import top.codecrab.eblog.entity.UserMessage;
import top.codecrab.eblog.search.mq.MqTypes;
import top.codecrab.eblog.service.*;
import top.codecrab.eblog.utils.RedisUtil;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserMessageService messageService;

    @Autowired
    private UserCollectionService collectionService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    @Transactional
    public Result jieSet(Long id, String field, Integer rank) {
        Post post = postService.getById(id);
        Assert.notNull(post, "该文章已被封禁或删除");

        if ("delete".equals(field)) {
            //删除 -- 逻辑删除
            post.setCommentCount(0);
            post.setStatus(-1);
            //更新每周热议
            redisUtil.zSet("week:hot:post", id, 0);
            redisUtil.del("week:hot:postInfo:" + id);

            //该分类下文章个数减1
            Category category = categoryService.getById(post.getCategoryId());
            Assert.notNull(category, "找不到分类");
            category.setPostCount(Math.max(category.getPostCount() - 1, 0));
            categoryService.updateById(category);

            //发送消息到队列
            amqpTemplate.convertAndSend(MqTypes.POST_REMOVE_ROUTING_KEY, id);

            //删除关联的收藏
            collectionService.removeByMap(MapUtil.of("post_id", id));
            //逻辑删除评论和消息
            commentService.updateStatus(new QueryWrapper<Comment>().eq("post_id", id), -1);
            messageService.updateStatus(new QueryWrapper<UserMessage>().eq("post_id", id), -1);
//            postService.deleteRedis(id);
        } else if ("stick".equals(field)) {
            //置顶
            post.setLevel(rank);
        } else if ("status".equals(field)) {
            //加精
            post.setRecommend(rank == 1);
        }
        postService.updateById(post);
        return Result.success();
    }
}
