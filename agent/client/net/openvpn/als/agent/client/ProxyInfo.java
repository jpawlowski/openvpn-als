package net.openvpn.als.agent.client;

/**
 * Describes the location and authenitcation details of a single proxy
 * server.
 */
public class ProxyInfo {
    
    //  Private instance variables
    private String protocol;
    private String username;
    private String password;
    private String hostname;
    private int port;
    private String sourceIdent;    
    private boolean isActiveProfile = false;
    

    /**
     * Constructor.
     *
     * @param protocol
     * @param username
     * @param password
     * @param hostname
     * @param port
     * @param sourceIdent
     */
    public ProxyInfo(String protocol, String username, String password, String hostname, int port, String sourceIdent) {
        super();
        this.protocol = protocol;
        this.username = username;
        this.password = password;
        this.hostname = hostname;
        this.port = port;
        this.sourceIdent = sourceIdent;
    }

    /**
     * Get the proxy server information as a URI. This includes the protocl,
     * host, port, username and password.
     * 
     * @return proxy server information as a URI
     */
    public String toUri() {
        StringBuffer buf = new StringBuffer(getProtocol());
        buf.append("://"); //$NON-NLS-1$
        if(username != null && !username.equals("")) { //$NON-NLS-1$
            buf.append(username);
            if(password != null && !password.equals("")) { //$NON-NLS-1$
                buf.append(":"); //$NON-NLS-1$
                buf.append(password);
            }
            buf.append("@"); //$NON-NLS-1$
        }
        buf.append(hostname);
        if(port != 0) {
            buf.append(":"); //$NON-NLS-1$
            buf.append(port);
        }
        return buf.toString();
    }

    /**
     * Get to hostname or IP address of the proxy server
     * 
     * @return hostname or IP address of the proxy server
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Get the password to use for proxy server authentication
     * 
     * @return password to use for proxy server authentication
     */
    public String getPassword() {
        return password;
    }

    /**
     * Get the port on which the proxy server is running
     * 
     * @return port on which the proxy server is running
     */
    public int getPort() {
        return port;
    }

    /**
     * Get the protocol on which the proxy server is running.
     * 
     * @return protocol on which the proxy server is running.
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Get the source ident
     * 
     * CHECK Whats this?
     * 
     * @return source ident
     */
    public String getSourceIdent() {
        return sourceIdent;
    }

    /**
     * Get the username to use for proxy server authentication
     * 
     * @return username to use for proxy server authentication
     */
    public String getUsername() {
        return username;
    }

    public boolean isActiveProfile() {
        return isActiveProfile;
    }

    public void setActiveProfile(boolean isActiveProfile) {
        this.isActiveProfile = isActiveProfile;
    }
}
