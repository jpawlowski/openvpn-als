
				/*
 *  Adito
 *
 *  Copyright (C) 2003-2006 3SP LTD. All Rights Reserved
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
			
package com.adito.util;

import java.io.EOFException;

/**
 */
public class CIDRNetwork {

    private final String network;
    private int networkBits;
    private String networkAddress;
    private String subnetMask;
    private String broadcastAddress;
    private int[] net;
    private int[] subnet;
    private String lastIP;

    /**
     * Default constructor
     * 
     * @param network
     * @throws IllegalArgumentException
     */
    public CIDRNetwork(String network) throws IllegalArgumentException {
        int index = network.indexOf("/");
        if (index == -1)
            index = network.indexOf("\\");

        if (index == -1)
            throw new IllegalArgumentException("CIDR network should be in the format 192.168.1.0/24");

        try {
            networkBits = Integer.parseInt(network.substring(index + 1)) - 1;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("CIDR network setting invalid! " + network);
        }

        this.network = network;
        subnet = IPUtils.createMaskArray(networkBits);
        net = IPUtils.getByteAddress(network.substring(0, index));
        net = IPUtils.calcNetworkNumber(net, subnet);

        broadcastAddress = IPUtils.createAddressString(IPUtils.calcBroadcastAddress(net, networkBits));
    }
    
    public CIDRNetwork(String ipAddress, String subnetMask) {
    	
    	subnet = IPUtils.getByteAddress(subnetMask);
    	int[] ip = IPUtils.getByteAddress(ipAddress);
    	net = IPUtils.calcNetworkNumber(ip, subnet);
        networkAddress = IPUtils.createAddressString(net);
        subnetMask = IPUtils.createAddressString(subnet);
        
	if(subnetMask.equals("255.0.0.0"))
			networkBits = 7;
	else if(subnetMask.equals("255.128.0.0"))
			networkBits = 8;
		else if(subnetMask.equals("255.192.0.0"))
			networkBits = 9;
		else if(subnetMask.equals("255.224.0.0"))
		networkBits = 10;
		else if(subnetMask.equals("255.240.0.0"))
			networkBits = 11;
		else if(subnetMask.equals("255.248.0.0"))
			networkBits = 12;
		else if(subnetMask.equals("255.252.0.0"))
			networkBits = 13;
		else if(subnetMask.equals("255.254.0.0"))
			networkBits = 14;
		else if(subnetMask.equals("255.255.0.0"))
			networkBits = 15;
		else if(subnetMask.equals("255.255.128.0"))
			networkBits = 16;
		else if(subnetMask.equals("255.255.192.0"))
			networkBits = 17;
		else if(subnetMask.equals("255.255.224.0"))
			networkBits = 18;
		else if(subnetMask.equals("255.255.240.0"))
			networkBits = 19;
		else if(subnetMask.equals("255.255.248.0"))
			networkBits = 20;
		else if(subnetMask.equals("255.255.252.0"))
			networkBits = 21;
		else if(subnetMask.equals("255.255.254.0"))
			networkBits = 22;
		else if(subnetMask.equals("255.255.255.0"))
			networkBits = 23;
		else if(subnetMask.equals("255.255.255.128"))
			networkBits = 24;
		else if(subnetMask.equals("255.255.255.192"))
			networkBits = 25;
		else if(subnetMask.equals("255.255.255.224"))
			networkBits = 26;
		else if(subnetMask.equals("255.255.255.240"))
			networkBits = 27;
		else if(subnetMask.equals("255.255.255.248"))
			networkBits = 28;
		else if(subnetMask.equals("255.255.255.252"))
			networkBits = 29;
		
    	network = networkAddress + "/" + getNetworkBits();
    	broadcastAddress = IPUtils.createAddressString(IPUtils.calcBroadcastAddress(net, networkBits));
    }

    /**
     * @param startAddress
     * @param endAddress
     * @return String
     * @throws EOFException
     */
    public String getNextIPAddress(String startAddress, String endAddress) throws EOFException {
        if (lastIP == null && (startAddress == null || "".equals(startAddress))) {
            lastIP = IPUtils.createAddressString(IPUtils.calcFirstAddress(net, subnet));
        } else if (lastIP == null) {
            lastIP = startAddress;
        } else {
            if (endAddress == null || "".equals(endAddress)) {
                String addressString = IPUtils.createAddressString(IPUtils.calcLastAddress(net, networkBits));
                if (lastIP.equals(addressString))
                    throw new EOFException("No more IPs available");
            } else {
                if (lastIP.equals(endAddress))
                    throw new EOFException("No more IPs available");
            }

            lastIP = IPUtils.createAddressString(IPUtils.nextAddress(IPUtils.getByteAddress(lastIP)));
        }

        return lastIP;
    }

    /**
     * @return network address
     */
    public String getNetworkAddress() {
        return networkAddress;
    }

    /**
     * @return network bits
     */
    public int getNetworkBits() {
        return networkBits + 1;
    }

    /**
     * @return subnet mask
     */
    public String getSubnetMask() {
        return subnetMask;
    }

    /**
     * @return broadcast address
     */
    public String getBroadcastAddress() {
        return broadcastAddress;
    }

    /**
     * @return CIDR string
     */
    public String getCIDRString() {
        return network;
    }

    public String toString() {
        return getCIDRString();
    }

    /**
     * @param startAddress
     * @param endAddress
     * @return true if its valid
     */
    public boolean isValidDHCPRange(String startAddress, String endAddress) {
        if (!isValidAddressForNetwork(startAddress))
            return false;

        if (!isValidAddressForNetwork(endAddress))
            return false;

        int[] addressOneBytes = IPUtils.getByteAddress(startAddress);
        int[] addressTwoBytes = IPUtils.getByteAddress(endAddress);
        boolean valid = false;
        for (int index = 0; index < addressOneBytes.length; index++) {

            if ((addressOneBytes[index] ^ subnet[index]) < (addressTwoBytes[index] ^ subnet[index])) {
                valid = true;
                break;
            }
        }
        return valid;
    }

    /**
     * @param address
     * @return true if its valid
     */
    public boolean isValidAddressForNetwork(String address) {
        try {
        	if(address.equals(networkAddress))
        		return false;
        	
            int[] bytes = IPUtils.getByteAddress(address);

            boolean valid = true;
            // Check the network address against the subnet mask
            for (int index = 0; index < bytes.length; index++) {
                int subnetValue = subnet[index];
                if (subnetValue == 0)
                    break;

                if ((bytes[index] & subnetValue) != net[index]) {
                    valid = false;
                    break;
                }
            }
            return valid;
        } catch (IllegalArgumentException iae) {
            return false;
        }
    }
}