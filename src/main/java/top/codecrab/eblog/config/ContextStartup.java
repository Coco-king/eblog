package top.codecrab.eblog.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;
import top.codecrab.eblog.entity.Category;
import top.codecrab.eblog.service.CategoryService;
import top.codecrab.eblog.service.PostService;

import javax.servlet.ServletContext;
import java.util.List;

@Component
public class ContextStartup implements ApplicationRunner, ServletContextAware {

    private ServletContext servletContext;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PostService postService;

    /**
     * 重写run方法，使程序在启动后查询数据库，显示未删除(状态为0)的分类信息
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Category> categories = categoryService.list(new QueryWrapper<Category>().eq("status", 0));
        servletContext.setAttribute("categories", categories);

        //初始化本周热议到缓存中
        postService.initWeekHot();
    }

    /**
     * 获取当前的上下文对象
     */
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
