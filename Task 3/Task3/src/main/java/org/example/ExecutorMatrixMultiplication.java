package org.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorMatrixMultiplication {
    public static double[][] multiply(double[][] matrixA, double[][] matrixB, int numThreads) throws InterruptedException {
        int n = matrixA.length;
        double[][] result = new double[n][n];

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        int rowsPerThread = n / numThreads;

        for (int t = 0; t < numThreads; t++) {
            int startRow = t * rowsPerThread;
            int endRow = (t == numThreads - 1) ? n : startRow + rowsPerThread;

            executor.submit(() -> {
                for (int i = startRow; i < endRow; i++) {
                    for (int j = 0; j < n; j++) {
                        for (int k = 0; k < n; k++) {
                            result[i][j] += matrixA[i][k] * matrixB[k][j];
                        }
                    }
                }
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            Thread.sleep(10);
        }

        return result;
    }
}
