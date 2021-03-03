package top.codecrab.eblog.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import top.codecrab.eblog.common.response.CommentCount;
import top.codecrab.eblog.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.codecrab.eblog.vo.CommentVo;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author codecrab
 * @since 2021-02-26
 */
public interface CommentMapper extends BaseMapper<Comment> {

    IPage<CommentVo> selectComments(Page<Comment> page, @Param(Constants.WRAPPER) QueryWrapper<Comment> orderByDesc);

    List<CommentVo> selectCommentsAndPostTitle(@Param(Constants.WRAPPER) QueryWrapper<Comment> wrapper);

    List<CommentCount> selectPostCommentCount(@Param(Constants.WRAPPER) QueryWrapper<Comment> wrapper);

    void updateStatus(@Param("status") int status, @Param(Constants.WRAPPER) QueryWrapper<Comment> wrapper);
}
