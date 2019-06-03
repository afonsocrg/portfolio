#include "all.h"
#include "shellPub.h"
#include "managerPub.h"

#define MAXDESC 8000		/*Max size of task description*/
#define ULOM 10				/*UL order of magnitude*/
#define MAXULI "4294967295"	/*greatest UL number*/
#define BUFFSIZE 100		/*buffer default size*/
#define TOKBUFFSZ 10		/*default number of arguments*/
#define ARGSZ 10			/*default size of each argument*/

/*command codes*/
#define INVALID -1
#define ADD 0
#define DURATION 1
#define DEPEND 2
#define REMOVE 3
#define PATH 4
#define EXIT 5


/*check is every argument os valid*/
static char validArgs(int commandCode, char** argv);

/*check if valid unsigned long*/
static char validUL(char* string);

/*check if less than unsigned long max*/
static char checkOverflow(char* string);

/*check if valid strng (starts and ends with quotes;
has less than MAXDESC characters*/
static char validStr(char* string);

/*counts arguments*/
static int NArguments(char** argv);

/*runs commands*/
static char runCommand(char** argv, int nargs, char commandCode);
