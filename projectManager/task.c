#include "taskPr.h"

Task* TASKcreate(ULI id, char* desc, ULI dur, int numDepend){
	Task* new;

	/*init task values*/
	new = (Task*)malloc(sizeof(Task));
	TASKsetid(new, id);
	TASKsetDesc(new, desc);
	TASKsetduration(new, dur);
	TASKsetearly(new, 0);
	TASKsetlate(new, 0);
	new->depends = NULL;
	new->precedes = NULL;

	/*these values change in TASKlink*/
	new->numPrecede = 0;
	new->numDepend = 0;

	return new;
}

void TASKfree(Task* ptr){
	free(ptr->description);
	free(ptr->depends);
	free(ptr->precedes);	
}

/*-----------------------------------------------------------------*/
/*                     Read task struct fields                     */
/*-----------------------------------------------------------------*/
ULI TASKid(Task* ptr){
	return ptr->id;
}

ULI TASKduration(Task* ptr){
	return ptr->duration;
}

ULI TASKearly(Task* ptr){
	return ptr->early;
}

ULI TASKlate(Task* ptr){
	return ptr->late;
}

char* TASKdescription(Task* ptr){
	return ptr->description;
}

int TASKnumDepend(Task* ptr){
	return ptr->numDepend;
}

int TASKnumPrecede(Task* ptr){
	return ptr->numPrecede;
}

Task** TASKdepends(Task* ptr){
	return ptr->depends;
}

Task** TASKprecedes(Task* ptr){
	return ptr->precedes;
}
/*-----------------------------------------------------------------*/



/*-----------------------------------------------------------------*/
/*                    Write task struct fields                     */
/*-----------------------------------------------------------------*/
void TASKsetid(Task* ptr, ULI id){
	ptr->id = id;
}

void TASKsetduration(Task* ptr, ULI dur){
	ptr->duration = dur;
}

void TASKsetearly(Task* ptr, ULI early){
	ptr->early = early;
}

void TASKsetlate(Task* ptr, ULI late){
	ptr->late = late;
}

void TASKsetDesc(Task* ptr, char* string){
	ptr->description = (char*)malloc(strlen(string) + 1);
	strcpy(ptr->description, string);
}

void TASKsetNumPrecede(Task* ptr, int newNum){
	ptr->numPrecede = newNum;
}

void TASKsetNumDepend(Task* ptr, int newNum){
	ptr->numDepend = newNum;
}

static void setDependency(Task* dependent, Task* precedent){
	if(TASKnumDepend(dependent) == 0){
		dependent->depends = (Task**)malloc(sizeof(Task*));
		TASKdepends(dependent)[0] = precedent;
	} else {
		dependent->depends = (Task**)realloc(
			TASKdepends(dependent),
			sizeof(Task*) * (TASKnumDepend(dependent) + 1));

		TASKdepends(dependent)[TASKnumDepend(dependent)] = precedent;
	}
	/*(dependent->numDepend)++*/
	TASKsetNumDepend(dependent, TASKnumDepend(dependent) + 1);
}

static void setPrecedence(Task* dependent, Task* precedent){
	if(TASKnumPrecede(precedent) == 0){
		precedent->precedes = (Task**)malloc(sizeof(Task*));
		TASKprecedes(precedent)[0] = dependent;
	} else {
		precedent->precedes = (Task**)realloc(
			TASKprecedes(precedent),
			sizeof(Task*) * (TASKnumPrecede(precedent) + 1));

		TASKprecedes(precedent)[TASKnumPrecede(precedent)] = dependent;
	}
	/*(precedent->numPrecede)++*/
	TASKsetNumPrecede(precedent, TASKnumPrecede(precedent) + 1);
}
/*-----------------------------------------------------------------*/



void TASKprint(Task* ptr, char pathed){
	ULI early, late;
	int numdep, i;
	Task** deps;

	early = TASKearly(ptr);
	late = TASKlate(ptr);

	/*print basic info*/
	printf("%lu %s %lu",
		TASKid(ptr),
		TASKdescription(ptr),
		TASKduration(ptr));

	/*if task is pathed, print early and late*/
	if(pathed){
		if(early == late){
			printf(" [%lu CRITICAL]", early);
		} else {
			printf(" [%lu %lu]", early, late);
		}
	}
	/*print dependencies if they exist*/
	numdep = TASKnumDepend(ptr);
	deps = TASKdepends(ptr);
	for(i = 0; i < numdep; i++){
		printf(" %lu", TASKid(deps[i]));
	}
	printf("\n");
}


void TASKcalcuLate(Task* ptr, ULI projDur){
	int numprec, i;
	Task** prec;
	ULI currLate, tryLate;

	if(ptr == NULL){
		/*check errors*/
		printf("TASKcalcuLate: ERROR - NULL pointer\n");
		return;
	}

	currLate = projDur - TASKduration(ptr);
	numprec = TASKnumPrecede(ptr);
	prec = TASKprecedes(ptr);
	/*for each dependant task compare and get smaller late start*/
	for(i = 0; i < numprec; i++){
		tryLate = TASKlate(prec[i]) - TASKduration(ptr);
		if(tryLate < currLate){
			currLate = tryLate;
		}
	}
	TASKsetlate(ptr, currLate);
}


void TASKlink(Task* dependent, Task* precedent){
	int i, numDep;
	Task** dependencies;

	if(precedent == NULL){
		printf("TASKlink: NULL precedent\n");
		return;
	} else if (dependent == NULL){
		printf("TASKlink: NULL dependent\n");
		return;		
	}

	dependencies = TASKdepends(dependent);
	numDep = TASKnumDepend(dependent);
	/*if dependency already set, exit*/
	for(i = 0; i < numDep; i++)
		if(precedent == dependencies[i])
			return;


	setDependency(dependent, precedent);
	setPrecedence(dependent, precedent);
}

void TASKunlink(Task* ptr){
	int i, numDep;
	Task** deps;

	/*if task has dependants, return*/
	if(TASKnumPrecede(ptr) > 0)
		return;

	numDep = TASKnumDepend(ptr);
	deps = TASKdepends(ptr);
	/*remove precedence of every precedent task*/
	for(i = 0; i < numDep; i++)
		rmPrecedence(deps[i], ptr);
}

static void rmPrecedence(Task* precedent, Task* dependent){
	Task** prec;
	int i, numPrec;

	i = -1;
	numPrec = TASKnumPrecede(precedent);
	prec = TASKprecedes(precedent);
	/*find wanted pointer to remove*/
	while(++i < numPrec && prec[i] != dependent)
		;
	/*overwrite task pointer to be deleted*/
	while(++i < numPrec)
		prec[i-1] = prec[i];

	TASKsetNumPrecede(precedent, --numPrec);
	return;
}
