package top.codecrab.eblog.im.server;

import org.tio.server.ServerTioConfig;
import org.tio.websocket.server.WsServerStarter;
import org.tio.websocket.server.handler.IWsMsgHandler;
import top.codecrab.eblog.im.handler.ImWsMsgHandler;

import java.io.IOException;

public class ImServerStarter {

    private final WsServerStarter starter;

    public ImServerStarter(int port) throws IOException {
        //创建消息处理器
        IWsMsgHandler handler = new ImWsMsgHandler();
        //创建ws的服务
        starter = new WsServerStarter(port, handler);
        //设置心跳时间
        ServerTioConfig config = starter.getServerTioConfig();
        //5秒
        config.setHeartbeatTimeout(5000);
    }

    /**
     * 开启ws服务
     */
    public void start() throws IOException {
        starter.start();
    }
}
