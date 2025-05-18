package org.example.wepproject.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PostDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("authorId")
    private Long authorId;

    @JsonProperty("categoryId")
    private Long categoryId;
    // converting blob in base 64 enconding
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

    @JsonProperty("likesCount")
    private Integer likesCount;

    @JsonProperty("commentsCount")
    private Integer commentsCount;

    public PostDTO(Long id, Long authorId, Long categoryId, String mediaBlobBase64, String externalMediaUrl,
                   Integer creationYear, java.sql.Timestamp datePosted, String description,
                   Integer likesCount, Integer commentsCount) {
        this.id = id;
        this.authorId = authorId;
        this.categoryId = categoryId;
        this.mediaBlobBase64 = mediaBlobBase64;
        this.externalMediaUrl = externalMediaUrl;
        this.creationYear = creationYear;
        this.datePosted = datePosted;
        this.description = description;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
    }
}
