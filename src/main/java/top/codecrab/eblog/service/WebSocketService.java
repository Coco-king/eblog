package top.codecrab.eblog.service;

public interface WebSocketService {
    void sendNotReadCountToUser(Long toUserId);
}
