/*
Afonso Goncalves | ist189399

Manage files
*/

#include "functions.h"

void resetBuffer(){
	/*Clean buffer*/
	while(getchar() != '\n');
}

Sparse readFromFile(char filename[]){
	/*Reads matrix from file*/
	Sparse Matrix;
	FILE *input;
	char line[MAXLINE];
	unsigned long row, col;
	double value;

	Matrix = createEmptyMatrix();

	input = fopen(filename, "r");

	if(input != NULL){
		while(fgets(line, MAXLINE, input)){
			if(sscanf(line, "[%lu;%lu]=%lf", &row, &col, &value) == 3){
				Matrix = addToMatrix(Matrix, row, col, value);
			}
		}
		fclose(input);
	}

	return Matrix;
}

void writeFile(Sparse Matrix, char filename[]){
	/*Saves matrix into file*/
	FILE *out;
	Element el;
	int i;

	out = fopen(filename, "w");
	if(out != NULL){
		for(i = 0; i < Matrix.len; i++){
			el = Matrix.mat[i];
			fprintf(out, "[%lu;%lu]=%.3f\n", el.row, el.col, el.value);
		}
		fclose(out);
	}
}
