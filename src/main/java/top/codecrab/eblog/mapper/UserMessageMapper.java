package top.codecrab.eblog.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import top.codecrab.eblog.entity.UserMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.codecrab.eblog.vo.UserMessageVo;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author codecrab
 * @since 2021-02-26
 */
public interface UserMessageMapper extends BaseMapper<UserMessage> {

    IPage<UserMessageVo> selectMessages(Page<UserMessageVo> page, @Param(Constants.WRAPPER) QueryWrapper<UserMessage> wrapper);

    void updateStatus(@Param("status") int status, @Param(Constants.WRAPPER) QueryWrapper<UserMessage> wrapper);
}
