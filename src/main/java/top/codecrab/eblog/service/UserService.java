package top.codecrab.eblog.service;

import top.codecrab.eblog.common.response.Result;
import top.codecrab.eblog.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import top.codecrab.eblog.shiro.AccountProfile;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author codecrab
 * @since 2021-02-26
 */
public interface UserService extends IService<User> {

    Result register(User user);

    AccountProfile login(String username, String password);

    Result doSet(AccountProfile profile);

    Result sendActivateEmail(String email);

    Result sendForgetEmail(String email, String code);
}
