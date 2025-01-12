package com.matrix;


public class TestResult {
    private final int matrixSize;
    private final int threads;
    private final long executionTime;
    private final long memoryUsage;

    public TestResult(int matrixSize, int threads, long executionTime, long memoryUsage) {
        this.matrixSize = matrixSize;
        this.threads = threads;
        this.executionTime = executionTime;
        this.memoryUsage = memoryUsage;
    }

    public int getMatrixSize() {
        return matrixSize;
    }

    public int getThreads() {
        return threads;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public long getMemoryUsage() {
        return memoryUsage;
    }
}
