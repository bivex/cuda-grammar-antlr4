# CUDA ANTLR4 Grammar

This repository contains an ANTLR4 grammar for parsing CUDA C++ code. It is built as an extension to the standard ANTLR4 C++14 grammar, adding support for CUDA-specific keywords and syntax.

## Features Supported
- **CUDA Keywords:** `__global__`, `__device__`, `__host__`, `__constant__`, `__shared__`, `__managed__`, `__restrict__`.
- **Kernel Launch Syntax:** Properly parses the execution configuration syntax: `kernel_name<<<blocks, threads, shared_mem, stream>>>(args...)`.
- **Full C++14 Support:** Inherits all the standard C++14 parsing capabilities (classes, templates, lambdas, etc.) from the official ANTLR4 grammars repository.

## Prerequisites
- Java (JDK 8 or higher)
- Bash (for the build script)

## Setup & Build

A `build.sh` script is provided to automate the setup process. It will:
1. Download the `antlr-4.13.1-complete.jar` (if not already present).
2. Generate the Java lexer and parser classes from the `.g4` files.
3. Compile all Java source files.
4. Run a test parser against a sample CUDA code.

Simply run:

```bash
chmod +x build.sh
./build.sh
```

## Testing a Custom File

You can test the parser against your own `.cu` files using the `build.sh` script:

```bash
./build.sh path/to/your/file.cu
```

If no file is provided, it falls back to a hardcoded string of a basic matrix addition kernel in `Main.java`.

## Files
- `CUDALexer.g4`: The lexer rules, including CUDA-specific tokens.
- `CUDAParser.g4`: The parser rules, including rules for kernel execution configurations.
- `CUDAParserBase.java`: A helper class required for resolving C++ specific parsing ambiguities.
- `Main.java`: The main testing application that reads a `.cu` file (or string) and prints the Abstract Syntax Tree (AST).
- `matrixMul.cu`: A sample realistic CUDA file for testing.

## Based On
This grammar is derived from the official [ANTLR4 grammars-v4 C++14 grammar](https://github.com/antlr/grammars-v4/tree/master/cpp).
