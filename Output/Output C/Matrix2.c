#include <stdio.h>
#include <stdlib.h>
#include <sys/timeb.h>
#include <windows.h>
#include <psapi.h>
/*
#define n 2048
double a[n][n];
double b[n][n];
double c[n][n];

// Function to get the current memory usage in bytes
SIZE_T getMemoryUsage() {
    PROCESS_MEMORY_COUNTERS pmc;
    GetProcessMemoryInfo(GetCurrentProcess(), &pmc, sizeof(pmc));
    return pmc.WorkingSetSize; // Returns memory usage in bytes
}

// Function to get total physical memory in the system
SIZE_T getTotalPhysicalMemory() {
    MEMORYSTATUSEX statex;
    statex.dwLength = sizeof(statex);
    GlobalMemoryStatusEx(&statex);
    return statex.ullTotalPhys; // Returns total physical memory
}

// Function to get CPU time in milliseconds
double getCPUTimeInMs() {
    FILETIME ftCreation, ftExit, ftKernel, ftUser;
    GetProcessTimes(GetCurrentProcess(), &ftCreation, &ftExit, &ftKernel, &ftUser);

    ULARGE_INTEGER kernelTime, userTime;
    kernelTime.LowPart = ftKernel.dwLowDateTime;
    kernelTime.HighPart = ftKernel.dwHighDateTime;
    userTime.LowPart = ftUser.dwLowDateTime;
    userTime.HighPart = ftUser.dwHighDateTime;

    // Return total CPU time (Kernel + User) in milliseconds
    return (kernelTime.QuadPart + userTime.QuadPart) / 10000.0; // Convert to milliseconds
}

int main() {
    // Initialize matrices
    for (int i = 0; i < n; ++i) {
        for (int j = 0; j < n; ++j) {
            a[i][j] = (double)rand() / RAND_MAX;
            b[i][j] = (double)rand() / RAND_MAX;
            c[i][j] = 0;
        }
    }

    // Perform the operation 10 times, collecting data only in the last iteration
    for (int iter = 0; iter < 15; ++iter) {
        if (iter == 14) { // Only collect data in the last iteration
            // Measure memory usage before operation
            SIZE_T memoryBefore = getMemoryUsage();
            SIZE_T totalMemory = getTotalPhysicalMemory(); // Get total physical memory

            // Start measuring time
            struct timeb start, stop;
            ftime(&start);

            // Perform matrix multiplication
            for (int i = 0; i < n; ++i) {
                for (int j = 0; j < n; ++j) {
                    for (int k = 0; k < n; ++k) {
                        c[i][j] += a[i][k] * b[k][j];
                    }
                }
            }

            // Stop measuring time
            ftime(&stop);
            double diff = (double)(stop.time - start.time) + (double)(stop.millitm - start.millitm) / 1000.0; // Total execution time in seconds
            double execTimeMs = diff * 1000.0; // Convert execution time to milliseconds
            printf("Execution time: %0.10f seconds\n", diff);

            // Measure memory usage after operation
            SIZE_T memoryAfter = getMemoryUsage();

            // Calculate memory used by this operation
            SIZE_T memoryUsed = memoryAfter - memoryBefore;

            // Print only the memory difference
            printf("Memory used by operation: %zu bytes\n", memoryUsed);
            printf("Memory used by operation: %0.10f MB\n", memoryUsed / (1024.0 * 1024.0));

            // Calculate memory usage percentage
            double memoryUsagePercent = (double)memoryUsed / totalMemory * 100.0; // Memory usage in percent
            printf("Memory used by operation: %0.10f%% of total physical memory\n", memoryUsagePercent);

            // Get CPU time in milliseconds
            double cpuTimeMs = getCPUTimeInMs();
            printf("CPU time: %0.10f ms\n", cpuTimeMs); // Display with 10 decimal places

            // Calculate CPU usage percentage
            double cpuUsagePercent = (cpuTimeMs / execTimeMs) * 100; // Correct usage calculation
            printf("CPU usage: %0.10f%%\n", cpuUsagePercent);
        } else {
            // Reset the result matrix for other iterations
            for (int i = 0; i < n; ++i) {
                for (int j = 0; j < n; ++j) {
                    c[i][j] = 0; // Reset the matrix for the next iteration
                }
            }
        }
    }

    return 0;
}
*/