import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        CharStream input;
        if (args.length > 0) {
            input = CharStreams.fromFileName(args[0]);
            System.out.println("Parsing file: " + args[0]);
        } else {
            input = CharStreams.fromString(
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
            System.out.println("Parsing default string...");
        }

        CUDALexer lexer = new CUDALexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CUDAParser parser = new CUDAParser(tokens);
        
        // Add error listener to capture syntax errors cleanly
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                System.err.println("line " + line + ":" + charPositionInLine + " " + msg);
            }
        });

        ParseTree tree = parser.translationUnit();
        System.out.println("Parsing complete.");
        if (parser.getNumberOfSyntaxErrors() == 0) {
            System.out.println("Successfully parsed the input!");
        } else {
            System.err.println("Encountered " + parser.getNumberOfSyntaxErrors() + " syntax errors.");
        }
    }
}
