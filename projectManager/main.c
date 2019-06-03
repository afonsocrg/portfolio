#include "main.h"


/*lifecycle of the shell*/
int main(){
	char status;
	char **argv;
	int i;

	SHELLinit();

	status = TRUE;
	while(status){
		/*readArguments allocates
		needed memory*/
		argv = readArguments();
		if(argv != NULL){
			status = execute(argv);

			/*free arguments*/
			i = -1;
			while(argv[++i] != NULL)
				free(argv[i]);
			free(argv);
		}
	}
	
	SHELLexit();
	return 0;
}
