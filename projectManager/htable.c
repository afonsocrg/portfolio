#include "htablePr.h"


HNode** HTcreate(){
	HNode** root;
	int i;

	root = (HNode**)malloc(sizeof(HNode*) * HTSIZE);
	for(i = 0; i < HTSIZE; i++)
		/*init with default NULL values*/
		root[i] = NULL;

	return root;
}

void HTfreeKey(HNode** root, Key key){
	HNode* curr;
	HNode* aux;
	Key index;

	/*get hash row*/
	index = hash(key);
	curr = root[index];
	while(curr != NULL){
		aux = curr;
		curr = curr->next;
		free(aux);
	}
	root[index] = NULL;
}


void HTinsert(HNode** root, HItem item, Key key){
	Key index;
	index = hash(key);
	root[index] = insertNode(root[index], item, key);
}

void HTremove(HNode** root, Key key){
	Key index;
	index = hash(key);
	root[index] = removeNode(root[index], key);
}


HItem HTsearchKey(HNode** root, Key key){
	HNode* currNode;

	currNode = root[hash(key)];
	while(currNode != NULL){
		if(HNodeKey(currNode) == key)
			return HNodeItem(currNode);
		
		currNode = HTnext(currNode);
	}
	return NULL;
}



/*-----------------------------------------------------------------*/
/*                    Read htable struct fields                    */
/*-----------------------------------------------------------------*/
static Key HNodeKey(HNode* ptr){
	return ptr->key;
}

static HItem HNodeItem(HNode* ptr){
	return ptr->item;
}

static HNode* HTnext(HNode* ptr){
	return ptr->next;
}
/*-----------------------------------------------------------------*/



/*-----------------------------------------------------------------*/
/*                   Write htable struct fields                    */
/*-----------------------------------------------------------------*/
static void HNodesetKey(HNode* ptr, Key key){
	ptr->key = key;
}

static void HNodesetItem(HNode* ptr, HItem item){
	ptr->item = item;
}

static void HNodesetNext(HNode* ptr, HNode* next){
	ptr->next = next;
}
/*-----------------------------------------------------------------*/


static HNode* insertNode(HNode* head, HItem item, Key key){
	/*inserts to begining of row*/
	HNode* new;

	new = (HNode*)malloc(sizeof(HNode));
	HNodesetKey(new, key);
	HNodesetItem(new, item);
	HNodesetNext(new, head);

	return new;
}

static HNode* removeNode(HNode* head, Key key){
	HNode* aux, *prev;

	if(head == NULL)
		return head;

	else if (HNodeKey(head) == key){
		aux = HTnext(head);
		free(head);
		return aux;
	
	} else {
		aux = head;
		while(aux != NULL && HTnext(aux) != NULL){
			prev = aux;
			aux = HTnext(prev);
			if(HNodeKey(aux) == key){
				/*prev->next = aux->next*/
				HNodesetNext(prev, HTnext(aux));
				free(aux);
				aux = NULL;	/*break while*/
			}
		}
		return head;
	}
}
