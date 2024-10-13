import numpy as np
import pytest
import psutil


def matrix_multiply(a, b):
    n = len(a)
    c = [[0] * n for _ in range(n)]
    for i in range(n):
        for j in range(n):
            for k in range(n):
                c[i][j] += a[i][k] * b[k][j]
    return c


@pytest.fixture
def setup_matrices():
    size = 1024
    A = np.random.rand(size, size)
    B = np.random.rand(size, size)
    return A, B


@pytest.mark.benchmark(min_rounds=5)
def test_matrix_multiply(benchmark, setup_matrices):
    A, B = setup_matrices
    result = benchmark(matrix_multiply, A, B)


    print("CPU usage:", psutil.cpu_percent(), "%")
    print("Memory usage:", psutil.virtual_memory().percent, "%")
    assert result is not None



