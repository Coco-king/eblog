package top.codecrab.eblog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.codecrab.eblog.common.response.CommentCount;
import top.codecrab.eblog.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import top.codecrab.eblog.vo.CommentVo;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author codecrab
 * @since 2021-02-26
 */
public interface CommentService extends IService<Comment> {

    IPage<CommentVo> paging(Page<Comment> page, Long postId, Long userId, String order);

    List<CommentVo> commentsAndPostTitle(QueryWrapper<Comment> wrapper);

    void updateStatus(QueryWrapper<Comment> wrapper, int status);
}
