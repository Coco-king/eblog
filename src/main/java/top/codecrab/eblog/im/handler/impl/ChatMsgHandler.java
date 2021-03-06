package top.codecrab.eblog.im.handler.impl;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.websocket.common.WsRequest;
import org.tio.websocket.common.WsResponse;
import top.codecrab.eblog.config.Consts;
import top.codecrab.eblog.im.filter.ExcludeMineFilter;
import top.codecrab.eblog.im.handler.MsgHandler;
import top.codecrab.eblog.im.message.ChatImMessage;
import top.codecrab.eblog.im.message.ChatOutMessage;
import top.codecrab.eblog.im.vo.ImMess;
import top.codecrab.eblog.im.vo.ImTo;
import top.codecrab.eblog.im.vo.ImUser;
import top.codecrab.eblog.service.ChatService;
import top.codecrab.eblog.utils.SpringUtil;

import java.util.Date;

@Slf4j
public class ChatMsgHandler implements MsgHandler {

    @Override
    public void handler(String data, WsRequest wsRequest, ChannelContext channelContext) {
        ChatImMessage imMessage = JSONUtil.toBean(data, ChatImMessage.class);

        ImUser mine = imMessage.getMine(); //发送人
        ImTo to = imMessage.getTo(); //接收人在这里是群聊

        //可以在这里过滤关键词

        ImMess imMess = new ImMess();
        imMess.setUsername(mine.getUsername());
        imMess.setAvatar(mine.getAvatar());
        imMess.setType(to.getType());
        imMess.setContent(mine.getContent());
        imMess.setMine(false);
        imMess.setFromid(mine.getId());
        imMess.setTimestamp(new Date());
        imMess.setId(Consts.IM_GROUP_ID);

        ChatOutMessage outMessage = new ChatOutMessage();
        outMessage.setData(imMess);
        outMessage.setEmit(Consts.IM_MESS_TYPE_CHAT);

        String jsonStr = JSONUtil.toJsonStr(outMessage);
        log.info("群聊信息----> {}", jsonStr);

        WsResponse wsResponse = WsResponse.fromText(jsonStr, "utf-8");

        //由于前端框架会自动把自己的发言显示一遍，所以要在后端过滤掉自己的发言
        ExcludeMineFilter filter = new ExcludeMineFilter();
        filter.setCurrentContext(channelContext);
        //发送到群聊
        Tio.sendToGroup(channelContext.getTioConfig(), Consts.IM_GROUP_NAME, wsResponse, filter);
        //缓存到redis
        ChatService chatService = SpringUtil.getBean("chatService", ChatService.class);
        chatService.setGroupHistoryMsg(imMess);
    }

}
