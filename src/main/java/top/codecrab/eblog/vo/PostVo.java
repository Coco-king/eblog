package top.codecrab.eblog.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.codecrab.eblog.entity.Post;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class PostVo extends Post {

    private Long authorId;
    private String authorName;
    private String authorAvatar;

    private String categoryName;

    /**
     * 用户收藏帖子的日期
     */
    private String collectionCreated;

}
