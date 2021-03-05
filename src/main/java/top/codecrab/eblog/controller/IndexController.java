package top.codecrab.eblog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import top.codecrab.eblog.search.model.PostDocument;
import top.codecrab.eblog.vo.PostVo;

@Controller
public class IndexController extends BaseController {

    @RequestMapping({"", "/", "/index"})
    public String index() {
        //执行查询，1、分页信息 2、分类id 3、所属用户 4、置顶等级 5、精选 6、排序字段
        IPage<PostVo> pageData = postService.paging(getPage(), null, null,
                null, null, "created");

        request.setAttribute("currentCategoryId", 0);
        request.setAttribute("pageData", pageData);
        request.setAttribute("isOrder", "created");

        return "index";
    }

    @GetMapping("/recommend")
    public String recommend(Boolean recommend, String order) {
        int cp = ServletRequestUtils.getIntParameter(request, "cp", 1);
        request.setAttribute("cp", cp);
        request.setAttribute("isRecommend", recommend);
        request.setAttribute("isOrder", order);
        //执行查询，1、分页信息 2、分类id 3、所属用户 4、置顶等级 5、精选 6、排序字段
        IPage<PostVo> pageData = postService.paging(new Page<>(cp, 10), null, null,
                null, recommend, order);
        request.setAttribute("currentCategoryId", 0);
        request.setAttribute("pageData", pageData);
        return "index";
    }

    @RequestMapping("/search")
    public String search(String q, Boolean recommend, String order) {
        int cp = ServletRequestUtils.getIntParameter(request, "cp", 1);
        int ps = ServletRequestUtils.getIntParameter(request, "ps", 10);

        IPage<PostDocument> pageData = searchService.search(PageRequest.of(cp - 1, ps), q, recommend, order);

        request.setAttribute("cp", cp);
        request.setAttribute("isRecommend", recommend);
        request.setAttribute("isOrder", order);
        request.setAttribute("q", q);
        request.setAttribute("pageData", pageData);
        return "search";
    }

}
