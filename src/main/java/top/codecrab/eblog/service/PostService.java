package top.codecrab.eblog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.codecrab.eblog.entity.Post;
import com.baomidou.mybatisplus.extension.service.IService;
import top.codecrab.eblog.vo.PostVo;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author codecrab
 * @since 2021-02-26
 */
public interface PostService extends IService<Post> {

    IPage<PostVo> paging(Page<PostVo> page, Long categoryId, Long userId, Integer level, Boolean recommend, String order);

    PostVo detail(Long id);

    void initWeekHot();

    void cacheViewCount(PostVo postVo, HttpServletRequest request);

    IPage<PostVo> selectPageCollection(Page<PostVo> page, QueryWrapper<Post> wrapper);

    void delete(Long id);
}
