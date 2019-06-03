/*
Afonso Goncalves | ist189399

main file. Reads stdio input and handles it
*/

#include "functions.h"

int main(int argc, char **argv){
	char resetFlag; /*controls resetBuffer()*/
	char buffer[FILENAMESIZE];
	Command input;
	Sparse Matrix;

	/*Init Matrix*/
	if(argc >= 2){
		Matrix = readFromFile(argv[1]);
		strcpy(input.filename, argv[1]);
	} else {
		Matrix = createEmptyMatrix();
	}

	while (1) {
		resetFlag = 1;
		switch(input.command = getchar()){
			case 'a':
				if(scanf("%lu %lu %lf", &input.iArg1, &input.iArg2, &input.dArg1) == 3)
					Matrix = addToMatrix(Matrix, input.iArg1, input.iArg2, input.dArg1);
				break;
			
			case 'p':
				printSparse(Matrix);
				break;
			
			case 'i':
				infoSparse(Matrix, 1);
				break;
			
			case 'l':
				if(scanf("%lu", &input.iArg1))
					printSection(Matrix, input.iArg1, 0);
				break;
			
			case 'c':
				if(scanf("%lu", &input.iArg1))
					printSection(Matrix, input.iArg1, 1);
				break;
			
			case 'o':
				if(getchar() == '\n'){
					Matrix = sortSparse(Matrix, 0);
					resetFlag = 0;
				} else {
					Matrix = sortSparse(Matrix, 1);
				}
				break;
			
			case 'z':
				if(scanf("%lf", &input.dArg1)){
					Matrix = changeZero(Matrix, input.dArg1);
				}
				break;
			
			case 'w':
				if(getchar() != '\n' && fgets(buffer, FILENAMESIZE, stdin) != NULL){
					/*remove final '\n'*/
					buffer[strlen(buffer)-1] = '\0';
					strcpy(input.filename, buffer);
				}
					
				writeFile(Matrix, input.filename);
				resetFlag = 0;
				break;
			
			case 's':
				shortInfo(Matrix);
				break;
			
			case 'q':
				return 0;
		}

		if(resetFlag) resetBuffer();
	}
}