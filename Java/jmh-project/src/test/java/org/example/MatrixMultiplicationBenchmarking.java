package org.example;

import org.openjdk.jmh.annotations.*;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class MatrixMultiplicationBenchmarking {

	@State(Scope.Thread)
	public static class Operands {
		private final int n = 2048; // Rozmiar macierzy

		public double[][] a = new double[n][n];
		public double[][] b = new double[n][n];

		@Setup(Level.Trial)
		public void setup() {
			Random random = new Random();
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					a[i][j] = random.nextDouble();
					b[i][j] = random.nextDouble();
				}
			}
		}
	}

	@State(Scope.Thread)
	public static class ResourceUsage {
		long totalMemoryUsed = 0;
		long totalCpuTimeUsed = 0;
		int iterations = 0;

		// Gathering data
		public void accumulate(long memoryUsed, long cpuTimeUsed) {
			totalMemoryUsed += memoryUsed;
			totalCpuTimeUsed += cpuTimeUsed;
			iterations++;
		}

		// Counting and display
		@TearDown(Level.Trial)
		public void printAverages() {
			if (iterations > 0) {
				double averageMemoryUsage = (double) totalMemoryUsed / iterations;
				double averageCpuTimeUsed = (double) totalCpuTimeUsed / iterations;

				System.out.println("Average memory usage: " + averageMemoryUsage + " bytes");
				System.out.println("Average CPU time usage: " + averageCpuTimeUsed + " ms");
			}
		}
	}

	@Benchmark
	@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
	@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
	public void multiplication(Operands operands, ResourceUsage resourceUsage) {
		// BEGGINING SETS
		long beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

		// BEGGINING SETS
		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		long beforeCpuTime = threadMXBean.getCurrentThreadCpuTime();

		// MATRIX MULTIPLICATION
		new MatrixMultiplication().execute(operands.a, operands.b);

		// END MEMORY
		long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

		// END PROCCESOR
		long afterCpuTime = threadMXBean.getCurrentThreadCpuTime();

		// MEMORY AND CPU TIME
		long memoryUsed = afterUsedMem - beforeUsedMem;
		long cpuTimeUsed = (afterCpuTime - beforeCpuTime) / 1_000_000; // Counting to miliseconds

		// Gathered data
		resourceUsage.accumulate(memoryUsed, cpuTimeUsed);
	}

	public static void main(String[] args) throws Exception {
		org.openjdk.jmh.runner.Runner runner = new org.openjdk.jmh.runner.Runner(
				new org.openjdk.jmh.runner.options.OptionsBuilder()
						.include(MatrixMultiplicationBenchmarking.class.getSimpleName())
						.forks(1)
						.build()
		);
		runner.run();
	}
}

class MatrixMultiplication {
	public void execute(double[][] a, double[][] b) {
		int n = a.length;
		double[][] result = new double[n][n];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				result[i][j] = 0;
				for (int k = 0; k < n; k++) {
					result[i][j] += a[i][k] * b[k][j];
				}
			}
		}
	}
}
