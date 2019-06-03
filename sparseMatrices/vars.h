#define MATSIZE 		10000
#define FILENAMESIZE 	81
#define MAXLINE 		30
#define O_ARGSIZE 		7

typedef struct {
	char command;
	char colFlag;
	char filename[FILENAMESIZE];
	char orderParam[O_ARGSIZE];

	unsigned long iArg1;
	unsigned long iArg2;

	double dArg1;
} Command;

typedef struct {
	unsigned long row;
	unsigned long col;

	double value;
} Element;

typedef struct {
	unsigned long size;
	unsigned long len;

	unsigned long mini;
	unsigned long maxi;
	unsigned long minj;
	unsigned long maxj;

	double zero;

	Element mat[MATSIZE];
} Sparse;
