package org.example;

import org.openjdk.jmh.annotations.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class MatrixMultiplicationBenchmarkingBlocks {

    @State(Scope.Thread)
    public static class Operands {
        private final int size = 1024; // Rozmiar macierzy
        private final int blockSize = 64; // Rozmiar bloku
        private final double density = 0.9; // Gęstość macierzy (0.0 do 1.0)
        public double[][] a = new double[size][size];
        public double[][] b = new double[size][size];
        public double[][] c = new double[size][size];

        @Setup(Level.Trial)
        public void setup() {
            Random random = new Random();
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    a[i][j] = random.nextDouble() < density ? random.nextDouble() : 0;
                    b[i][j] = random.nextDouble() < density ? random.nextDouble() : 0;
                    c[i][j] = 0; // Inicjalizujemy wynik
                }
            }
        }

        public void blockMultiply(double[][] a, double[][] b, double[][] c) {
            for (int i = 0; i < size; i += blockSize) {
                for (int j = 0; j < size; j += blockSize) {
                    for (int k = 0; k < size; k += blockSize) {
                        // Mnożenie bloków
                        for (int ii = i; ii < Math.min(i + blockSize, size); ii++) {
                            for (int jj = j; jj < Math.min(j + blockSize, size); jj++) {
                                for (int kk = k; kk < Math.min(k + blockSize, size); kk++) {
                                    c[ii][jj] += a[ii][kk] * b[kk][jj];
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Benchmark
    @Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    public void multiplication(Operands operands) {
        operands.blockMultiply(operands.a, operands.b, operands.c);
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.runner.Runner runner = new org.openjdk.jmh.runner.Runner(
                new org.openjdk.jmh.runner.options.OptionsBuilder()
                        .include(MatrixMultiplicationBenchmarkingBlocks.class.getSimpleName())
                        .forks(4)
                        .build()
        );
        runner.run();
    }
}
