# networkAnalysis (March 2019)

In this project, we ([Daniel](https://github.com/Beu-Wolf) and me) were challenged to analyse a router network. There's a chance that some routers won't be able to reach others upon the removal of some critical routers. We had to find all those routers and also tell how many sub-networks would exist if they were removed.

This is, of course, a graph problem, so we used Tarjan's algorithm to find all the articulation points (critical routers). Since we had memory and time restrictions, we were forced to adjust the used algorithms to this specific problem.

More details [here](https://github.com/afonsocrg/networkAnalysis/blob/master/networkAnalysisReport.pdf)
