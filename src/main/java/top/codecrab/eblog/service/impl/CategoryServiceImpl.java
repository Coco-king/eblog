package top.codecrab.eblog.service.impl;

import top.codecrab.eblog.entity.Category;
import top.codecrab.eblog.mapper.CategoryMapper;
import top.codecrab.eblog.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author codecrab
 * @since 2021-02-26
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

}
