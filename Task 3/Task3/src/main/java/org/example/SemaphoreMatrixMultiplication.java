package org.example;

import java.util.concurrent.Semaphore;

public class SemaphoreMatrixMultiplication {
    public static double[][] multiply(double[][] matrixA, double[][] matrixB, int numThreads) throws InterruptedException {
        int n = matrixA.length;
        double[][] result = new double[n][n];

        Semaphore semaphore = new Semaphore(numThreads);
        Thread[] threads = new Thread[n];

        for (int i = 0; i < n; i++) {
            int row = i;
            threads[i] = new Thread(() -> {
                try {
                    semaphore.acquire();
                    for (int j = 0; j < n; j++) {
                        for (int k = 0; k < n; k++) {
                            result[row][j] += matrixA[row][k] * matrixB[k][j];
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    semaphore.release();
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        return result;
    }
}
