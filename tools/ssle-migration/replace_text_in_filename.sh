#!/bin/bash

usage() {
	echo	
	echo "Usage: replace_text_in_filename.sh -o original -n new -f filename"
	echo
	echo "Options:"
	echo "  -o  Original string"
	echo "  -n  New string"
	echo "  -f  Name of the file to rename"
	exit 1
}

# Parse the options

# We are run without parameters -> usage
if [ "$1" == "" ]; then
	usage
fi

while getopts "o:n:f:h" options; do
  case $options in
        o ) ORIGINAL=$OPTARG;;
        n ) NEW=$OPTARG;;
        f ) FILENAME=$OPTARG;;
        h ) usage;;
        \? ) usage;;
        * ) usage;;
  esac
done	

NEWFILENAME=`echo "$FILENAME"|sed s/"$ORIGINAL"/"$NEW"/g`

if [ "$NEWFILENAME" != "$FILENAME" ]; then
	mv "$FILENAME" "$NEWFILENAME"
fi
