package top.codecrab.eblog.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import top.codecrab.eblog.common.response.CommentCount;
import top.codecrab.eblog.common.response.Result;
import top.codecrab.eblog.entity.Comment;
import top.codecrab.eblog.entity.Post;
import top.codecrab.eblog.mapper.CommentMapper;
import top.codecrab.eblog.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.codecrab.eblog.service.PostService;
import top.codecrab.eblog.utils.ShiroUtil;
import top.codecrab.eblog.vo.CommentVo;

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
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    protected CommentMapper commentMapper;

    @Override
    public IPage<CommentVo> paging(Page<Comment> page, Long postId, Long userId, String order) {
        return commentMapper.selectComments(page, new QueryWrapper<Comment>()
                .eq(postId != null, "post_id", postId)
                .eq(userId != null, "user_id", userId)
                .ge("c.status", 0).orderByDesc("level", "vote_up", order)
        );
    }

    @Override
    public List<CommentVo> commentsAndPostTitle(QueryWrapper<Comment> wrapper) {
        return commentMapper.selectCommentsAndPostTitle(wrapper);
    }

    @Override
    public void updateStatus(QueryWrapper<Comment> wrapper, int status) {
        commentMapper.updateStatus(status, wrapper);
    }
}
