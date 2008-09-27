#!/bin/bash
# This read the "convlist" file, goes through it
# line by line and converts old variable names
# to new one in the most inefficient way possible :).
# But it works.

cat variables|while read line; do

	ORIG=$(echo $line|cut -d "=" -f 1)
	NEW=$(echo $line|cut -d "=" -f 2)

	cat $1|sed s/"$ORIG"/"$NEW"/g > $1.new
	cp -f $1.new $1
	rm -f $1.new

done
