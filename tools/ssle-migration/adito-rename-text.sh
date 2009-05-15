#!/bin/bash
#
# This is run with the original filename as an argument. If you need convert
# many files, you should use "find" similarly to this:
#
# find /directory -type f -exec ./adito-rename-text.sh {} \;

# Check if this file matches, skipping binary files. If it does not match, then exit.
grep -i --binary-files=without-match explorer $1 > /dev/null
if [ $? -ne 0 ]; then
	exit
fi

# Remove most variants of SSL-Explorer's name and references to 3sp
cat $1 |sed s/"sslexplorer"/"adito"/g|sed s/SSL-Explorer/Adito/g|sed s/"SSL Explorer"/"Adito"/g|sed s/sslExplorer/adito/g|sed s/SSLEXPLORER/ADITO/g|sed s/SSLExplorer/Adito/g|sed s/ssl-explorer/adito/g|sed s/SslExplorer/Adito/g|sed s/SSl-Explorer/Adito/g|sed s/SSL-explorer/Adito/g|sed s/3sp\.com/localhost/g > $1.new

mv $1.new $1
rm -f $1.new
