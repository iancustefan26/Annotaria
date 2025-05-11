package org.example.wepproject.auth.Models;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;

}

