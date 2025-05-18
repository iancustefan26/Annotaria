package org.example.wepproject.Models;


import lombok.NoArgsConstructor;
import lombok.*;
import java.sql.Blob;
import java.sql.Timestamp;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Post {
    private Long id;
    private Long authorId;
    private Long categoryId;
    private Blob mediaBlob;
    private String externalMediaUrl;
    private Integer creationYear;
    private Timestamp datePosted;
    private String description;
    private int likesCount;
    private int commentsCount;

}
