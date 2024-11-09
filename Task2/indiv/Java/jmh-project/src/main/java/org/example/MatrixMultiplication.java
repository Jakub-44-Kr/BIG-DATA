package org.example;

import org.openjdk.jmh.annotations.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 2)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@Timeout(time = 300, timeUnit = TimeUnit.SECONDS)
public class MatrixMultiplication {

	private double[][] matrixA;
	private double[][] matrixB;

	@Param({"D:/Studia/BigData/indiv/Java/jmh-project/mc2depi.mtx"})
	private String matrixFilePath;

	@Setup(Level.Trial)
	public void setUp() throws IOException {
		matrixA = loadMatrixFromFile(matrixFilePath);
		matrixB = matrixA;
	}

	private double[][] loadMatrixFromFile(String filePath) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			int rows = 0, cols = 0;
			double[][] matrix = null;

			while ((line = reader.readLine()) != null) {
				if (line.startsWith("%")) continue;
				String[] parts = line.trim().split("\\s+");
				if (matrix == null) {
					rows = Integer.parseInt(parts[0]);
					cols = Integer.parseInt(parts[1]);
					matrix = new double[rows][cols];
				} else {
					int row = Integer.parseInt(parts[0]) - 1;
					int col = Integer.parseInt(parts[1]) - 1;
					double value = Double.parseDouble(parts[2]);
					matrix[row][col] = value;
				}
			}
			return matrix;
		}
	}

	@Benchmark
	public double[][] multiplication() {
		return multiply(matrixA, matrixB);
	}

	public double[][] multiply(double[][] a, double[][] b) {
		int n = a.length;
		double[][] c = new double[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				double sum = 0; // Initialize the sum for each c[i][j]
				for (int k = 0; k < n; k++) {
					sum += a[i][k] * b[k][j];
				}
				c[i][j] = sum; // Assign the computed sum to c[i][j]
			}
		}
		return c;
	}

	public static void main(String[] args) throws Exception {
		org.openjdk.jmh.runner.Runner runner = new org.openjdk.jmh.runner.Runner(
				new org.openjdk.jmh.runner.options.OptionsBuilder()
						.include(MatrixMultiplication.class.getSimpleName())
						.forks(2)
						.build()
		);
		runner.run();
	}
}
