package top.codecrab.eblog.shiro;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * shiro使用session的话，必须实现序列化接口
 */
@Data
public class AccountProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank(message = "昵称不能为空")
    @Length(min = 2, max = 16, message = "昵称在2-16个字符之间")
    private String username;

    @Email(message = "邮箱格式不正确")
    @NotBlank(message = "邮箱不能为空")
    private String email;
    private String avatar;
    private Date created;
    private String sign;
    private String city;
    private String gender;
    private Integer status;

    public String getSex() {
        if ("0".equals(gender)) {
            return "男";
        } else if ("1".equals(gender)) {
            return "女";
        } else {
            return "保密";
        }
    }
}
