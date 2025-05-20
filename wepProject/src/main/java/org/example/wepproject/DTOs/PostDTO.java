package org.example.wepproject.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.example.wepproject.Models.Post;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;

@Data
@Builder
public class PostDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("authorId")
    private Long authorId;

    @JsonProperty("categoryId")
    private Long categoryId;

    @JsonProperty("mediaBlobBase64")
    private String mediaBlobBase64;

    @JsonProperty("externalMediaUrl")
    private String externalMediaUrl;

    @JsonProperty("creationYear")
    private Integer creationYear;

    @JsonProperty("datePosted")
    private java.sql.Timestamp datePosted;

    @JsonProperty("description")
    private String description;

    @JsonProperty("likeCount")
    private long likeCount;

    @JsonProperty("commentCount")
    private long commentCount;

    public PostDTO(Long id, Long authorId, Long categoryId, String mediaBlobBase64, String externalMediaUrl,
                   Integer creationYear, java.sql.Timestamp datePosted, String description,
                   long likeCount, long commentCount) {
        this.id = id;
        this.authorId = authorId;
        this.categoryId = categoryId;
        this.mediaBlobBase64 = mediaBlobBase64;
        this.externalMediaUrl = externalMediaUrl;
        this.creationYear = creationYear;
        this.datePosted = datePosted;
        this.description = description;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }

    public static PostDTO PostToPostDTO(Post post) {


        String base64Image = null;
        if (post.getMediaBlob() != null) {
            try {
                Blob blob = post.getMediaBlob();
                byte[] bytes = blob.getBytes(1, (int) blob.length());
                base64Image = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(bytes);
            } catch (SQLException e) {
                System.out.println("SQLException in media blob conversion: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return PostDTO.builder()
                .id(post.getId())
                .authorId(post.getAuthorId())
                .categoryId(post.getCategoryId())
                .mediaBlobBase64(base64Image)
                .externalMediaUrl(post.getExternalMediaUrl())
                .creationYear(post.getCreationYear())
                .datePosted(post.getDatePosted())
                .description(post.getDescription())
                .likeCount(post.getLikesCount())
                .commentCount(post.getCommentsCount())
                .build();
    }
}