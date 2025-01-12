package com.matrix;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        HazelcastInstance instance = Hazelcast.newHazelcastInstance();

        // Określamy rozmiary macierzy i liczby wątków do testów
        int[] matrixSizes = {10, 50, 100, 256, 512, 1024, 2048, 4096};
        int[] threadCounts = {2, 5, 10, 20};

        List<TestResult> results = new ArrayList<>();

        for (int size : matrixSizes) {
            for (int threads : threadCounts) {
                System.out.println("Testing size: " + size + ", threads: " + threads);

                try {
                    // Generowanie macierzy
                    double[][] matrixOne = MatrixBuilder.create(size, size);
                    double[][] matrixTwo = MatrixBuilder.create(size, size);

                    // Pomiar czasu
                    long startTimestamp = System.currentTimeMillis();
                    // Wywołanie metody z klasy MatrixCalculator (jeśli używamy wielowątkowości i MapReduce)
                    MatrixCalculator.multiplyUsingThreads(matrixOne, matrixTwo, instance, threads);  // Tutaj wykorzystujemy wątki
                    long endTimestamp = System.currentTimeMillis();

                    // Pomiar wykorzystania pamięci
                    long memoryUsage = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);

                    // Zapisanie wyników
                    results.add(new TestResult(size, threads, (endTimestamp - startTimestamp), memoryUsage));

                } catch (OutOfMemoryError e) {
                    System.err.println("Error: Matrix size " + size + " with " + threads + " threads caused an OutOfMemoryError.");
                    results.add(new TestResult(size, threads, -1, -1));  // Oznaczamy test jako nieudany
                } catch (Exception e) {
                    System.err.println("Error: An exception occurred during the test with matrix size " + size + " and " + threads + " threads.");
                    e.printStackTrace();
                    results.add(new TestResult(size, threads, -1, -1));  // Oznaczamy test jako nieudany
                }
            }
        }

        instance.shutdown();

        // Oczekiwanie na zakończenie wszystkich zadań przed wyświetleniem wyników
        synchronized (results) {
            // Wyświetlenie tabeli wyników
            printResultsTable(results);
        }
    }

    private static void printResultsTable(List<TestResult> results) {
        System.out.println("\nTest Results:");
        System.out.printf("%-10s %-10s %-15s %-15s%n", "MatrixSize", "Threads", "ExecutionTime(ms)", "MemoryUsage(MB)");
        System.out.println("---------------------------------------------------------------");
        for (TestResult result : results) {
            if (result.getExecutionTime() == -1) {
                // Jeśli test się nie powiódł (OutOfMemoryError lub wyjątek)
                System.out.printf("%-10d %-10d %-15s %-15s%n",
                        result.getMatrixSize(),
                        result.getThreads(),
                        "Failed",
                        "Failed");
            } else {
                System.out.printf("%-10d %-10d %-15d %-15d%n",
                        result.getMatrixSize(),
                        result.getThreads(),
                        result.getExecutionTime(),
                        result.getMemoryUsage());
            }
        }
    }
}
