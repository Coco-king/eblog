package top.codecrab.eblog.im.message;

import lombok.Data;
import top.codecrab.eblog.im.vo.ImMess;

@Data
public class ChatOutMessage {

    private String emit;
    private ImMess data;

}
