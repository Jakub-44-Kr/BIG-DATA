package com.matrix;

import java.util.Random;

public class MatrixBuilder {
    public static double[][] create(int rows, int columns) {
        Random randGenerator = new Random();
        double[][] generatedMatrix = new double[rows][columns];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                generatedMatrix[row][col] = randGenerator.nextDouble() * 10;
            }
        }
        return generatedMatrix;
    }
}
