#include "all.h"
#include "listPub.h"


/*create new node with item inside*/
static LNode* NEW(List* ptr, Item item);


/*-----------------------------------------------------------------*/
/*                    Write list struct fields                     */
/*-----------------------------------------------------------------*/
/*set new head to list*/
static void LISTsetHead(List* ptr, LNode* newHead);

/*set new tail to list*/
static void LISTsetTail(List* ptr, LNode* newTail);

/*set node next element*/
static void LISTsetNext(LNode* ptr, LNode* newNext);

/*set node previous element*/
static void LISTsetPrev(LNode* ptr, LNode* newPrev);
/*-----------------------------------------------------------------*/
