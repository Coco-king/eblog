package top.codecrab.eblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import top.codecrab.eblog.entity.UserMessage;
import top.codecrab.eblog.mapper.UserMessageMapper;
import top.codecrab.eblog.service.UserMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.codecrab.eblog.vo.UserMessageVo;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author codecrab
 * @since 2021-02-26
 */
@Service
public class UserMessageServiceImpl extends ServiceImpl<UserMessageMapper, UserMessage> implements UserMessageService {

    @Autowired
    private UserMessageMapper messageMapper;

    @Override
    public IPage<UserMessageVo> paging(Page<UserMessageVo> page, QueryWrapper<UserMessage> wrapper) {
        return messageMapper.selectMessages(page, wrapper);
    }

    @Override
    public void updateStatus(QueryWrapper<UserMessage> wrapper, int status) {
        messageMapper.updateStatus(status, wrapper);
    }
}
