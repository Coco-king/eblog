package top.codecrab.eblog.service;

import top.codecrab.eblog.im.vo.ImMess;
import top.codecrab.eblog.im.vo.ImUser;

import java.util.List;

public interface ChatService {
    ImUser getCurrentUser();

    void setGroupHistoryMsg(ImMess responseMess);

    List<Object> getGroupHistoryMsg(int count);
}
