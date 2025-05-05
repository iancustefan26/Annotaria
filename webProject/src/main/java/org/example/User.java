package org.example;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class User{
    private int id;
    private String username;
    private String passwordHash;
    private String email;
    private Timestamp createdAt;
}
