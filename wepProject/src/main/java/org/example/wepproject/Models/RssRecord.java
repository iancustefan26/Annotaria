package org.example.wepproject.Models;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class RssRecord {
    private String title;
    private String link;
    private String description;
    private Timestamp pubDate;
    private Integer guid;
}
