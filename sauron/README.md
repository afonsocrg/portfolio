# Sauron (February - May 2020)

### Team members


| Name              | User                             |
|-------------------|----------------------------------|
| Afonso Gonçalves  | <https://github.com/afonsocrg>   |
| Daniel Seara      | <https://github.com/Beu-Wolf>    |
| Marcelo Santos    | <https://github.com/tosmarcel>   |


### Description
This Distributed Systems project challenged the team to develop a tracking system, called Sauron.

This system is composed of several cameras (Cam) that detect and identify those people and cars and report their observations to the Silo Server. There are also other entities (Spotter) who query the Silo Server about previous reports, to check one's last position or one's itinerary.

In the second iteration of this project, we replicated the Silo Server, to improve its availability and performance. We wanted to assure high availability and fault tolerance, so we needed to sacrifice the consistency (See Brewer's/CAP Theorem). We implemented an adaptation of the Gossip algorithm.

### Concurrency

Regarding the concurrent model, we decided to use thread-safe data structures for reading and writing. 
Declaring the functions as `synchronized` was worse because they wouldn't be fine-grained and could
introduce concurrency bottlenecks. It was used a `concurrentLinkedDeque` 
to store the reports and a `concurrentHashMap` to register the cameras.

