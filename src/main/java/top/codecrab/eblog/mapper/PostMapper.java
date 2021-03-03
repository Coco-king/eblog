package top.codecrab.eblog.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import top.codecrab.eblog.entity.Post;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.codecrab.eblog.vo.PostVo;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author codecrab
 * @since 2021-02-26
 */
public interface PostMapper extends BaseMapper<Post> {

    IPage<PostVo> selectPosts(Page<PostVo> page, @Param(Constants.WRAPPER) QueryWrapper<Post> wrapper);

    PostVo selectOnePost(@Param(Constants.WRAPPER) QueryWrapper<Post> wrapper);

    IPage<PostVo> selectPageCollection(Page<PostVo> page, @Param(Constants.WRAPPER) QueryWrapper<Post> wrapper);
}
