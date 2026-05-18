import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Main {
    public static void main(String[] args) throws Exception {
        CharStream input = CharStreams.fromString(
            "__global__ void add(int n, float *x, float *y) {\n" +
            "    int index = threadIdx.x;\n" +
            "    int stride = blockDim.x;\n" +
            "    for (int i = index; i < n; i += stride)\n" +
            "        y[i] = x[i] + y[i];\n" +
            "}\n" +
            "int main(void) {\n" +
            "    int N = 1<<20;\n" +
            "    float *x, *y;\n" +
            "    cudaMallocManaged(&x, N*sizeof(float));\n" +
            "    cudaMallocManaged(&y, N*sizeof(float));\n" +
            "    int blockSize = 256;\n" +
            "    int numBlocks = (N + blockSize - 1) / blockSize;\n" +
            "    add<<<numBlocks, blockSize>>>(N, x, y);\n" +
            "    cudaDeviceSynchronize();\n" +
            "    cudaFree(x);\n" +
            "    cudaFree(y);\n" +
            "    return 0;\n" +
            "}\n"
        );
        CUDALexer lexer = new CUDALexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CUDAParser parser = new CUDAParser(tokens);
        ParseTree tree = parser.translationUnit();
        System.out.println(tree.toStringTree(parser));
    }
}
