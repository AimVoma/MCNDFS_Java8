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

### Programming Skills
 * Java 8(beating heart of multithreading)  
 * Bash(console commands automation)
 * Python(Data Operations, Visualizations)

### Running Environment
* DAS4 Cluster

### Automation Scripts
* Bash Automated Script for running combination of different commands + MCNDFS versions + Promela Files + NoWorkers + Cycles
* Each of the executions of combinations of [workers], [mcndfs],[promela files], [Loop cycles] are automatically logged in
* Python Script to retrieve the logged data and perform visuals on each running instance
* Second Python script that aggregates the results and performs metrics per model (For all the executions, Summary)

### Pipeline steps
* ./run.sh script, intermediate csv files are generated
    * traverse_vis.py, automatically converting all the .csv data to images(saved locally)
        * aggregate_vis.py, performs the mean value calculation of all the intermediate files/models

### Run Command
./run.sh ( Automatically Generates all the pipeline intermediate steps and outcomes -- Full Automation )

### Models Average Running Time On Das4
* 3 hours, 40 mins


