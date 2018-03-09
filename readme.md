# Multi-core nested depth-first search in Java 8

## Purpose of this project is to implement multithreading depth-first search algorithm based on Laarman Paper
* http://www.cs.vu.nl/~tcs/cm/ndfs/laarman.pdf

### Introduction Notes
First we need to thouroughly run through Laarman's paper and formulate our multithreading prototype model based
on paper's pseudocode.
After we done with the 'naive' version of the algorithm we are going to start improving it, step by step adding 
more functionality and optimal parameters(See Red&Black States).
Furthermore our final goal is to come up with 4 Improvements on top of already 'improved' version of MCNDFS algorithm,
and cross-test our improved versions

### Technical Background
* Volatile Variables(Atomic Read - Writes, Preserve order execution)
* Callable interface provide convenient way of utilizing classes instances via multithreading setup
* ExecutorService provide us fixed Thread Pool on demand in order to interleave class instances  
* ExecutorCompletionService provides us the <Future> object in a style of asynchronous callback and completion order

### Improvements Over 'Naive' MCNDFS:
We came out with 4 alternatives of how would we make our original prototype more optimized
* Utilizing Reentrant Lock (Imprv1)
* Utilizing ReentrantReadWriteLock (Imprv2)
* Utilizing Concurrent Hash Map (Imprv3)
* Utilizing Less Explored Areas of the graph (Imprv4)

### Testing - Debugging Tools:
* VisualVM (Detects deadlocks, parking threads)
* jdb

### Running Environment
* DAS4 Cluster

### Automation Scripts
* Bash Automated Script for running combination of different commands + MCNDFS versions + Promela Files + NoWorkers + Cycles
* Each of the executions of combinations of <workers>, <mcndfs>, <promela files>, <repetition cycles> are automatically logged in
* Python Script to refine the logged data and perform statistic metrics
* Another Python script in order to revisit the refined data and perform visuals on our performance data results



