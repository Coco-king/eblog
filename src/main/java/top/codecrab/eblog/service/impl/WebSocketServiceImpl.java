package top.codecrab.eblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import top.codecrab.eblog.entity.UserMessage;
import top.codecrab.eblog.service.UserMessageService;
import top.codecrab.eblog.service.WebSocketService;
import top.codecrab.eblog.utils.ShiroUtil;

@Service
public class WebSocketServiceImpl implements WebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserMessageService messageService;

    @Override
    @Async //异步
    public void sendNotReadCountToUser(Long toUserId) {
        int count = messageService.count(new QueryWrapper<UserMessage>()
                .eq("to_user_id", toUserId).eq("status", 0));

        //websocket通知 通道==> /user/1/messageCount
        messagingTemplate.convertAndSendToUser(toUserId.toString(), "/messageCount", count);
    }
}
