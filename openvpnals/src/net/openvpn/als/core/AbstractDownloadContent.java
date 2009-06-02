package net.openvpn.als.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForward;

import net.openvpn.als.security.SessionInfo;

/**
 * Abstract implementation of {@link net.openvpn.als.core.DownloadContent}
 * that provides methods likely to be common across all implementations.
 */
public abstract class AbstractDownloadContent implements DownloadContent {
    
    // Private instance variables

    private ActionForward forward;
    private String messageKey;
    private String messageResourcesKey;
    private int id;
    private String mimeType;
    private String messageArg0;
    private String messageArg1;
    private String messageArg2;
    private String messageArg3;
    private String messageArg4;

    /**
     * Constructor.
     *
     * @param mimeType
     * @param forward
     * @param messageKey
     * @param messageResourcesKey
     * @param messageArg0
     * @param messageArg1
     * @param messageArg2
     * @param messageArg3
     * @param messageArg4
     */
    public AbstractDownloadContent(String mimeType, ActionForward forward, String messageKey, String messageResourcesKey,
                    String messageArg0, String messageArg1, String messageArg2, String messageArg3, String messageArg4) {
        this.mimeType = mimeType;
        this.forward = forward;
        this.messageKey = messageKey;
        this.messageResourcesKey = messageResourcesKey;
        this.messageArg0 = messageArg0;
        this.messageArg1 = messageArg1;
        this.messageArg2 = messageArg2;
        this.messageArg3 = messageArg3;
        this.messageArg4 = messageArg4;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.core.DownloadContent#sendDownload(javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpServletRequest)
     */
    public abstract void sendDownload(HttpServletResponse response, HttpServletRequest request) throws Exception;

    /* (non-Javadoc)
     * @see net.openvpn.als.core.DownloadContent#completeDownload(net.openvpn.als.security.SessionInfo)
     */
    public void completeDownload(SessionInfo session) {
        // don't always need to override this.
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.core.DownloadContent#getId()
     */
    public int getId() {
        return this.id;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.core.DownloadContent#setId(int)
     */
    public void setId(int id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.core.DownloadContent#getForward()
     */
    public ActionForward getForward() {
        return this.forward;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.core.DownloadContent#getMessageKey()
     */
    public String getMessageKey() {
        return this.messageKey;
    }

    /**
     * Get the mime type for this download
     * 
     * @return mime type
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Set the mime type for this download
     * 
     * @param mimeType mime type
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.core.DownloadContent#getMessageResourcesKey()
     */
    public String getMessageResourcesKey() {
        return this.messageResourcesKey;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.core.DownloadContent#getMessageArg0()
     */
    public String getMessageArg0() {
        return this.messageArg0;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.core.DownloadContent#getMessageArg1()
     */
    public String getMessageArg1() {
        return this.messageArg1;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.core.DownloadContent#getMessageArg2()
     */
    public String getMessageArg2() {
        return this.messageArg2;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.core.DownloadContent#getMessageArg3()
     */
    public String getMessageArg3() {
        return this.messageArg3;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.core.DownloadContent#getMessageArg4()
     */
    public String getMessageArg4() {
        return this.messageArg4;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.core.DownloadContent#getMessageForward()
     */
    public ActionForward getMessageForward() {
        return new ActionForward("/showFileDownload.do", true);
    }
}
