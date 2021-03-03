package top.codecrab.eblog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.codecrab.eblog.entity.UserMessage;
import com.baomidou.mybatisplus.extension.service.IService;
import top.codecrab.eblog.vo.UserMessageVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author codecrab
 * @since 2021-02-26
 */
public interface UserMessageService extends IService<UserMessage> {

    IPage<UserMessageVo> paging(Page<UserMessageVo> page, QueryWrapper<UserMessage> orderByAsc);

    void updateStatus(QueryWrapper<UserMessage> wrapper, int status);
}
