package top.codecrab.eblog.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.codecrab.eblog.config.Consts;
import top.codecrab.eblog.im.vo.ImMess;
import top.codecrab.eblog.im.vo.ImUser;
import top.codecrab.eblog.service.ChatService;
import top.codecrab.eblog.shiro.AccountProfile;
import top.codecrab.eblog.utils.CommonUtils;
import top.codecrab.eblog.utils.RedisUtil;

import java.util.List;

@Slf4j
@Service("chatService")
public class ChatServiceImpl implements ChatService {

    @Value("${im.user.default-avatar}")
    private String defaultAvatar;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public ImUser getCurrentUser() {
        AccountProfile profile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        ImUser user = new ImUser();
        if (profile != null) {
            user.setId(profile.getId());
            user.setUsername(profile.getUsername() + (user.getId() == 1 ? "（管理员）" : ""));
            user.setStatus(ImUser.ONLINE_STATUS);
            user.setSign(profile.getSign());
            user.setAvatar(profile.getAvatar());
        } else {
            Session session = SecurityUtils.getSubject().getSession();
            Long userId = (Long) session.getAttribute("imUserId");
            user.setId(userId == null ? RandomUtil.randomLong() : userId);
            session.setAttribute("imUserId", user.getId());

            String string = user.getId().toString();
            user.setUsername("匿名用户#" + string.substring(0, 4));
            user.setStatus(ImUser.ONLINE_STATUS);
            user.setSign(CommonUtils.getSign());

            String[] split = defaultAvatar.split(",");
            user.setAvatar(split[Integer.parseInt(string.substring(string.length() - 1))]);
        }

        return user;
    }

    @Override
    public void setGroupHistoryMsg(ImMess imMess) {
        redisUtil.lSet(Consts.IM_GROUP_HISTROY_MSG_KEY, imMess, 7 * 24 * 60 * 60);
    }

    @Override
    public List<Object> getGroupHistoryMsg(int count) {
        long length = redisUtil.lGetListSize(Consts.IM_GROUP_HISTROY_MSG_KEY);
        return redisUtil.lGet(Consts.IM_GROUP_HISTROY_MSG_KEY, length - count < 0 ? 0 : length - count, length);
    }
}
