package net.openvpn.als.vfs.utils;

import net.openvpn.als.boot.Util;
import net.openvpn.als.vfs.utils.URI.MalformedURIException;


/**
 * URIUserInfo allows to slit all information of a network placement (host, path, userinfo, ...)
 */
public class URIUserInfo {
    
    private String host;
    private String path;
    private int port;
    private String username;
    private String password;
    private String userInfo;
    private String uri;
    
    public URIUserInfo(String stScheme, String host, String stUri, int port, String stUsername, String stPassword) throws MalformedURIException {
           
        try {
            URI uri;

            if ("file".equals(stScheme)) {
                stUri.replace("://", ":///");
                uri = new URI((!stUri.startsWith(stScheme + ":///") ? stScheme + ":///" : "") + stUri);
            } else {
                host = host.replace("\\\\", "").replace("\\", "").replace("/", "");
                stUri = stUri.replace("\\\\", "").replace("\\", "/");
                uri = new URI((!stUri.startsWith(stScheme + "://") ? stScheme + "://" : "") + (!host.equals("") ? host + "/" : "") + stUri);
            }
            
            setUri(uri.toString());
            String userInfo = uri.getUserinfo();
            setUserInfo(userInfo);
            setHost(uri.getHost());
            if (null != uri.getPath() && uri.getPath().startsWith("/")) {
                setPath(uri.getPath().substring(1));
            } else {
                setPath(uri.getPath());
            }
            
            if (port > 0) {
                setPort(port);
            } else if (uri.getPort() >= 0) {
                setPort(uri.getPort());
            } else {
                setPort(0);
            }
            if (!"".equals(stUsername)) {
                setUsername(stUsername);
                if (!"".equals(stPassword))
                    setPassword(stPassword);
            } else if (userInfo != null && !userInfo.equals("")) {
                String username = null;
                String pw = "";
                userInfo = Util.urlDecode(userInfo);
                int idx = userInfo.indexOf(":");
                username = userInfo;
                if (idx != -1) {
                    username = userInfo.substring(0, idx);
                    pw = userInfo.substring(idx + 1);
                }
                setUsername(username);
                setPassword(pw);
            }
        } catch (MalformedURIException e) {
            throw e;
        }
    }
       
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
    
    
}
