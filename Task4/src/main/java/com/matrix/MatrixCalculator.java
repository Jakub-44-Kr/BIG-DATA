package com.matrix;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MatrixCalculator {

    // Standardowa metoda mnożenia (jednowątkowe)
    public static double[][] multiply(double[][] matrixA, double[][] matrixB) {
        int rowsA = matrixA.length;
        int colsA = matrixA[0].length;
        int colsB = matrixB[0].length;

        double[][] result = new double[rowsA][colsB];

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                for (int k = 0; k < colsA; k++) {
                    result[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }

        return result;
    }

    // Metoda MapReduce z Hazelcast
    public static double[][] multiplyUsingMapReduce(double[][] matrixA, double[][] matrixB, HazelcastInstance hazelcastInstance, int numberOfThreads) {
        int rowsA = matrixA.length;
        int colsA = matrixA[0].length;
        int colsB = matrixB[0].length;

        double[][] result = new double[rowsA][colsB];
        IExecutorService executorService = hazelcastInstance.getExecutorService("default");

        List<Callable<double[]>> tasks = new ArrayList<>();
        for (int i = 0; i < rowsA; i++) {
            final int row = i;
            tasks.add(new MapTask(row, matrixA, matrixB));
        }

        try {
            List<Future<double[]>> futures = executorService.invokeAll(tasks);

            // Zbieranie wyników z futures
            for (int i = 0; i < rowsA; i++) {
                result[i] = futures.get(i).get();
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return result;
    }

    // Klasa MapTask: Oblicza pojedynczy wiersz wynikowej macierzy
    public static class MapTask implements Callable<double[]>, Serializable {
        private final int row;
        private final double[][] matrixA;
        private final double[][] matrixB;

        public MapTask(int row, double[][] matrixA, double[][] matrixB) {
            this.row = row;
            this.matrixA = matrixA;
            this.matrixB = matrixB;
        }

        @Override
        public double[] call() {
            int colsB = matrixB[0].length;
            double[] resultRow = new double[colsB];

            // Obliczanie wiersza dla wynikowej macierzy
            for (int j = 0; j < colsB; j++) {
                for (int k = 0; k < matrixA[0].length; k++) {
                    resultRow[j] += matrixA[row][k] * matrixB[k][j];
                }
            }
            return resultRow;
        }
    }

    // Metoda równoległa, która dzieli pracę na wątki (z wykorzystaniem Hazelcast)
    public static double[][] multiplyUsingThreads(double[][] matrixA, double[][] matrixB, HazelcastInstance hazelcastInstance, int numberOfThreads) {
        int rowsA = matrixA.length;
        int colsA = matrixA[0].length;
        int colsB = matrixB[0].length;

        double[][] result = new double[rowsA][colsB];
        IExecutorService executorService = hazelcastInstance.getExecutorService("default");

        List<Future<double[]>> futures = new ArrayList<>();

        // Dzielimy pracę na różne wątki
        for (int i = 0; i < rowsA; i++) {
            final int row = i;
            futures.add(executorService.submit(new RowMultiplicationTask(row, matrixA, matrixB)));
        }

        // Zbieramy wyniki
        for (int i = 0; i < rowsA; i++) {
            try {
                result[i] = futures.get(i).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    // Klasa RowMultiplicationTask: Oblicza wiersz macierzy
    public static class RowMultiplicationTask implements Callable<double[]>, Serializable {
        private final int row;
        private final double[][] matrixA;
        private final double[][] matrixB;

        public RowMultiplicationTask(int row, double[][] matrixA, double[][] matrixB) {
            this.row = row;
            this.matrixA = matrixA;
            this.matrixB = matrixB;
        }

        @Override
        public double[] call() {
            int colsB = matrixB[0].length;
            double[] resultRow = new double[colsB];
            for (int j = 0; j < colsB; j++) {
                for (int k = 0; k < matrixA[0].length; k++) {
                    resultRow[j] += matrixA[row][k] * matrixB[k][j];
                }
            }
            return resultRow;
        }
    }

    // Funkcja główna do przeprowadzania testów
    public static void testMatrixMultiplication(HazelcastInstance instance, int[] matrixSizes, int[] threadCounts) {
        // Wypisanie nagłówka tabeli
        System.out.printf("%-10s %-10s %-15s %-15s%n", "MatrixSize", "Threads", "ExecutionTime(ms)", "MemoryUsage(MB)");
        System.out.println("---------------------------------------------------------------");

        // Przeprowadzanie testów dla różnych rozmiarów macierzy i ilości wątków
        for (int size : matrixSizes) {
            for (int threads : threadCounts) {
                System.out.println("Testing size: " + size + ", threads: " + threads);

                try {
                    // Generowanie macierzy
                    double[][] matrixOne = MatrixBuilder.create(size, size);
                    double[][] matrixTwo = MatrixBuilder.create(size, size);

                    long startTimestamp = System.currentTimeMillis();
                    double[][] result = multiplyUsingMapReduce(matrixOne, matrixTwo, instance, threads);
                    long endTimestamp = System.currentTimeMillis();

                    // Mierzymy zużycie pamięci
                    long memoryUsage = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);

                    // Wypisanie wyników
                    System.out.printf("%-10d %-10d %-15d %-15d%n", size, threads, (endTimestamp - startTimestamp), memoryUsage);

                } catch (OutOfMemoryError e) {
                    System.err.println("Error: Matrix size " + size + " with " + threads + " threads caused an OutOfMemoryError.");
                    System.out.printf("%-10d %-10d %-15s %-15s%n", size, threads, "Failed", "Failed");
                } catch (Exception e) {
                    System.err.println("Error: An exception occurred during the test with matrix size " + size + " and " + threads + " threads.");
                    e.printStackTrace();
                    System.out.printf("%-10d %-10d %-15s %-15s%n", size, threads, "Failed", "Failed");
                }
            }
        }
    }
}