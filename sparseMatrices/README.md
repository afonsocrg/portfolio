# Sparse Matrices (April 2018)

This university project consists of developing a program that manages sparse matrices. The interface of this project is a simple shell that recognizes 1 lettered commands and a few arguments.
The program can also load pre-saved matrices (filename sent as an argument while calling program)
The usage of dynamic memory was forbidden in this project.

## Internal representation
Since sparse matrices are very costly, we will represent them as an array of structures (which have a row, col and value) that represent the matrix cells.

## Command list
|Command|Arguments          | Description                                                               |
|:-----:|:-----------------:|:-------------------------------------------------------------------------:|
|a      |row col value      |Adds element to matrix                                                     |
|p      |       ----        |Lists every element in matrix                                              |
|i      |       ----        |Lists matrix limits and its density                                        |
|l      |line number        |Lists matrix line                                                          |
|c      |col number         |Lists matrix column                                                        |
|0      |collumn*           |Sort matrix values in array by its line or column value (according to argument)|
|z      |value              |defines new zero to the matrix                                             |
|s      |       ----        |Compress matrix                                                            |
|w      |   <filename>      |Saves matrix in file                                                       |
|q      |       ----        |Quit                                                                       |

arg* - optional argument
