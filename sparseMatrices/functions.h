#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "vars.h"

#define key(El, keyFlag) (keyFlag ? El.col : El.row)
#define swap(El1, El2) {Element tmp = El1; El1 = El2; El2 = tmp;}

#define left(k) (2*k + 1)
#define right(k) (2 * (k+1))

/*fn_files.c*/
void resetBuffer();
Sparse readFromFile(char filename[]);
void writeFile(Sparse Matrix, char filename[]);

/*fn_sparse.c*/
Sparse createEmptyMatrix();
Sparse MatrixCopy(Sparse origin);
Sparse addToMatrix(Sparse Matrix, unsigned long row, unsigned long col, double value);
Sparse changeZero(Sparse Matrix, double newVal);
Sparse setLimits(Sparse Matrix);
Sparse sortSparse(Sparse Matrix, char colFlag);

/*fn_array.c*/
char less(Element a, Element b, char keyFlag);
void myquicksort(Element a[], int l, int r, char keyFlag);
int mypartition(Element a[], int l, int r, char keyFlag);
void shiftRight(Element vec[], int l, int r);

/*fn_output.c*/
void printSparse(Sparse Matrix);
void printSection(Sparse Matrix, unsigned long number, char SecFlag);
void infoSparse(Sparse Matrix, char prntFlag);
void shortInfo(Sparse Matrix);
