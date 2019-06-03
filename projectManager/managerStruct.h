#ifndef _LIST_
#define _LIST_
#include "listStruct.h"
#endif

#ifndef _HASHTABLE_
#define _HASHTABLE_
#include "htableStruct.h"
#endif


typedef struct {
	List* taskList;			/*beginning and end of list*/
	HNode** htable;			/*search tasks by id*/
	char pathed;			/*project is pathed or not*/
	unsigned long duration;	/*duration of the project*/
} Manager;
