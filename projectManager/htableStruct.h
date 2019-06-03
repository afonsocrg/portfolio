#ifndef _LIST_
#define _LIST_
#include "listStruct.h"
#endif

typedef LNode* HItem;
typedef unsigned long Key;

typedef struct hnode {
	struct hnode* next;		/*next element of row*/
	HItem item;				/*item in node*/
	Key key;				/*key of current node*/
} HNode;
