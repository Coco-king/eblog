package top.codecrab.eblog.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.codecrab.eblog.common.response.Result;
import top.codecrab.eblog.entity.Comment;
import top.codecrab.eblog.entity.Post;
import top.codecrab.eblog.entity.UserMessage;
import top.codecrab.eblog.service.*;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    protected PostService postService;

    @Autowired
    protected CommentService commentService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected UserMessageService messageService;

    @Autowired
    protected UserCollectionService collectionService;

    @Autowired
    protected CategoryService categoryService;

    @Override
    @Transactional
    public Result jieSet(Long id, String field, Integer rank) {
        Post post = postService.getById(id);
        Assert.notNull(post, "该文章已被封禁或删除");

        if ("delete".equals(field)) {
            //删除 -- 逻辑删除
            post.setStatus(-1);
            //删除关联的收藏
            collectionService.removeByMap(MapUtil.of("post_id", id));
            //逻辑删除评论和消息
            commentService.updateStatus(new QueryWrapper<Comment>().eq("post_id", id), -1);
            messageService.updateStatus(new QueryWrapper<UserMessage>().eq("post_id", id), -1);
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
