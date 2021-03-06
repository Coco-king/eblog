package top.codecrab.eblog.im.handler;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.websocket.common.WsRequest;
import org.tio.websocket.server.handler.IWsMsgHandler;
import top.codecrab.eblog.config.Consts;

import java.util.Map;

@Slf4j
public class ImWsMsgHandler implements IWsMsgHandler {

    /**
     * 握手时走的方法
     *
     * @return 返回HttpResponse表示可以握手，返回其他不可以
     */
    @Override
    public HttpResponse handshake(HttpRequest httpRequest, HttpResponse httpResponse, ChannelContext channelContext) throws Exception {

        //握手时绑定到群通道，也可以拦截指定用户
        String userId = httpRequest.getParam("userId");
        log.info("{} ====> 正在握手", userId);
        Tio.bindUser(channelContext, userId);

        return httpResponse;
    }

    /**
     * 握手之后执行
     */
    @Override
    public void onAfterHandshaked(HttpRequest httpRequest, HttpResponse httpResponse, ChannelContext channelContext) throws Exception {
        //绑定群
        Tio.bindGroup(channelContext, Consts.IM_GROUP_NAME);
        log.info("绑定群通道 ----> {}", channelContext.getId());
    }

    /**
     * 传输字节码消息
     */
    @Override
    public Object onBytes(WsRequest wsRequest, byte[] bytes, ChannelContext channelContext) throws Exception {
        return null;
    }

    /**
     * 传输字符串消息
     *
     * @param s json数据，type、data
     */
    @Override
    public Object onText(WsRequest wsRequest, String s, ChannelContext channelContext) throws Exception {
        if (s != null && !s.contains("ping")) {
            log.info("接收到信息——————————————————>{}", s);
        }
        Map map = JSONUtil.toBean(s, Map.class);
        String type = MapUtil.getStr(map, "type");
        String data = MapUtil.getStr(map, "data");

        MsgHandler handler = MsgHandlerFactory.getMsgHandler(type);
        // 处理消息
        handler.handler(data, wsRequest, channelContext);
        return null;
    }

    /**
     * 关闭连接执行的方法
     */
    @Override
    public Object onClose(WsRequest wsRequest, byte[] bytes, ChannelContext channelContext) throws Exception {
        return null;
    }
}
