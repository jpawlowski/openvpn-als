#!/bin/bash
# This read the file given as the second parameter,
# goes through it line by line and converts old
# variable names to new one in the most
# inefficient way possible, but it works, kind of
# :)
#
# Example usage:
#
# convert_variable_names.sh File.java file_with_replacements

cat $2|while read line; do

	# Skip lines that begin with # or are empty
	echo "$line"|grep -E "(^$|^#.*$)"

	if [ $? -ne 0 ]; then

		# First take care of ${variable.name}
		# instances
		ORIG=\{$(echo $line|cut -d "=" -f 1)\}
		NEW=\{$(echo $line|cut -d "=" -f 2)\}

		cat $1|sed s/"$ORIG"/"$NEW"/g > $1.new
		cp -f $1.new $1
		rm -f $1.new

		# Next take care of the actual
		# definitions (name="variable.name")
		# to see which have been replaced
		ORIG=\"$(echo $line|cut -d "=" -f 1)\"
		NEW=\"$(echo $line|cut -d "=" -f 2)\"

		cat $1|sed s/"$ORIG"/"$NEW"/g > $1.new
		cp -f $1.new $1
		rm -f $1.new

	fi
done
