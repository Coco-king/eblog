package top.codecrab.eblog.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.codecrab.eblog.common.response.Result;
import top.codecrab.eblog.entity.Post;
import top.codecrab.eblog.search.mq.MqTypes;

import java.util.Map;

@Slf4j
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

    /**
     * 管理员初始化es
     */
    @ResponseBody
    @PostMapping("/initEsData")
    public Result initEsData() {
        long initTotal = searchService.initES();
        return Result.success("ES初始化成功，共有 " + initTotal + " 条数据", initTotal);
    }

    @GetMapping("/check")
    public String check() {
        return "admin";
    }

    @ResponseBody
    @PostMapping("/check/noCheck")
    public Result doCheck() {
        IPage<Post> page = postService.page(getPage(), new QueryWrapper<Post>().eq("status", 0));
        return Result.success(page);
    }

    @GetMapping("/pass")
    public String pass(Long id) {
        Post post = postService.getById(id);
        post.setStatus(1);
        postService.updateById(post);
        return "admin";
    }

    @GetMapping("/delete")
    public String delete(Long id) {
        postService.delete(id);
        amqpTemplate.convertAndSend(MqTypes.POST_REMOVE_ROUTING_KEY, id);
        return "admin";
    }
}
