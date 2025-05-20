package org.example.wepproject.Models;


import lombok.*;

import java.sql.Timestamp;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode

public class Comment {
    private Long id;
    private Long userId;
    private Long postId;
    private String content;
    private Timestamp datePosted;
}
