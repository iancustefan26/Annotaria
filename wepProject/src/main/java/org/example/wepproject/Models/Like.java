package org.example.wepproject.Models;


import lombok.*;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Like {
    private Long id;
    private Long userId;
    private Long postId;
}
