# Max Flows (April - May 2019)

Made with love with [my boy <3](https://github.com/Beu-Wolf).

This project consists of a transportation network analysis. This network ensures merchandise transportation between several producers and one single hypermarket. Between these points, there are many warehouses (with limited space) that can store the products.
Our goal is to find out the network capacity as well as which transport connections and warehouses must have it increased in order to maximize the network flow.

This is a maximum flow problem. We solved it by modelling this network using a graph and applying Relabel-to-Front algorithm, so we could find the Maximum Flow and the Minimum Cut (more details [here](https://github.com/afonsocrg/portfolio/blob/master/maxFlows/maxFlowsReport.pdf)).
In this project, we had rough memory and time restrictions, which made us think of alternative ways to implement our algorithms, based on this specific problem.
