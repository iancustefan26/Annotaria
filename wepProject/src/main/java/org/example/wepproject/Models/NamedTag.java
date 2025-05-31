package org.example.wepproject.Models;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode


public class NamedTag {
    private Long id;
    private String name;
}
