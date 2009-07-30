#!/bin/bash


# Cycle through new maven2 directories:

for i in activedirectory agent agent-awt agent-swt applications clientcerts ldap maverick-crypto maverick-multiplex maverick-ssl maverick-util networkplaces pam radius tunnels ui unix webapp webforwards; do
	echo
	echo "Checking $i"
	echo

	for PATTERN in adito-agent adito-agent-awt adito-agent-swt adito-commons-vfs adito-community-activedirectory adito-community-applications adito-community-network-places adito-community-pam adito-community-tunnels adito-community-unix adito-community-web-forwards; do
		find $i -iname "*$PATTERN*"|grep -v ".svn"
		grep -ri $PATTERN $i/*|grep -v ".svn"
	done

	echo
	echo "Done checking $i, press return to continue"
	echo
	read yn
done
