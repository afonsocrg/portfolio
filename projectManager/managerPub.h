#ifndef _MANAGER_
#define _MANAGER_
#include "managerStruct.h"
#endif

/*
This component uses global values inside .c files

To instantiate more than one manager, please
add "Manager* manager" to the beginning of each function
and remove the global variable in manager.c file
*/


/*initialize manager (init lists, tasks and htable)*/
void MNGinit();

/*frees everything that is alloced (avoid memory leaks)*/
void MNGfreeall();

/*add task to project (add to list, search table)*/
void MNGaddTask(ULI id, char* desc, ULI dur, ULI* deps, int numDep);

/*removes task from project (removes from list and search table)*/
void MNGremoveTask(ULI id);


/*print project
	if pathonly -> print only critical tasks
	if durFlag 	-> print only tasks with duration greater then duration*/
void MNGprintTasks(ULI duration, char durFlag, char pathonly);

/*print ids of tasks dependent of wanted one*/
void MNGdisplayDep(ULI arg1);

/*calculates project critital path*/
void MNGpath();
