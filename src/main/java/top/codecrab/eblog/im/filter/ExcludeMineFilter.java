package top.codecrab.eblog.im.filter;

import lombok.Data;
import org.tio.core.ChannelContext;
import org.tio.core.ChannelContextFilter;

@Data
public class ExcludeMineFilter implements ChannelContextFilter {

    private ChannelContext currentContext;

    /**
     * 如果当前的用户id不为自己，就发送信息，否则拦截
     */
    @Override
    public boolean filter(ChannelContext channelContext) {
        return !channelContext.userid.equals(currentContext.userid);
    }
}
