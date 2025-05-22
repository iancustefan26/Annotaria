package org.example.wepproject.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class CommentDTO {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("post_id")
    private Long postId;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("username")
    private String username;
    @JsonProperty("content")
    private String content;
    @JsonProperty("datePosted")
    private Timestamp datePosted;
    @JsonProperty("commentCount")
    private long commentCount;
    @JsonProperty("isOwnComment")
    private boolean isOwnComment;
}
