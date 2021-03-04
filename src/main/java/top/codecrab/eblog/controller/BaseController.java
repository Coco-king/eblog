package top.codecrab.eblog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import top.codecrab.eblog.service.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BaseController {

    protected static final String KAPTCHA_SESSION_KEY = "KAPTCHA_SESSION_KEY";
    protected static final String FORGET_KAPTCHA_SESSION_KEY = "FORGET_KAPTCHA_SESSION_KEY";

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected HttpServletResponse response;

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

    @Autowired
    protected AdminService adminService;

    /**
     * 抽取获取分页bean
     *
     * @param <T> 分页的泛型
     */
    protected <T> Page<T> getPage() {
        //当前页
        int cp = ServletRequestUtils.getIntParameter(request, "cp", 1);
        //一页显示多少条
        int ps = ServletRequestUtils.getIntParameter(request, "ps", 10);
        //包装分页
        return new Page<>(cp, ps);
    }
}
