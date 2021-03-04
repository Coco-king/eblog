package top.codecrab.eblog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.codecrab.eblog.common.response.Result;

@Controller
@RequestMapping("/admin")
public class AdminController extends BaseController {

    /**
     * 管理员操作之 加精，置顶，删除
     *
     * @param id    文章id
     * @param field 方法的属性，具体为上面三种的哪一种
     * @param rank  1表示操作，0表示取消
     */
    @ResponseBody
    @PostMapping("/jie-set")
    public Result jieSet(Long id, String field, Integer rank) {
        return adminService.jieSet(id, field, rank);
    }
}
