#include "managerPr.h"

Manager manager;

void MNGinit(){
	manager.pathed = 0;
	manager.duration = 0;
	manager.htable = HTcreate();
	manager.taskList = LISTcreate();
}

void MNGfreeall(){
	LNode* nodeptr;
	LNode* aux;
	Task* taskPtr;
	ULI key;

	nodeptr = manager.taskList->head;
	while(nodeptr != NULL){
		/*free task and htable row node by node*/
		taskPtr = LISTitem(nodeptr);
		key = TASKid(taskPtr);
		HTfreeKey(manager.htable, key);
		TASKfree(taskPtr);
		free(taskPtr);
		aux = nodeptr->next;
		free(nodeptr);
		nodeptr = aux;
	}

	free(manager.taskList);
	free(manager.htable);
}

void MNGaddTask(ULI id, char* desc, ULI dur, ULI* deps, int numDep){
	ULI bigearly, testEarly;
	int i;
	Task* task;
	LNode* dep;
	LNode* newNode;

	/*check if valid task*/
	if(searchID(id)){
		printf("id already exists\n");
		return;
	}
	if((manager.taskList->head == NULL && numDep > 0) ||
		(manager.taskList->head != NULL && !validDepend(deps, numDep))){
		printf("no such task\n");
		return;		
	}

	bigearly = 0;
	/*create task and add to list and htable*/
	task = TASKcreate(id, desc, dur, numDep);
	newNode = LISTadd(manager.taskList, task);
	HTinsert(manager.htable, newNode, id);

	/*link tasks and search biggest early start*/
	for(i = 0; i < numDep; i++){
		dep = searchID(deps[i]);
		TASKlink(task, dep->item);
		if((testEarly = dep->item->early + dep->item->duration) > bigearly)
			bigearly = testEarly;
	}

	/*set early start and update proj duration*/
	TASKsetearly(task, bigearly);
	manager.pathed = 0;
	if((dur + bigearly) > manager.duration)
		manager.duration = dur + bigearly;
}

void MNGremoveTask(ULI id){
	LNode* rmvPtr;
	Task* taskPtr;

	if((rmvPtr = searchID(id)) == NULL){
		printf("no such task\n");
		return;
	}

	taskPtr = LISTitem(rmvPtr);

	if(TASKnumPrecede(taskPtr) > 0){
		printf("task with dependencies\n");
		return;
	}

	HTremove(manager.htable, id);
	TASKunlink(taskPtr);
	TASKfree(taskPtr);
	free(taskPtr);
	LISTremove(manager.taskList, rmvPtr);

	manager.pathed = 0;

	return;
}

void MNGprintTasks(ULI duration, char durFlag, char pathonly){
	LNode* currPtr;
	Task* taskPtr;

	if(!durFlag){
		duration = 0;
	}

	if(manager.taskList != NULL && manager.taskList->head != NULL){
		currPtr = manager.taskList->head;
		while(currPtr != NULL){
			taskPtr = LISTitem(currPtr);
			if(	(pathonly && TASKearly(taskPtr) == TASKlate(taskPtr))||
				(!pathonly && TASKduration(taskPtr) >= duration)
				)
				TASKprint(taskPtr, manager.pathed);

			currPtr = currPtr->next;
		}
	}
}

void MNGdisplayDep(ULI id){
	LNode* currPtr;
	Task** precedences;
	int numPrec, i;

	if((currPtr = searchID(id)) == NULL){
		printf("no such task\n");
		return;
	}

	numPrec = TASKnumPrecede(LISTitem(currPtr));
	printf("%lu:", id);
	if(numPrec == 0)
		printf(" no dependencies");
	
	precedences = TASKprecedes(LISTitem(currPtr));
	for(i = 0; i < numPrec; i++)
		printf(" %lu", TASKid(precedences[i]));

	printf("\n");
}

void MNGpath(){
	LNode* currLNode;
	Task* currTask;
	ULI dur;

	dur = 0;
	if(!manager.pathed){
		/*set project duration*/
		currLNode = LISThead(manager.taskList);
		while(currLNode != NULL){
			currTask = LISTitem(currLNode);
			if((TASKearly(currTask) + TASKduration(currTask)) > dur)
				dur = TASKearly(currTask) + TASKduration(currTask);
			
			currLNode = LISTnext(currLNode);
		}
		manager.duration = dur;
	}
	
	/*calculate from end to beginning of list:
	when starting from the end, all needed late starts
	are calculated for each task*/
	currLNode = LISTtail(manager.taskList);
	while(currLNode != NULL){
		TASKcalcuLate(LISTitem(currLNode), dur);
		currLNode = LISTprev(currLNode);
	}

	manager.pathed = 1;
	MNGprintTasks(0, 0, TRUE);
	printf("project duration = %lu\n", manager.duration);
	return;
}

static LNode* searchID(ULI id){
	return HTsearchKey(manager.htable, id);
}

static char validDepend(ULI* dependencies, int numDepend){
	int i;
	for(i = 0; i < numDepend; i++)
		if(!searchID(dependencies[i]))
			/*depend does not exist*/
			return FALSE;

	return TRUE;
}
