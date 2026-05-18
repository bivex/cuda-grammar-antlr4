#include <stdio.h>
#include <cuda_runtime.h>

// Matrix multiplication kernel called by MatrixMul()
__global__ void MatrixMulKernel(float* C, float* A, float* B, int wA, int wB)
{
    // Block row and column
    int blockRow = blockIdx.y;
    int blockCol = blockIdx.x;

    // Thread row and column within Csub
    int row = threadIdx.y;
    int col = threadIdx.x;

    // Each thread block computes one sub-matrix Csub of C
    float Cvalue = 0;

    // Loop over all the sub-matrices of A and B that are required to compute Csub
    // Multiply each pair of sub-matrices together and accumulate the results
    for (int m = 0; m < (wA / 16); ++m) {
        // Shared memory for the sub-matrix of A
        __shared__ float As[16][16];
        // Shared memory for the sub-matrix of B
        __shared__ float Bs[16][16];

        // Load the matrices from device memory to shared memory; each thread loads one element of each matrix
        As[row][col] = A[row * wA + m * 16 + col];
        Bs[row][col] = B[(m * 16 + row) * wB + col];

        // Synchronize to make sure the matrices are loaded
        __syncthreads();

        // Multiply the two matrices together; each thread computes one element of the block sub-matrix
        for (int e = 0; e < 16; ++e) {
            Cvalue += As[row][e] * Bs[e][col];
        }

        // Synchronize to make sure that the preceding computation is done
        // before loading two new sub-matrices of A and B in the next iteration
        __syncthreads();
    }

    // Write the computed sub-matrix to device memory; each thread writes one element
    int c = wB * 16 * blockRow + 16 * blockCol;
    C[c + wB * row + col] = Cvalue;
}

// Host function to set up and run the kernel
int main(int argc, char** argv)
{
    int width = 256;
    int height = 256;
    int size = width * height * sizeof(float);

    float *h_A = (float*)malloc(size);
    float *h_B = (float*)malloc(size);
    float *h_C = (float*)malloc(size);

    for (int i = 0; i < width * height; ++i) {
        h_A[i] = 1.0f;
        h_B[i] = 1.0f;
    }

    float *d_A, *d_B, *d_C;
    cudaMalloc((void**)&d_A, size);
    cudaMalloc((void**)&d_B, size);
    cudaMalloc((void**)&d_C, size);

    cudaMemcpy(d_A, h_A, size, cudaMemcpyHostToDevice);
    cudaMemcpy(d_B, h_B, size, cudaMemcpyHostToDevice);

    dim3 threads(16, 16);
    dim3 blocks(width / threads.x, height / threads.y);

    // Test the CUDA parsing logic with kernel launch
    MatrixMulKernel<<<blocks, threads>>>(d_C, d_A, d_B, width, width);

    cudaDeviceSynchronize();

    cudaMemcpy(h_C, d_C, size, cudaMemcpyDeviceToHost);

    printf("Done. Result[0] = %f\n", h_C[0]);

    cudaFree(d_A);
    cudaFree(d_B);
    cudaFree(d_C);
    free(h_A);
    free(h_B);
    free(h_C);

    return 0;
}
