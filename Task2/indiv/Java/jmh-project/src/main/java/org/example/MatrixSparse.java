package org.example;

import org.openjdk.jmh.annotations.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class MatrixSparse {

    @State(Scope.Thread)
    public static class Operands {
        private final int n = 2048; // Rozmiar macierzy
        private final double density = 0.5; // Gęstość macierzy (0.0 do 1.0)
        public double[][] a = new double[n][n];
        public double[][] b = new double[n][n];
        public double[][] c = new double[n][n];

        @Setup(Level.Trial)
        public void setup() {
            Random random = new Random();
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    a[i][j] = random.nextDouble() < density ? random.nextDouble() : 0;
                    b[i][j] = random.nextDouble() < density ? random.nextDouble() : 0;
                    c[i][j] = 0; // Inicjalizujemy wynik
                }
            }
        }

        public void denseMultiply(double[][] a, double[][] b, double[][] c) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    double sum = 0;
                    for (int k = 0; k < n; k++) {
                        sum += a[i][k] * b[k][j];
                    }
                    c[i][j] = sum;
                }
            }
        }

        public void sparseMultiply(double[][] a, double[][] b, double[][] c) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    double sum = 0;
                    for (int k = 0; k < n; k++) {
                        if (a[i][k] != 0 && b[k][j] != 0) { // Checking 0
                            sum += a[i][k] * b[k][j];
                        }
                    }
                    c[i][j] = sum;
                }
            }
        }
    }

    @Benchmark
    @Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    public void multiplication(Operands operands) {

        if (operands.density < 0.5) {
            operands.sparseMultiply(operands.a, operands.b, operands.c);
        } else {
            operands.denseMultiply(operands.a, operands.b, operands.c);
        }
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.runner.Runner runner = new org.openjdk.jmh.runner.Runner(
                new org.openjdk.jmh.runner.options.OptionsBuilder()
                        .include(MatrixSparse.class.getSimpleName())
                        .forks(2)
                        .build()
        );
        runner.run();
    }
}
