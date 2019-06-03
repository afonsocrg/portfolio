#include "all.h"

#include "managerPub.h"

#include "taskPub.h"
#include "listPub.h"
#include "htablePub.h"

/*search for a list node by its task id*/
static LNode* searchID(ULI id);

/*check if dependencies ids already exist in project*/
static char validDepend(ULI* dependencies, int numDepend);
