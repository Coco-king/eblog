package top.codecrab.eblog.im.message;

import lombok.Data;
import top.codecrab.eblog.im.vo.ImTo;
import top.codecrab.eblog.im.vo.ImUser;

@Data
public class ChatImMessage {

    private ImUser mine;
    private ImTo to;

}
