package org.example.wepproject.Models;

import lombok.*;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode

public class Category {
    private Long id;
    private String name;
}
