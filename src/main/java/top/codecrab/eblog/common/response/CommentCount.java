package top.codecrab.eblog.common.response;

import lombok.Data;

@Data
public class CommentCount {
    private Long postId;
    private String postTitle;
    private Integer postViewCount;
    private Integer postCommentCount;
}
