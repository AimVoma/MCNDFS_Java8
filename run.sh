#!/bin/bash

#BASH SCRIPT FOR EXECUTING ROUTINES


#Hardcoded Path for ndfs bash script
cmd=~/ndfs/bin/ndfs

#Hardcoded Path for promela inputs
cmd1=~/ndfs/input/

#Promela Files
files=( accept-cycle.prom bintree-cycle-single.prom simple-loop.prom bintree-converge.prom bintree-loop.prom tritree-cycle.prom bintree-cycle.prom bintree.prom tritree.prom )

#MCNDFS versions
versions=( 1_naive 2_ext 3_imprv_relock 4_imprv_rerwlock 5_imprv_cncrtmap 6_imprv_nprmttn )

#Nr Of Workers/Threads
workers=( 4 8 16 )

#Nr Of Cycles/Repetitions
cycles=(10)

for v in "${versions[@]}"; do
	for w in "${workers[@]}"; do
		for f in "${files[@]}"; do
			for c in "${cycles[@]}"; do
				# echo running with params: $f $v $w $c
				# echo RUN: "$cmd $cmd1/$f $cmd2/$v $w $c"
				# java -cp $cmd driver.Main $cmd1/$f $v $w $c
				$cmd $cmd1/$f $v $w $c
			done
		done
	done
done
