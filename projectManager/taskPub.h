#ifndef _ALL_
#define _ALL_
#include "all.h"
#endif

#ifndef _TASK_
#define _TASK_
#include "taskStruct.h"
#endif


/*create a new task*/
Task* TASKcreate(ULI id, char* desc, ULI dur, int numDepend);

/*free alloced memory*/
void TASKfree(Task* ptr);

/*-----------------------------------------------------------------*/
/*                     Read task struct fields                     */
/*-----------------------------------------------------------------*/
/*outputs task id*/
ULI TASKid(Task* ptr);

/*outputs task duration*/
ULI TASKduration(Task* ptr);

/*outputs task early start*/
ULI TASKearly(Task* ptr);

/*outputs task latestart*/
ULI TASKlate(Task* ptr);

/*outputs task description pointer*/
char* TASKdescription(Task* ptr);

/*outputs task number of dependencies*/
int TASKnumDepend(Task* ptr);

/*outputs task number of precedences*/
int TASKnumPrecede(Task* ptr);

/*outputs pointer to precedent tasks pointers
(task depends on these tasks)*/
Task** TASKdepends(Task* ptr);

/*outputs pointer to dependent tasks pointers
(task precedes these tasks)*/
Task** TASKprecedes(Task* ptr);
/*-----------------------------------------------------------------*/



/*-----------------------------------------------------------------*/
/*                    Write task struct fields                     */
/*-----------------------------------------------------------------*/
/*change task ID*/
void TASKsetid(Task* ptr, ULI id);

/*change task duration*/
void TASKsetduration(Task* ptr, ULI dur);

/*change task early start*/
void TASKsetearly(Task* ptr, ULI early);

/*change task late start*/
void TASKsetlate(Task* ptr, ULI late);

/*change task description*/
void TASKsetDesc(Task* ptr, char* desc);

/*change task number of precedences*/
void TASKsetNumPrecede(Task* ptr, int newNum);
/*-----------------------------------------------------------------*/



/*adds dependent task to precedent's dependencies list*/
void TASKlink(Task* dependent, Task* precedent);

/*removes task from precedent tasks' dependent list*/
void TASKunlink(Task* ptr);


/*calculates task late start*/
void TASKcalcuLate(Task* ptr, ULI projDur);


/*print in stdout task information
prints early and late start if pathed*/
void TASKprint(Task* ptr, char pathed);