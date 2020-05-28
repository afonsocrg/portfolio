# OG Compiler

This project consisted of developing a compiler for the OG language, which was specified by our teacher (specification available at specification.pdf).
This project was the most challenging and fun to develop, and it pushed me to my limits.

The first delivery consisted of creating the nodes that would represent the language structural elements. In the second one, we developed the lexical analysis and parsing, using flex and YACC (og_scanner.l and og_parser.y, respectively). This code produces an abstract syntactic tree (AST). We also developed the xml_writer, to represent the AST in XML.
In the final delivery, we created the code that would translate the abstract syntactic tree (AST) in postfix. This final stage also consisted of ensuring the semantic correctness of the language: It makes no sense to add a number with a string, for instance.

# Used Libraries / Dependencies
In this project, we used several modules that allowed us to focus on the main aspects of building a compiler. Below there is a small description of each one's goal.
 * flex 2.6.4: Lexical analyser generator;
 * GNU Bison 3.6.2: Parser generator;
 * libcdk15: Set of general classes and superclasses that represent the core elements used by most programming languages. Every OG element inherits from one of these classes.
 * pf2asm: postfix -> assembly compiler
 * librts: Base code for the compiler-generated programs
 * yasm 1.3.0: convert .asm into .o
 * GNU ld 2.34.50.20200508: link .o files


## OG Hello World!
In this section, I will help you write your first OG program. Before getting started, it's necessary to have everything set to get the executable file.

##### Step 1. Compile the compiler
The OG compiler depends on several modules, so we need to install them first.
Go to the libcdk directory
```
$ cd libcdk15-202004101316
```
Edit the `ROOT` variable in the Makefile to install the module in the desired directory; If the `ROOT` variable is empty, it will install the module in the root directory. Doing this may need privileged permissions.
To install the module, run the following commands:
```
$ make clean # just to make sure
$ make
$ make install
```
Repeat this process for the librts and pf2asm, using the same `ROOT` directory.
Go to the og directory and compile it:
```
$ cd og
$ make
```


##### Step 2. Write the .og program
A simple Hello World looks like this. I will save it as `hello.og` in the repo root directory.
```
public int og() {
	writeln "Hello World!";
	return 0;
}
```

##### Step 3.Compile and execute it
Run the following commands from the repo root directory:
```
$ ./og/og hello.og            # .og -> .asm
$ yasm -felf32 hello.asm      # .asm -> .o
$ ld -melf_i386 hello.o -L${LIB_DIRECTORY} -lrts   # link the .o file
$ ./a.out                     # execute it
```
Where `LIB_DIRECTORY` is the directory where the librts is installed. (`${ROOT}/usr/lib`). You can copy this value from the last line of the output of the `make` command.
