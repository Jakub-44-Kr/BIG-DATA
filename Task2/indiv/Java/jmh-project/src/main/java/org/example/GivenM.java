package org.example;

import org.openjdk.jmh.annotations.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 2)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@Timeout(time = 300, timeUnit = TimeUnit.SECONDS)
public class GivenM {

    private SparseMatrix sparseMatrixA;
    private SparseMatrix sparseMatrixB;

    @Param({"D:/Studia/BigData/indiv/Java/matrix form site/mc2depi.mtx"}) //I do not know why but i couldnt give good path that would work on your pc as it should be ../../../../../path.mtx but it do not work and i've wasted 3 hours on that and i do not want to do it forgive me pls
    private String matrixFilePath;

    @Setup(Level.Trial)
    public void setUp() throws IOException {
        System.out.println("Current working directory: " + System.getProperty("user.dir"));
        sparseMatrixA = loadMatrixFromFile(matrixFilePath);
        sparseMatrixB = sparseMatrixA;
    }

    private SparseMatrix loadMatrixFromFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        int rows = 0, cols = 0;
        SparseMatrix sparseMatrix = null;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("%")) {
                continue;
            }
            String[] parts = line.trim().split("\\s+");
            if (sparseMatrix == null) {
                rows = Integer.parseInt(parts[0]);
                cols = Integer.parseInt(parts[1]);
                sparseMatrix = new SparseMatrix(rows, cols);
            } else {
                int row = Integer.parseInt(parts[0]) - 1;
                int col = Integer.parseInt(parts[1]) - 1;
                double value = Double.parseDouble(parts[2]);
                sparseMatrix.set(row, col, value);
            }
        }
        reader.close();
        return sparseMatrix;
    }

    @Benchmark
    public SparseMatrix multiplication() {
        return sparseMultiply(sparseMatrixA, sparseMatrixB);
    }

    private SparseMatrix sparseMultiply(SparseMatrix a, SparseMatrix b) {
        if (a.getCols() != b.getRows()) {
            throw new IllegalArgumentException("Matrix dimensions do not match for multiplication.");
        }

        SparseMatrix result = new SparseMatrix(a.getRows(), b.getCols());
        for (int i : a.getData().keySet()) {
            for (int k : a.getData().get(i).keySet()) {
                double aVal = a.get(i, k);
                if (aVal != 0) {
                    for (int j : b.getData().getOrDefault(k, Collections.emptyMap()).keySet()) {
                        double bVal = b.get(k, j);
                        if (bVal != 0) {
                            double sum = result.get(i, j) + aVal * bVal;
                            result.set(i, j, sum);
                        }
                    }
                }
            }
        }
        return result;
    }

    public static class SparseMatrix {
        private final int rows, cols;
        private final Map<Integer, Map<Integer, Double>> data = new HashMap<>();

        public SparseMatrix(int rows, int cols) {
            this.rows = rows;
            this.cols = cols;
        }

        public void set(int row, int col, double value) {
            data.computeIfAbsent(row, k -> new HashMap<>()).put(col, value);
        }

        public double get(int row, int col) {
            return data.getOrDefault(row, Collections.emptyMap()).getOrDefault(col, 0.0);
        }

        public int getRows() {
            return rows;
        }

        public int getCols() {
            return cols;
        }

        public Map<Integer, Map<Integer, Double>> getData() {
            return data;
        }
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.runner.Runner runner = new org.openjdk.jmh.runner.Runner(
                new org.openjdk.jmh.runner.options.OptionsBuilder()
                        .include(GivenM.class.getSimpleName())
                        .forks(2)
                        .build()
        );
        runner.run();
    }
}