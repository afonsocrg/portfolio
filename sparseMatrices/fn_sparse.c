/*
Afonso Goncalves | ist189399

Manage Sparse structure
*/

#include "functions.h"

Sparse createEmptyMatrix(){
	/*return empty default matrix*/
	Sparse Matrix;

	Matrix.len = 0;
	Matrix.mini = Matrix.minj = 0;
	Matrix.maxi = Matrix.maxj = 0;
	Matrix.zero = 0;
	Matrix.size = 0;

	return Matrix;
}

Sparse MatrixCopy(Sparse origin){
	/*returns copy of the argument*/
	Sparse out;
	Element elem;
	int i;

	out.len = origin.len;
	out.mini = origin.mini;
	out.maxi = origin.maxi;
	out.minj = origin.minj;
	out.maxj = origin.maxj;
	out.zero = origin.zero;
	out.size = origin.size;

	for(i = 0; i < out.len; i++){
		elem.row = origin.mat[i].row;
		elem.col = origin.mat[i].col;
		elem.value = origin.mat[i].value;
		out.mat[i] = elem;
	}

	return out;
}

Sparse addToMatrix(Sparse Matrix, unsigned long row, unsigned long col, double value){
	/*Adds element into matrix (if valid position) */
	int newPos;
	char used;
	Element newEl;
	
	/*check valid arguments*/
	if( row < 0 || col < 0){
		return Matrix;
	}

	/*check free position*/
	used = 0, newPos = -1;
	while(!used && ++newPos < Matrix.len){
		if (Matrix.mat[newPos].row == row && Matrix.mat[newPos].col == col){
			used = 1;
		}
	}

	/*create new element*/
	newEl.row = row;
	newEl.col = col;
	newEl.value = value;

	if(used){
		if(value != Matrix.zero){
			/*used position & not zero -> Replace element*/
			Matrix.mat[newPos] = newEl;
		} else {
			/*used position & zero -> Remove element*/
			shiftRight(Matrix.mat, newPos+1, Matrix.len);
			Matrix.len--;
			Matrix = setLimits(Matrix);
		}
	} else {
		if(value != Matrix.zero){
			/*free position & not zero -> Add element*/
			Matrix.mat[newPos] = newEl;
			Matrix.len++;
			Matrix = setLimits(Matrix);
		}
	}
	return Matrix;
}

Sparse changeZero(Sparse Matrix, double newVal){
	/*sets new zero; removes new zeros from matrix*/
	int i;

	Matrix.zero = newVal;

	for(i = 0; i<Matrix.len; i++){
		if(Matrix.mat[i].value == Matrix.zero){
			shiftRight(Matrix.mat, i+1, Matrix.len);
			Matrix.len--;
			/*rechecks i position (has new element)*/
			i--;
		}
	}
	/*updates matrix limits and size*/
	return setLimits(Matrix);
}

Sparse setLimits(Sparse Matrix){
	/*Set matrix size and row and col limits*/
	int i;

	/*default cases*/
	if(Matrix.len == 0){
		Matrix.maxi = Matrix.mini = 0;
		Matrix.maxj = Matrix.minj = 0;
	} else {
		Matrix.maxi = Matrix.mini = Matrix.mat[0].row;
		Matrix.maxj = Matrix.minj = Matrix.mat[0].col;
	}


	/*search for limits*/
	for(i = 0; i < Matrix.len; i++){
		if(Matrix.mat[i].row < Matrix.mini) Matrix.mini = Matrix.mat[i].row;
		else if(Matrix.mat[i].row > Matrix.maxi) Matrix.maxi = Matrix.mat[i].row;
		if(Matrix.mat[i].col < Matrix.minj) Matrix.minj = Matrix.mat[i].col;
		else if(Matrix.mat[i].col > Matrix.maxj) Matrix.maxj = Matrix.mat[i].col;
	}

	/*set size*/
	Matrix.size = (Matrix.maxi - Matrix.mini + 1) * (Matrix.maxj - Matrix.minj + 1);

	return Matrix;
}

Sparse sortSparse(Sparse Matrix, char colFlag){
	/*Orders matrix*/
	/*Keep abstraction barriers*/
	myquicksort(Matrix.mat, 0, Matrix.len-1, colFlag);
	return Matrix;
}