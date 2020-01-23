# Scotland Yard Bot (October 2019)
This project consisted of finding the cheapest possible path between two points in a given map. Since we needed it to be fast and optimal and we had a consistent heuristic (explained in more detail further), we chose to implement A*.

### Modeling the problem
The map was based on the Scotland Yard board game (hence the project name), so its connections were based on the available transportations to the corresponding targets. This way,  the map would be represented by a graph, having each intersection/point as a vertex and each transport connection as an edge. However, this wasn't a traditional graph: we had to distinguish between every type of transportation (taxi, bus, subway and boat) since the agents would have limited tickets for each one.

There was also an additional complexity factor: The problem could have multiple agents, each one trying to reach its destination. They shared the available tickets and one agent could not be in the same location as the other one simultaneously nor could stay in the same location in two consecutive moves.
We modelled this concept as a super agent, with `N` legs, each leg being one agent. This super-agent would walk in a state-space that corresponded to the combination of every "leg" state-space. After permutating every possibility we would filter out the ones that were illegal in this scope.

### Heuristic used
Since the optimal path is the one that uses the less amount of tickets and each edge uses a single ticket, we can approximate the optimal solution to the shallowest path (ignoring the edges in which the agent has no more tickets available). Running a BFS from the target vertex will give us the minimum number of vertices that one agent has to go through to reach the destination. This number was used as the vertex heuristic.

This is an **admissible** heuristic since it never exceeds the optimal cost (in the best case, the agent follows the shortest path). It is also really close to the optimal cost: With an infinite amount of tickets, the algorithm will surely find the optimal path on its first try.

This heuristic is also **consistent**: Since the heuristic is the minimum distance to the target, and each edge has a unitary cost, we have `h(N) = c(N, P)+h(P)` for each vertex `N` and each neighbour `P`. This way we always preserve the consistency condition: `h(N) <= c(N,P)+h(P)`.

### Directory Structure and Usage
Run the project with `python3 go.py` in the project directory. This file runs several tests, using the developed solution (imported from `solve.py`). To better understand what the program was doing, we wrote some scripts that allowed us to see vertex adjacencies (`adjacency.sh` would filter the output from `listAdjacent.txt`, produced by`parseGraph.py`).

### Final notes
The A* algorithm itself was implemented easily and quickly. It took, however, several attempts to find the best heuristic: minor changes/tweaks affected very significantly the performance of the agent. This was surely a good way to see the importance of a good heuristic.
While trying to make the code run as fast as possible, we learned about list comprehension (it reduced several seconds in the runtime) and some python built-in functions. These optimizations made our project one of the 20 fastest of the entire course, granting us a bonus.
