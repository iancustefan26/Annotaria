package org.example.wepproject.Helpers.FeedAlgorithmic;

import org.example.wepproject.Models.MatrixCell;

import java.util.List;

public class MatrixConvertor {
    public static float[][] toMatrix(List<MatrixCell> cells) {
        // last line are the ids of the post (n)(0) = id of the first post.. and so on
        int maxRow = 0, maxCol = 0;
        for (MatrixCell cell : cells) {
            if (cell.getRowIndex() > maxRow) maxRow = cell.getRowIndex();
            if (cell.getColIndex() > maxCol) maxCol = cell.getColIndex();
        }

        float[][] matrix = new float[maxRow][maxCol];
        for (MatrixCell cell : cells) {
            matrix[cell.getRowIndex() - 1][cell.getColIndex() - 1] = cell.getValue();
        }

        return matrix;
    }
    public static void printMatrix(float[][] matrix) {
        for(int i = 0; i < matrix.length; i++) {
            for(int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}
