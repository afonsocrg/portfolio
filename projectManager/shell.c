#include "shellPr.h"

void SHELLinit(){
	MNGinit();
}

void SHELLexit(){
	MNGfreeall();
}

char** readArguments(){
	char** arguments;
	char* currentArg;
	int argsBufSz, currargSz;
	int currInd, argInd;
	char c, outsideQuote;

	argsBufSz = TOKBUFFSZ;
	currargSz = BUFFSIZE;
	arguments = (char**)malloc(sizeof(char*) * argsBufSz);
	currentArg = (char*)malloc(sizeof(char) * currargSz);

	/*read line*/
	currInd = argInd = 0;
	outsideQuote = 1;
	while((c = getchar()) != '\n'){
		if(c == ' ' && currInd > 0 && outsideQuote){
			currentArg[currInd] = '\0';
			currInd = 0;
			arguments[argInd++] = currentArg;
			/*check if buffer limit exceeded*/
			if(argInd >= argsBufSz){
				argsBufSz += TOKBUFFSZ;
				arguments = (char**)realloc(arguments, sizeof(char*) * argsBufSz);
			}
			/*new argument slot*/
			currargSz = BUFFSIZE;
			currentArg = (char*)malloc(sizeof(char) * currargSz);
		} else {
			if(c == '"')
				outsideQuote = !outsideQuote;
			
			currentArg[currInd++] = c;
			/*check if buffer limit exceeded*/
			if(currInd >= currargSz){
				currargSz += BUFFSIZE;
				currentArg = (char*)realloc(currentArg, sizeof(char) * currargSz);
			}
		}
	}
	if(currInd == 0 && argInd == 0){
		free(currentArg);
		return NULL;
	}

	currentArg[currInd] = '\0';
	arguments[argInd++] = currentArg;

	if(argInd >= argsBufSz){
		argsBufSz += 1;
		arguments = (char**)realloc(arguments, sizeof(char*) * argsBufSz);
	}

	/*arguments vector ends with NULL*/
	arguments[argInd] = NULL;
	return arguments;
}

char execute(char** argv){
	char* command;
	char commandCode;
	int nargs;

	if(argv == NULL){
		printf("illegal arguments\n");
		return 1;
	}

	nargs = NArguments(argv);
	command = argv[0];
	if(!strcmp(command, "add") && validArgs(ADD, argv))
		commandCode = ADD;

	else if(!strcmp(command, "duration") && validArgs(DURATION, argv))
		commandCode = DURATION;

	else if(!strcmp(command, "depend") && validArgs(DEPEND, argv))
		commandCode = DEPEND;

	else if(!strcmp(command, "remove") && validArgs(REMOVE, argv))
		commandCode = REMOVE;

	else if(!strcmp(command, "path"))
		commandCode = PATH;

	else if(!strcmp(command, "exit"))
		commandCode = EXIT;
	
	else {
		printf("illegal arguments\n");
		commandCode = INVALID;
	}
	return runCommand(argv, nargs, commandCode);
}

static char runCommand(char** argv, int nargs, char commandCode){
	ULI arg1, arg3;
	ULI* arg4;
	char* arg2;
	char* aux;
	int i;

	switch (commandCode){
		case ADD:
			/*cast arguments*/
			arg1 = strtoul(argv[1], &aux, 10);
			arg2 = argv[2];
			arg3 = strtoul(argv[3], &aux, 10);
			arg4 = (ULI *)malloc(
				sizeof(ULI) * (nargs - 3)); /*num of <ids>*/
			for(i = 4; i <= nargs; i++){
				arg4[i-4] = strtoul(argv[i], &aux, 10);
			}

			MNGaddTask(arg1, arg2, arg3, arg4, nargs - 3);
			free(arg4);
			break;
		case DURATION:
			arg1 = 0;
			if(nargs >= 1)
				arg1 = strtoul(argv[1], &aux, 10);
			MNGprintTasks(arg1, nargs >= 1, FALSE);
			break;
		case DEPEND:
			arg1 = strtoul(argv[1], &aux, 10);
			MNGdisplayDep(arg1);
			break;
		case REMOVE:
			arg1 = strtoul(argv[1], &aux, 10);
			MNGremoveTask(arg1);
			break;
		case PATH:
			MNGpath();
			break;
		case EXIT:
			return FALSE;
	}
	return TRUE;
}

static int NArguments(char** argv){
	int i = 0;
	while(argv[++i] != NULL);
	return i-1;
}

static char validArgs(int commandCode, char** argv){
	char valid = 1;
	int nargs, i;

	nargs = NArguments(argv);
	switch(commandCode){
		case ADD:
			valid = (
				nargs >= 3 &&
				validUL(argv[1]) &&
				validStr(argv[2]) &&
				validUL(argv[3])
			);

			for(i = 4; valid && i <= nargs; i++){
				/*check ids*/
				valid = validUL(argv[i]);
			}
			return valid;

		case DURATION:
			return(nargs == 0 || validUL(argv[1]));
		case DEPEND:
			return nargs >= 1 && validUL(argv[1]);
		case REMOVE:
			return nargs >= 1 && validUL(argv[1]);
		default: /*else*/
			return 0;
	}
}

static char validUL(char* string){
	int i;

	i = -1;
	while(string[++i] != '\0')
		if(string[i] < '0' || '9' < string[i])
			return FALSE;

	return checkOverflow(string); /*True if less than UL max*/
}

static char checkOverflow(char* string){
	int len;
	len = strlen(string);
	if(len == ULOM)
		/*less than MAXULI*/
		return strcmp(string, MAXULI) <= 0;
	
	/*less digits than ULOM, not 0*/
	return len<ULOM && strcmp(string, "0");
}

static char validStr(char* string){
	int i;

	i = 0;
	if(string[i] == '"')
		while(string[++i] != '"');
	

	return (
		i != 0 && 				/*1st char is quote*/
		string[i+1] == '\0'	&&	/*there is \0 after last quote*/
		i < MAXDESC				/*has less than MAXDESC chars*/
	);
}
