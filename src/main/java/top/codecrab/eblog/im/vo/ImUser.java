package top.codecrab.eblog.im.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class ImUser {

    public final static String ONLINE_STATUS = "online";
    public final static String HIDE_STATUS = "hide";

    //防止前端过长数字失去精度
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String username;
    private String status;     //在线状态 online：在线、hide：隐身
    private String sign;       //我的签名
    private String avatar;     //我的头像

    private Boolean mine;      //是否我发送的消息
    private String content;    // 消息内容
}
