package org.example;

import java.util.concurrent.atomic.AtomicReferenceArray;

public class AtomicMatrixMultiplication {
    public static double[][] multiply(double[][] matrixA, double[][] matrixB, int numThreads) throws InterruptedException {
        int n = matrixA.length;
        AtomicReferenceArray<double[]> result = new AtomicReferenceArray<>(n);
        for (int i = 0; i < n; i++) {
            result.set(i, new double[n]);
        }

        Thread[] threads = new Thread[numThreads];
        int rowsPerThread = n / numThreads;

        for (int t = 0; t < numThreads; t++) {
            int startRow = t * rowsPerThread;
            int endRow = (t == numThreads - 1) ? n : startRow + rowsPerThread;

            threads[t] = new Thread(() -> {
                for (int i = startRow; i < endRow; i++) {
                    double[] rowResult = result.get(i);
                    for (int j = 0; j < n; j++) {
                        for (int k = 0; k < n; k++) {
                            rowResult[j] += matrixA[i][k] * matrixB[k][j];
                        }
                    }
                }
            });
            threads[t].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        double[][] finalResult = new double[n][n];
        for (int i = 0; i < n; i++) {
            finalResult[i] = result.get(i);
        }

        return finalResult;
    }
}
