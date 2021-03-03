package top.codecrab.eblog.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * <p>
 * 用户信息
 * </p>
 *
 * @author codecrab
 * @since 2021-02-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("m_user")
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 昵称
     */
    @NotBlank(message = "昵称不能为空")
    @Length(min = 2, max = 16, message = "昵称在2-16个字符之间")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 16, message = "密码在6-16个字符之间")
    private String password;

    /**
     * 邮件
     */
    @Email(message = "邮箱格式不正确")
    @NotBlank(message = "邮箱不能为空")
    private String email;

    /**
     * 手机电话
     */
    private String mobile;

    /**
     * 积分
     */
    private Integer point;

    /**
     * 个性签名
     */
    private String sign;

    /**
     * 性别
     */
    private String gender;

    /**
     * 城市
     */
    private String city;

    /**
     * 微信号
     */
    private String wechat;

    /**
     * vip等级
     */
    private Integer vipLevel;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 内容数量
     */
    private Integer postCount;

    /**
     * 评论数量
     */
    private Integer commentCount;

    /**
     * 最后的登陆时间
     */
    private Date lasted;

    /**
     * 盐
     */
    private String salt;

    /**
     * 激活邮件地址
     */
    private String code;

}
