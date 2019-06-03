#include "listPr.h"


List* LISTcreate(){
	List* ptr;

	/*empty default list*/
	ptr = (List*)malloc(sizeof(List));
	ptr->head = NULL;
	ptr->tail = NULL;

	return ptr;
}

static LNode* NEW(List* ptr, Item item){
	LNode* newPtr;

	newPtr = (LNode*)malloc(sizeof(LNode));

	/*default new node with item*/
	newPtr->item = item;
	newPtr->next = NULL;
	newPtr->prev = NULL;

	return newPtr;
}


LNode* LISTadd(List* ptr, Item item){
	LNode* newPtr;
	LNode* tail;

	/*new node*/
	newPtr = NEW(ptr, item);

	/*add to the end of the list*/
	LISTsetNext(newPtr, NULL);
	LISTsetPrev(newPtr, LISTtail(ptr));

	if(LISThead(ptr) == NULL){
		/*if empty list*/
		LISTsetHead(ptr, newPtr);
		LISTsetTail(ptr, newPtr);
	} else {
		tail = LISTtail(ptr);
		LISTsetNext(tail, newPtr);
		LISTsetTail(ptr, newPtr);
	}
	return newPtr;
}

void LISTremove(List* ptr, LNode* elem){
	/*check if is head or tail separately:
	works if element is unique el. of list*/
	if(LISTprev(elem) == NULL)
		/*first position*/
		LISTsetHead(ptr, LISTnext(elem));

	else
		/*after element*/
		/*elem->prev->next = elem->next*/
		LISTsetNext(LISTprev(elem), LISTnext(elem));
	

	if(LISTnext(elem) == NULL)
		/*last position*/
		/*ptr->tail = elem->prev*/
		LISTsetTail(ptr, LISTprev(elem));

	else
		/*before element*/
		/*elem->next->prev = elem->prev*/
		LISTsetPrev(LISTnext(elem), LISTprev(elem));

	free(elem);
}



/*-----------------------------------------------------------------*/
/*                     Read list struct fields                     */
/*-----------------------------------------------------------------*/
LNode* LISThead(List* ptr){
	return ptr->head;
}

LNode* LISTtail(List* ptr){
	return ptr->tail;
}


Item LISTitem(LNode* node){
	return node->item;
}

LNode* LISTnext(LNode* node){
	return node->next;
}

LNode* LISTprev(LNode* node){
	return node->prev;
}
/*-----------------------------------------------------------------*/



/*-----------------------------------------------------------------*/
/*                    Write list struct fields                     */
/*-----------------------------------------------------------------*/
static void LISTsetHead(List* ptr, LNode* newHead){
	ptr->head = newHead;
}

static void LISTsetTail(List* ptr, LNode* newTail){
	ptr->tail = newTail;
}

static void LISTsetNext(LNode* ptr, LNode* newNext){
	ptr->next = newNext;
}

static void LISTsetPrev(LNode* ptr, LNode* newPrev){
	ptr->prev = newPrev;
}
/*-----------------------------------------------------------------*/
