package top.codecrab.eblog.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.codecrab.eblog.entity.UserMessage;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserMessageVo extends UserMessage {

    private String postTitle;
    private String fromUsername;
}
