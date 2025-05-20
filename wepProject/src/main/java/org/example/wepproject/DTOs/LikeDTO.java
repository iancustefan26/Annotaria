package org.example.wepproject.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LikeDTO {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("post_id")
    private Long postId;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("userHasLiked")
    private boolean userHasLiked;
    @JsonProperty("likeCount")
    private long likeCount;
}
