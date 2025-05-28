package org.example.wepproject.Models;


import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class MatrixCell {
    private int rowIndex;
    private int colIndex;
    private float value;

}
