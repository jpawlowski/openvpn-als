package com.adito.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForward;

import com.adito.security.SessionInfo;

/**
 * Core to the feature that allows files to be download for
 * various functions such as a private key downloads or VFS downloads, implementations
 * of this interface are responsible for provided the content to be downloaded.
 * <p>
 * For example, one implementation may get the downloadable content from a
 * local file, whilst another zips lots of resources to a single archive
 * and provides that for download.
 * <p>
 * Implementations must also provide some message resources that will
 * be displayed when at the time of file download.
 */
public interface DownloadContent {

    /**
     * Constant name for hook to delete temporary download 
     * files when session is invalidated
     */
    public final String FILES_DOWNLOAD_CLEANUP_SESSION_HOOK = "fileDownloadCleanupSessionHook";
    
	/**
     * Do the actual download. At this point the filename and content type
     * will already have been set on the response so all this method does
     * is get the downloadable input stream from somewhere and write it
     * to the {@link HttpServletResponse} objects output stream.
     * 
	 * @param response response that contains output stream to write to
	 * @param request request 
	 * @throws Exception on any error
	 */
	public void sendDownload(HttpServletResponse response, HttpServletRequest request)
			throws Exception;

    /**
     * Each download must have a unique id, this method returns it.
     * 
     * @return download id
     */
	public int getId();

    /**
     * Each download must have a unique id, this method sets it.
     * 
     * @param id download id
     */
	public void setId(int id);

    /**
     * Invoked when the download is complete, any clean up should be done
     * here.
     * @param session 
     */
	public void completeDownload(SessionInfo session);
	
    /**
     * Get the forward to direct to when the download is complete.
     * 
     * @return forward
     */
	public ActionForward getForward();
	
    /**
     * Get the message key for the resources to display to user on the
     * file download page.
     * 
     * @return message key
     */
    public String getMessageKey();

    /**
     * Get the resource bundle id from which to get the resources to display
     * to user on the file download page
     * 
     * @return message resources bundle id
     */
    public String getMessageResourcesKey();
    
    /**
     * Get the first argument to pass to the message to display to user on 
     * the file dfownload page.
     * 
     * @return first argument
     */
    public String getMessageArg0();

    /**
     * Get the second argument to pass to the message to display to user on 
     * the file download page.
     * 
     * @return second argument
     */
    public String getMessageArg1();

    /**
     * Get the third argument to pass to the message to display to user on 
     * the file download page.
     * 
     * @return third argument
     */
    public String getMessageArg2();

    /**
     * Get the fourth argument to pass to the message to display to user on 
     * the file download page.
     * 
     * @return fourth argument
     */
    public String getMessageArg3();

    /**
     * Get the fifth argument to pass to the message to display to user on 
     * the file download page.
     * 
     * @return fifth argument
     */
    public String getMessageArg4();
    
    /**
     * Get the mime type to return to the browser when downloaded. This will
     * affect the actions that are presented to the user (e.g. open with
     * application or save to disk etc).
     * 
     * @return mime type
     */
    public String getMimeType();
    
    /**
     * Get the filename to return to the browser when downloaded.
     * 
     * @return filename
     */
    public String getFileName();
    
    /**
     * Get the forward to use to show the download message
     * 
     * @return forward
     */
    public ActionForward getMessageForward();

    /**
     * Get the number of times this piece of content has been downloaded.
     * 
     * @return download count
     */
    public int getDownloadCount();

}