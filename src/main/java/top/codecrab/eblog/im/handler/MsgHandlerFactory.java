package top.codecrab.eblog.im.handler;

import lombok.extern.slf4j.Slf4j;
import top.codecrab.eblog.config.Consts;
import top.codecrab.eblog.im.handler.impl.ChatMsgHandler;
import top.codecrab.eblog.im.handler.impl.PingMsgHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MsgHandlerFactory {

    private static final Map<String, MsgHandler> msgHandler = new HashMap<>();

    public static void init() {
        msgHandler.put(Consts.IM_MESS_TYPE_CHAT, new ChatMsgHandler());
        msgHandler.put(Consts.IM_MESS_TYPE_PING, new PingMsgHandler());

        log.info("handler factory init!!");
    }

    public static MsgHandler getMsgHandler(String type) {
        return msgHandler.get(type);
    }
}
