# Thermometers puzzle solver (April 2018)
A Thermometers puzzle consists of a board full of thermometers. The main goal is to "fill the thermometers with mercury, such that each row and column have the correct number of filled cells. These numbers are given with the puzzle. Mercury always starts filling from the base (circular part) of a thermometer, towards the top. This does not depend on the actual orientation of the thermometer (some are upside down)."

This prolog program uses logic to solve these puzzles, given the following argument:
[
[List of thermometers],
[Line Limits],
[Column Limits]
]

Each thermometer is a list of positions (coordinates). Its first element corresponds to its base.
