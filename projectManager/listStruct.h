#ifndef _TASK_
#define _TASK_
#include "taskStruct.h"
#endif

typedef Task* Item;	

typedef struct node {
	Item item;			/*content of each node*/
	struct node* next;	/*link to the next node*/
	struct node* prev;	/*link to the prev node*/
} LNode;

typedef struct list {	/*list overview*/
	LNode* head;		/*pointer to first element of list*/
	LNode* tail;		/*pointer to last element of list*/
} List;
