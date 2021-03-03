package top.codecrab.eblog.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import top.codecrab.eblog.common.response.Result;
import top.codecrab.eblog.config.UploadConfig;
import top.codecrab.eblog.shiro.AccountProfile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

@Slf4j
@Component
public class UploadUtil {

    @Autowired
    UploadConfig uploadConfig;

    public final static String TYPE_AVATAR = "avatar";

    public Result upload(String type, MultipartFile file) throws IOException {

        if (StrUtil.isBlank(type) || file.isEmpty()) {
            return Result.fail("上传失败");
        }

        if (file.getSize() > 500 * 1024) {
            return Result.fail("图片大小超出限制");
        }

        // 获取文件名
        String fileName = file.getOriginalFilename();
        log.info("上传的文件名为：" + fileName);
        // 获取文件的后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        log.info("上传的后缀名为：" + suffixName);
        // 文件上传后的路径
        String filePath = uploadConfig.getUploadDir();

        if ("avatar".equalsIgnoreCase(type)) {
            AccountProfile profile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
            fileName = "/avatar/avatar_" + profile.getId() + suffixName;

        } else if ("post".equalsIgnoreCase(type)) {
            fileName = "/post/post_" + DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN) + suffixName;
        }

        File dest = new File(filePath + fileName);
        // 检测是否存在目录
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            file.transferTo(dest);
            log.info("上传成功后的文件路径未：" + filePath + fileName);

            String path = filePath + fileName;
            String url = "/upload" + fileName;

            log.info("url ---> {}", url);

            return Result.success(url);
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        return Result.success(null);
    }

}
