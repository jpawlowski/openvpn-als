#!/bin/bash
# Rename files and directories. Please make sure that replace_text_in_filename.sh
# is in PATH

# Move down one directory level at a time so that nothing breaks when moving things around.
for LEVEL in 1 2 3 4 5 6 7 8 9; do
	find . -maxdepth $LEVEL -exec replace_text_in_filename.sh -o sslexplorer -n openvpnals -f {} \; 
done

exit
