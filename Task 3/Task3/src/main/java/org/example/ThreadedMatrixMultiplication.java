package org.example;

public class ThreadedMatrixMultiplication {
    public static double[][] multiply(double[][] matrixA, double[][] matrixB, int numThreads) throws InterruptedException {
        int n = matrixA.length;
        double[][] result = new double[n][n];

        Thread[] threads = new Thread[numThreads];
        int rowsPerThread = n / numThreads;

        for (int t = 0; t < numThreads; t++) {
            int startRow = t * rowsPerThread;
            int endRow = (t == numThreads - 1) ? n : startRow + rowsPerThread;
            threads[t] = new Thread(() -> {
                for (int i = startRow; i < endRow; i++) {
                    for (int j = 0; j < n; j++) {
                        for (int k = 0; k < n; k++) {
                            result[i][j] += matrixA[i][k] * matrixB[k][j];
                        }
                    }
                }
            });
            threads[t].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        return result;
    }
}