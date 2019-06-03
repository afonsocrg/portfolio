#ifndef _LIST_
#define _LIST_
#include "listStruct.h"
#endif

/*create new empty list*/
List* LISTcreate();



/*add item to the end of the list*/
/*returns pointer to the new element*/
LNode* LISTadd(List* ptr, Item item);

/*remove element from list*/
void LISTremove(List* ptr, LNode* elem);


/*-----------------------------------------------------------------*/
/*                     Read list struct fields                     */
/*-----------------------------------------------------------------*/
/*get list first element*/
LNode* LISThead(List* ptr);

/*get list last element*/
LNode* LISTtail(List* ptr);


/*read node item*/
Item LISTitem(LNode* node);

/*get next node*/
LNode* LISTnext(LNode* node);

/*get previous node*/
LNode* LISTprev(LNode* node);
/*-----------------------------------------------------------------*/
