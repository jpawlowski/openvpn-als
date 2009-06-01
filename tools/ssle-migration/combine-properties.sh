#!/bin/bash

# This is brutal script that helps create a single
# property file from several sources. It's useful
# when migrating translations from SSL-Explorer
# *.properties files into an OmegaT TMX file using
# properties_import tool (see wiki). Use as follows:
#
# ./combine-properties.sh dir_with_english_properties dir_with_translated_properties workdir
#
# e.g.
#
# ./combine-properties.sh /home/user/english /home/user/french /home/user
#
# Rename the resulting all_xx_XX.properties as required (e.g. all_fr_FR.properties)

if [ "$1" == "" ]; then

	echo
	echo "Usage:"
	echo
	echo "  combine-properties.sh dir_with_english_properties dir_with_translated_properties workdir"
	echo
	exit 1
fi

# Create filelists for later appending
find $1 -name "*.properties" > $3/filelist.txt
find $2 -name "*.properties" > $3/filelist_xx_XX.txt

cat $3/filelist.txt|while read FILE; do
	cat $FILE >> $3/all.properties
done

cat $3/filelist_xx_XX.txt|while read FILE; do
	cat $FILE >> $3/all_xx_XX.properties
done
