#include "all.h"
#include "htablePub.h"

#define HTSIZE 349

#define hash(A) (A%HTSIZE)


/*returns node key*/
static Key HNodeKey(HNode* ptr);

/*returns node item*/
static HItem HNodeItem(HNode* ptr);

/*returns node next element (null if none)*/
static HNode* HTnext(HNode* ptr);

/*set node key*/
static void HNodesetKey(HNode* ptr, Key key);

/*set node item*/
static void HNodesetItem(HNode* ptr, HItem item);

/*set node's next node*/
static void HNodesetNext(HNode* ptr, HNode* next);


/*insert node in begining of hashtable row*/
static HNode* insertNode(HNode* head, HItem item, Key key);

/*remove node from hashtable row*/
static HNode* removeNode(HNode* head, Key key);
