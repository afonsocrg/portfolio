#include <iostream>
#include <forward_list>
#include <queue>

// warehouse ids start with 0
#define WAREHOUSE_IN_INDEX(id) (numSuppliers+1 + (id << 1))
#define WAREHOUSE_OUT_INDEX(id) (numSuppliers+1 + (id << 1) + 1)

#define min(a, b) (a > b ? b : a)

//edge structure, keeps source, target, residual capacity and flow
struct Edge {
	int src;
	int tgt;
	int cf;
	int f;
};

// function declaration
void readInput();
void addSupplier(int& index, int& production);
void addWarehouse(int& in, int& capacity);
void addTransport(int& source, int& target, int& c);

void convertToIndex(int &source, int &target);

void addToNeighborList(const int &source, const int &target);

void relabelToFront();
void initializePreFlow();
void discharge(int u);
void relabel(int u);
void push(int u, Edge* edge);

void initData();

void calculateMinCut();
void solve();



// variables
int numSuppliers, numStations;
int numVertices; // num total vertices

int* h = NULL; // vertex heights
int* e = NULL; // vertex excess flow


std::queue<int> L; // manage vertices to be considered in Relabel-To-Front
std::forward_list<Edge*>* N = NULL; // neighbor list -> points to edges leaving and enterering vertex 
std::forward_list<Edge*>::iterator* current = NULL;	// current vertex in neighbor list to be considered

// cmp function to compare edges
bool cmpFunc(Edge* e1, Edge* e2){
	return (e1->tgt == e2->tgt ? e1->src < e2->src : e1->tgt < e2->tgt);
}



int main () {
	readInput();

	// calculate max flow
	relabelToFront();

	// calculate cut and print it
	solve();

	// data is cleaned on process death
	return 0;
}


void readInput() {
	int numEdges, i;
	int source, target, c;

	// first line: numbers
	scanf("%d %d %d", &numSuppliers, &numStations, &numEdges);
	numVertices = 2 + numSuppliers + 2*numStations;
	initData();

	// second line: suppliers
	for(i = 1; i <= numSuppliers; i++){
		scanf("%d", &c);
		addSupplier(i, c); 
	}

	// third line: warehouses
	for(i = 0; i < numStations; i++) {
		int ind = WAREHOUSE_IN_INDEX(i);
		scanf("%d", &c);
		addWarehouse(ind, c);
	}

	// other lines: transport
	for(i = 0; i < numEdges; i++) {
		scanf("%d %d %d", &source, &target, &c);
		convertToIndex(source, target);
		addTransport(target, source, c); // creating transposed graph
	}
}

// keeping similar functions to maintain abstraction
void addSupplier(int& supplierIndex, int& production) {
	// create edge
	Edge* e = new Edge;
	e->src = supplierIndex;
	e->tgt = 0;				// target will always be the same
	e->cf = production;
	e->f = 0;

	// add edge to neighbour
	N[supplierIndex].push_front(e);
}

void addWarehouse(int& inIndex, int& capacity) {
	// create edge
	Edge* e = new Edge;
	e->src = inIndex+1;			// warehouse output vertex
	e->tgt = inIndex;
	e->cf = capacity;
	e->f = 0;

	// add to neighbour list
	N[inIndex].push_front(e);
	N[inIndex + 1].push_front(e);
}

void addTransport(int& source, int& target, int& c) {
	// create edge
	Edge* e = new Edge;
	e->src = source;
	e->tgt = target;
	e->cf = c;
	e->f = 0;

	// add to neighbour list
	N[source].push_front(e);
	N[target].push_front(e);
}


void convertToIndex(int &source, int &target) {
	// transform source
	if(source == 1)				// hipermarket
		source = numVertices-1;
	else if(source < numSuppliers+2)	// supplier
		--source;
	else {					// warehouse as source
		source = WAREHOUSE_OUT_INDEX((source-numSuppliers-2));
	}

	// transform target
	if(target == 1)				// hipermarket
		target = numVertices-1;
	else if(target < numSuppliers+2)	// supplier
		--target;
	else					// warehouse as target
		target = WAREHOUSE_IN_INDEX((target-numSuppliers-2));
}


// main algorithm
void relabelToFront(){	
	int u;
	initializePreFlow();

	for(int i = 1; i < numVertices - 1; i++) // init current neighbours
		current[i] = N[i].begin();

	// FIFO use -> pop first vertex with excess and discharge it
	while (!L.empty()){	
		u = L.front();
		L.pop();
		discharge(u);
	}
}

void push(int u, Edge* edge) {
	int flow = min(e[u], (u == edge->src ? edge->cf : edge->f));
	if(u == edge->tgt) flow = -flow; 	// removing flow from edge

	edge->f += flow;
	edge->cf -= flow;
	e[edge->src] -= flow;
	e[edge->tgt] += flow;
}

void relabel(int u){
	unsigned int minH = -1;

	// get min height of admissible neighbours
	for (auto it = N[u].begin(); it != N[u].end(); ++it){
		// u is source
		if(u == (*it)->src && (*it)->cf != 0)
			minH = min(minH, (unsigned int) h[(*it)->tgt]);

		// u is target
		else if (u == (*it)->tgt && (*it)->f != 0)
			minH = min(minH, (unsigned int) h[(*it)->src]);
	}

	h[u] = 1 + minH;
}

void initializePreFlow() {
	h[numVertices-1] = numVertices;
	e[numVertices-1] = 0;

	for (int i = 0; i < numVertices-1; i++){
		h[i] = 0;
		e[i] = 0;
	}

	// flows already initialized to 0 when building edge list

	for(std::forward_list<Edge*>::iterator it = N[numVertices-1].begin(); it != N[numVertices-1].end(); ++it){
		Edge* edge = (*it);

		e[edge->tgt] = edge->cf;
		edge->f = edge->cf;
		edge->cf = 0;
		e[numVertices-1] -= edge->f; 

		// First vertices to be considered
		L.push(edge->tgt);
	}
}

void discharge(int u) {
	while(e[u] > 0) {	
		if(current[u] == N[u].end()) {
			relabel(u);						// if there are no possible edges to discharge to, relabel
			current[u] = N[u].begin();
		} else {
			Edge* edge = *(current[u]);
			
			if(u == edge->src ? (edge->cf > 0 && h[u] == h[edge->tgt]+1) : (edge->f > 0 && h[u] == h[edge->src]+1)) {
				push(u, edge);
				if(u == edge->src ? (edge->tgt != 0) : (edge->src != 0))
					L.push(u == edge->src ? (edge->tgt) : (edge->src));
			} else
				(current[u])++;
		}
	}
}


void initData() {
	h = new int[numVertices];
	e = new int[numVertices];

	// L will be initialized in Relabel-To-Front
	current = new std::forward_list<Edge*>::iterator[numVertices];
	N = new std::forward_list<Edge*>[numVertices];
}


// since we are working with transposed graphs, all edges need to be reversed, and vertices ids must be recalculated
void solve(){
	bool firstPost = true;
	std::forward_list<Edge*> edgeCut;

	// line 1 -> maxFlow
	printf("%d\n", e[0]);

	// line 2 -> warehouses
	for(int i = numSuppliers+1; i < numVertices-1; i=i+2){
		if((h[i] >= h[numVertices-1]) != (h[i+1] >= h[numVertices-1])){
			if(firstPost){
				firstPost = false;
				printf("%d", (i-(numSuppliers+1))/2 + numSuppliers+2);		// recalculation of warehouse id
			} else
				printf(" %d", (i-(numSuppliers+1))/2 + numSuppliers+2);
		}
	}
	printf("\n");


	// line 3 -> edges

	// edges from suppliers to warehouses/hipermarket
	for(int i = 1; i < numSuppliers+1; i++){
		if(h[i] < h[numVertices-1]){
			for(std::forward_list<Edge*>::iterator it = N[i].begin(); it != N[i].end(); it++){
				if(h[(*it)->src] >= h[numVertices-1])
					edgeCut.push_front(*it);
			}
		}
	}

	// edges from warehouses to warehouses/hipermarket
	for(int i = numSuppliers+2; i < numVertices-1; i = i+2){
		if(h[i] < h[numVertices-1]){
			for(std::forward_list<Edge*>::iterator it = N[i].begin(); it != N[i].end(); ++it){
				if((i == (*it)->src && h[(*it)->tgt] >= h[numVertices-1])
				|| (i == (*it)->tgt && h[(*it)->src] >= h[numVertices-1]))
					edgeCut.push_front(*it);
			}
		}
	}


	// convert all edges in edgeCut to their original ids
	for(std::forward_list<Edge*>::iterator ite = edgeCut.begin(); ite != edgeCut.end(); ++ite){
		if((*ite)->src == numVertices-1) (*ite)->src = 1;
		else (*ite)->src = ((*ite)->src - (numSuppliers+1))/2 + numSuppliers+2; 

		if((*ite)->tgt < numSuppliers+1) (*ite)->tgt++;
		else (*ite)->tgt = ((*ite)->tgt - (numSuppliers+1))/2 + numSuppliers+2; 
	}

	// sort Edges using cmpFunc
	edgeCut.sort(cmpFunc);
	for(std::forward_list<Edge*>::iterator it = edgeCut.begin(); it != edgeCut.end(); ++it)
		printf("%d %d\n", (*it)->tgt, (*it)->src);
}
