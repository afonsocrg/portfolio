# Project Manager (May 2018)

This C project is about building a project manager, in which it's possible to manage tasks and dependencies, to calculate the early and the late start of each task and to find the critical path (the critical set of tasks that have to be completed without delays).
Tasks were saved in a linked list, that preserved their insertion order and helped in navigating through them. It was also implemented a Hash Table, in order to enhance the task search speed.
[Note]: In this project, I tried to keep some more abstraction and better file organization.

## Internal representation
Each task is represented by a struct with:
 - ID
 - Description
 - Duration
 - Dependencies (array of pointers to other tasks that precede the current one)
 - Precedences (array of pointers to tasks that depend on the current one)

## Command list
|Command|Arguments          | Description                                                               |
|:-----:|:-----------------:|:-------------------------------------------------------------------------:|
|add|<id> <description> <duration> <dependID_1*> ... <dependID_n*>|Add task to project with argument specifications|
|duration|<time*>|List tasks with duration greater than argument|
|depend|<ID>|List tasks that depend on the given one|
|remove|<ID>|Remove task from project|
|path|----|Calculate critical path|
|exit|----|Exit program|

<arg*> - Optional argument
