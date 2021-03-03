package top.codecrab.eblog.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.codecrab.eblog.entity.Comment;

@Data
@EqualsAndHashCode(callSuper = true)
public class CommentVo extends Comment {

    private Long authorId;
    private String authorName;
    private String authorAvatar;
    private String postTitle;
}
