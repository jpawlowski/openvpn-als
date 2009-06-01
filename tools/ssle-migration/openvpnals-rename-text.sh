#!/bin/bash
#
# This is run with the original filename as an argument. If you need convert
# many files, you should use "find" similarly to this:
#
# find /directory -type f -exec ./openvpnals-rename-text.sh {} \;

# Check if this file matches, skipping binary files. If it does not match, then exit.
grep -i --binary-files=without-match explorer $1 > /dev/null
if [ $? -ne 0 ]; then
	exit
fi

# Remove most variants of SSL-Explorer's name and references to 3sp
cat $1 |sed s/"sslexplorer"/"openvpnals"/g|sed s/SSL-Explorer/OpenVPN-ALS/g|sed s/"SSL Explorer"/"OpenVPN-ALS"/g|sed s/sslExplorer/openvpnals/g|sed s/SSLEXPLORER/OpenVPN-ALS/g|sed s/SSLExplorer/OpenVPN-ALS/g|sed s/ssl-explorer/openvpnals/g|sed s/SslExplorer/OpenVPN-ALS/g|sed s/SSl-Explorer/OpenVPN-ALS/g|sed s/SSL-explorer/OpenVPN-ALS/g|sed s/3sp\.com/localhost/g > $1.new

mv $1.new $1
rm -f $1.new
