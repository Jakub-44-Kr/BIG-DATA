package org.example;

import org.openjdk.jmh.annotations.*;


import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(value = 1)
@Warmup(iterations = 1)
@Measurement(iterations = 5)
@Timeout(time = 180, timeUnit = TimeUnit.SECONDS)
public class MatrixMultiplicationBenchmarking {

    @Param({"1", "2", "4" ,"6" ,"8" , "10", "12", "20"})
    private int numThreads;


    @Param({"128", "512", "1024", "2048"})
    private int SIZE;

    private double[][] matrixA;
    private double[][] matrixB;


    @Setup(Level.Trial)
    public void setUp() {
        matrixA = generateRandomMatrix(SIZE);
        matrixB = generateRandomMatrix(SIZE);
    }


    private double[][] generateRandomMatrix(int size) {
        Random random = new Random();
        double[][] matrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = random.nextDouble();
            }
        }
        return matrix;
    }


    @Benchmark
    public double[][] Threaded() throws ExecutionException, InterruptedException {
        double[][] result = ThreadedMatrixMultiplication.multiply(matrixA, matrixB, numThreads);
        return result;
    }


    @Benchmark
    public double[][] Executor() throws ExecutionException, InterruptedException {
        double[][] result = ExecutorMatrixMultiplication.multiply(matrixA, matrixB, numThreads);
        return result;
    }


    @Benchmark
    public double[][] Stream() throws ExecutionException, InterruptedException {
        double[][] result = StreamMatrixMultipilcation.multiply(matrixA, matrixB, numThreads);
        return result;
    }

    @Benchmark
    public double[][] Semaphore() throws ExecutionException, InterruptedException {
        double[][] result = SemaphoreMatrixMultiplication.multiply(matrixA, matrixB, numThreads);
        return result;
    }



    @Benchmark
    public double[][] Atomic() throws ExecutionException, InterruptedException {
        double[][] result = AtomicMatrixMultiplication.multiply(matrixA, matrixB, numThreads);
        return result;
    }


}