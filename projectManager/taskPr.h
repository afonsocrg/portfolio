#include "taskPub.h"


/*add precedent to dependent's list of dependencies*/
static void setDependency(Task* dependent, Task* precedent);

/*add dependent to precedent's list of precedences*/
static void setPrecedence(Task* dependent, Task* precedent);

/*remove dependent task from precedent precedences list*/
static void rmPrecedence(Task* precedent, Task* dependent);
