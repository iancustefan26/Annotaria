package org.example.wepproject.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.example.wepproject.DAOs.PostDAO;
import org.example.wepproject.Models.Post;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
public class PostDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("authorId")
    private Long authorId;

    @JsonProperty("authorUsername")
    private String authorUsername;

    @JsonProperty("categoryId")
    private Long categoryId;

    @JsonProperty("mediaBlobBase64")
    private String mediaBlobBase64;

    @JsonProperty("externalMediaUrl")
    private String externalMediaUrl;

    @JsonProperty("creationYear")
    private Integer creationYear;

    @JsonProperty("datePosted")
    private Timestamp datePosted;

    @JsonProperty("description")
    private String description;

    @JsonProperty("likeCount")
    private long likeCount;

    @JsonProperty("commentCount")
    private long commentCount;

    @JsonProperty("isOwnPost")
    private Boolean isOwnPost;

    @JsonProperty("isSaved")
    private Boolean isSaved;

    @JsonProperty("isLiked")
    private Boolean isLiked;

    @JsonProperty("mediaType")
    private String mediaType;

    public PostDTO(Long id, Long authorId, String authorUsername, Long categoryId, String mediaBlobBase64,
                   String externalMediaUrl, Integer creationYear, Timestamp datePosted,
                   String description, long likeCount, long commentCount, Boolean isOwnPost,
                   Boolean isSaved, Boolean isLiked, String mediaType) {
        this.id = id;
        this.authorId = authorId;
        this.authorUsername = authorUsername;
        this.categoryId = categoryId;
        this.mediaBlobBase64 = mediaBlobBase64;
        this.externalMediaUrl = externalMediaUrl;
        this.creationYear = creationYear;
        this.datePosted = datePosted;
        this.description = description;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.isOwnPost = isOwnPost;
        this.isSaved = isSaved;
        this.isLiked = isLiked;
        this.mediaType = mediaType;
    }

    public static PostDTO PostToPostDTO(Post post, Long currentUserId, String authorUsername) {
        String base64Media = null;
        if (post.getMediaBlob() != null) {
            try {
                Blob blob = post.getMediaBlob();
                byte[] bytes = blob.getBytes(1, (int) blob.length());
                String mimeType = post.getMediaType().equals("video") ? "video/mp4" : "image/jpeg";
                base64Media = "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(bytes);
            } catch (SQLException e) {
                System.err.println("SQLException in media blob conversion: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }

        PostDAO postDAO = new PostDAO();
        boolean isSaved = postDAO.isSavedPostById(currentUserId, post.getId());
        boolean isLiked = postDAO.isPostLiked(currentUserId, post.getId());

        return PostDTO.builder()
                .id(post.getId())
                .authorId(post.getAuthorId())
                .authorUsername(authorUsername)
                .categoryId(post.getCategoryId())
                .mediaBlobBase64(base64Media)
                .externalMediaUrl(post.getExternalMediaUrl())
                .creationYear(post.getCreationYear())
                .datePosted(post.getDatePosted())
                .description(post.getDescription())
                .likeCount(post.getLikesCount())
                .commentCount(post.getCommentsCount())
                .isOwnPost(post.getAuthorId().equals(currentUserId))
                .isSaved(isSaved)
                .isLiked(isLiked)
                .mediaType(post.getMediaType())
                .build();
    }
}