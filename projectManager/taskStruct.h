typedef struct task{

	unsigned long id;		/*ID*/
	unsigned long duration;	/*durration*/
	unsigned long early;	/*early start*/
	unsigned long late;		/*late start*/
	char* description;		/*description*/

	/*Links to other tasks
	accordingly to their
	correlation*/
	struct task **depends;	/*list of dependencies*/
	struct task **precedes;	/*list of precedences*/
	int numDepend;			/*len of dep. list*/
	int numPrecede;			/*len of prec. list*/

} Task;
