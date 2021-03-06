package top.codecrab.eblog.controller;

import cn.hutool.core.map.MapUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.codecrab.eblog.common.response.Result;
import top.codecrab.eblog.config.Consts;
import top.codecrab.eblog.im.vo.ImUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController extends BaseController {

    @GetMapping("/getMineAndGroupData")
    public Result getMineAndGroupData() {

        //默认群
        Map<String, Object> group = new HashMap<>();
        group.put("name", "社区群聊");
        group.put("type", "group");
        group.put("avatar", "https://img.imgdb.cn/item/6038ab575f4313ce25159507.png");
        group.put("id", Consts.IM_GROUP_ID);
        group.put("members", 0);

        ImUser user = chatService.getCurrentUser();
        return Result.success(MapUtil.builder()
                .put("group", group)
                .put("mine", user)
                .map());
    }

    @GetMapping("/getGroupHistoryMsg")
    public Result getGroupHistoryMsg() {
        List<Object> messages = chatService.getGroupHistoryMsg(100);
        return Result.success(messages);
    }
}
