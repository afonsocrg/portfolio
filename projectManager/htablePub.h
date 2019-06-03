#ifndef _HASHTABLE_
#define _HASHTABLE_
#include "htableStruct.h"
#endif

/*create new hashtable*/
/*returns pointer to new hashtable*/
HNode** HTcreate();

/*free an entire key row*/
void HTfreeKey(HNode** root, Key key);

/*insert item to hashtable*/
void HTinsert(HNode** root, HItem item, Key key);

/*remove item from hashtable*/
void HTremove(HNode** root, Key key);

/*search for an item with key Key*/
/*returns item*/
HItem HTsearchKey(HNode** root, Key key);
