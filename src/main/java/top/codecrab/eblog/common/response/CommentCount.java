package top.codecrab.eblog.common.response;

import lombok.Data;

@Data
public class CommentCount {
    private Long postId;
    private String postTitle;
    private String postViewCount;
    private Integer postCommentCount;
}
