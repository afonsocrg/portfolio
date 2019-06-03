/*
		Command format and types

<command>	<arg1>	<arg2>	<arg3>	<arg4>

add			id		descr.	durat.	ids
char*		ul		char*	ul		ul*

duration	?dur 	-----	-----	-----
char*		ul		-----	-----	-----

depend		id 		-----	-----	-----
char*		ul 		-----	-----	-----

remove		id 		-----	-----	-----
char* 		ul 		-----	-----	-----

path		-----	-----	-----	-----
char* 		-----	-----	-----	-----

exit		-----	-----	-----	-----
char*		-----	-----	-----	-----

overall		arg1	arg2	arg3	arg4
char*		ul		char*	ul		ul*
*/



/*initializes manager and shell*/
void SHELLinit();

/*closes all jobs and frees every memory*/
void SHELLexit();

/*read stdin line
return pointer to char pointers (each one points to an argument*/
/*each argument is delimited by quotes or spaces, if quotes are inexistent*/
char** readArguments();

/*interprets arguments and run commands*/
char execute(char** argv);
