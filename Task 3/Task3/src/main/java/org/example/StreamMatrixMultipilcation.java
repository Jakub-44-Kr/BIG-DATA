package org.example;

import java.util.stream.IntStream;

public class StreamMatrixMultipilcation{
    public static double[][] multiply(double[][] matrixA, double[][] matrixB, int numThreads) {
        int n = matrixA.length;
        double[][] result = new double[n][n];

        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", String.valueOf(numThreads));

        IntStream.range(0, n).parallel().forEach(i -> {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    result[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        });

        return result;
    }
}
