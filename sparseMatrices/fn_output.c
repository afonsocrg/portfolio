/*
Afonso Goncalves | ist189399

Manage stdout
*/

#include "functions.h"

void printSparse(Sparse Matrix){
	/*print every element in Matrix Element array*/
	Element el;
	int i;

	if(Matrix.len == 0){
		printf("empty matrix\n");
		return;
	}

	for (i = 0; i < Matrix.len; i++){
		el = Matrix.mat[i];
		printf("[%lu;%lu]=%.3f\n", el.row, el.col, el.value);
	}
}

void printSection(Sparse Matrix, unsigned long number, char SecFlag){
	/*Print one line or one row, according to SecFlag*/
	/*SecFlag: 0 for line; 1 for col*/
	int i;
	unsigned long curr;
	char found;

	/*print column*/
	if(SecFlag){
		/*Check if empty*/
		found = 0;
		for(i = 0; !found && i < Matrix.len; i++){
			if(Matrix.mat[i].col == number){
				found = 1;
			}
		}
		if(!found){
			printf("empty column\n");
			return;
		}

		/*Search for every matching element in every row between min and max row*/
		for(curr = Matrix.mini; curr <= Matrix.maxi; curr++){
			i = -1, found = 0;
			while(!found && ++i < Matrix.len){
				if(Matrix.mat[i].row == curr && Matrix.mat[i].col == number){
					found = 1;
				}
			}

			printf("[%lu;%lu]=%.3f\n", curr, number, found ? Matrix.mat[i].value : Matrix.zero);
		}
	/*print row*/
	} else {
		/*Check if empty*/
		found = 0;
		for(i = 0; !found && i < Matrix.len; i++){
			if(Matrix.mat[i].row == number){
				found = 1;
			}
		}
		if(!found){
			printf("empty line\n");
			return;
		}

		/*Search for every matching element in every col between min and max col*/
		for(curr = Matrix.minj; curr <= Matrix.maxj; curr++){
			i = -1, found = 0;
			while(!found && ++i < Matrix.len){
				if(Matrix.mat[i].row == number && Matrix.mat[i].col == curr){
					found = 1;
				}
			}

			printf(" %.3f", found ? Matrix.mat[i].value : Matrix.zero);
		}
		printf("\n");
	}
}

void infoSparse(Sparse Matrix, char prntFlag){
	/*print matrix status
	format:
		[<mini> <minj>] [<maxi> <maxj>] <no. elements> / <size> = <density>

	prntFlag: 0->DONT print | 1-> print*/

	if (prntFlag && !Matrix.len){
		printf("empty matrix\n");
		return;
	}

	if(prntFlag){
		printf("[%lu %lu] [%lu %lu] %lu / %lu = %.3f%%\n",\
			Matrix.mini,
			Matrix.minj,
			Matrix.maxi,
			Matrix.maxj,
			Matrix.len,
			Matrix.size,
			((float)Matrix.len / Matrix.size)*100);
	}
}

void shortInfo(Sparse Matrix){
	/*print compressed matrix*/
	Sparse aux = MatrixCopy(Matrix);
	unsigned long compressSize = (MATSIZE * 2);
	unsigned long numOfRows = Matrix.maxi-Matrix.mini+1;
	unsigned long i,j, k;
	unsigned long maxNum, heavierLine = 0;
	unsigned long rowHistogram[numOfRows];

	char stopFlag;
	int tryOffset;
	unsigned long valIndex, maxOffset = 0;

	double values[compressSize];
	unsigned long indexes[compressSize];
	int offsets[numOfRows];

	/*checks if compressable*/
	if((float)Matrix.len/Matrix.size > 0.5){
		printf("dense matrix\n");
		return;
	}

	/*Ordena vetor auxiliar: facilita procuras*/
	aux = sortSparse(aux, 0);

	for(i = 0; i < compressSize; i++){
		values[i] = Matrix.zero;
		indexes[i] = 0;
	}

	/*Init row histogram*/
	for(i = 0; i < numOfRows; i++){
		rowHistogram[i] = 0;
	}

	/*Build row histogram*/
	for(i = 0; i < aux.len; i++){
		rowHistogram[(aux.mat[i].row) - (aux.mini)]++;
	}

	/*Build output*/
	for(j = 0; j < numOfRows; j++){
		/*Find heavier row*/
		maxNum = 0;
		for(i = 0; i < numOfRows; i++){
			if(rowHistogram[i] > maxNum){
				maxNum = rowHistogram[i];
				heavierLine = i;
			}
		}

		if(maxNum > 0){
			/*prepare next heavier line*/
			rowHistogram[heavierLine] = 0;

			/*get heavier line value*/
			heavierLine += aux.mini;

			/*i := first index of heavierLine in matrix*/
			i = 0;
			while(i < Matrix.len && aux.mat[i++].row != heavierLine);
			i--;

			/*find fitting offset*/
			tryOffset = 0;
			stopFlag = 0;
			while(!stopFlag){
				k = i;
				stopFlag = 1;
				/*for each offset checks if every row fits in values, keeping relative positions*/
				while(k < aux.len && aux.mat[k].row == heavierLine && stopFlag){
					if(values[(aux.mat[k].col)-(aux.minj)+tryOffset] != aux.zero){
						stopFlag = 0;
						tryOffset++;
					}
					k++;
				}
			}

			/*write row in values (keep track of index)*/
			k = i;
			while(k < aux.len && aux.mat[k].row == heavierLine){
				valIndex = (aux.mat[k].col)-(aux.minj)+tryOffset;
				values[valIndex] = aux.mat[k].value;
				indexes[valIndex] = heavierLine;
				k++;
			}
			/*know max offset -> know compress size*/
			if(tryOffset > maxOffset){
				maxOffset = tryOffset;
			}

			offsets[heavierLine-(aux.mini)] = tryOffset;
		}
	}

	compressSize = maxOffset + aux.maxj - aux.minj + 1;

	/*print output*/
	printf("value =");
	for(i = 0; i < compressSize; i++){
		printf(" %.3f", values[i]);
	}

	printf("\nindex =");
	for(i = 0; i < compressSize; i++){
		printf(" %lu", indexes[i]);
	}

	printf("\noffset =");
	for(i = 0; i < numOfRows; i++){
		printf(" %d", offsets[i]);
	}
	printf("\n");
}
