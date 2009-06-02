package net.openvpn.als.vfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForward;

import net.openvpn.als.boot.Util;
import net.openvpn.als.core.AbstractDownloadContent;
import net.openvpn.als.core.filters.GZIPResponseWrapper;
import net.openvpn.als.policyframework.LaunchSession;
import net.openvpn.als.vfs.webdav.DAVServlet;
import net.openvpn.als.vfs.webdav.DAVUtilities;

/**
 * Implementation of {@link net.openvpn.als.core.AbstractDownloadContent} for
 * downloading an of VFS uri's.
 * <p>
 * The download will be presented as a zip archive with a filename of either the
 * only URI select or of the basename of the root folder (both suffixed with
 * .zip).
 */
public class ZipDownload extends AbstractDownloadContent {

    final static Log log = LogFactory.getLog(ZipDownload.class);

    private String[] uris;
    private String rootPath;
    private ActionForward messageForward;
    private int downloadCount;
    private LaunchSession launchSession;

    /**
     * @param launchSession launch session
     * @param messageForward forward to use to display message
     * @param rootPath The current location.
     * @param uris The Array of source uri's
     * @param forward The forward to go to after the operation.
     * @param messageKey The message key
     * @param messageResourcesKey The message resource key.
     */
    public ZipDownload(LaunchSession launchSession, ActionForward messageForward, String rootPath, String[] uris, ActionForward forward, String messageKey,
                       String messageResourcesKey) {
        super("application/x-zip", forward, messageKey, messageResourcesKey, null, null, null, null, null);
        this.uris = uris;
        this.launchSession = launchSession;
        this.rootPath = rootPath;
        this.messageForward = messageForward;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.core.DownloadContent#sendDownload(javax.servlet.http.HttpServletResponse,
     *      javax.servlet.http.HttpServletRequest)
     */
    public void sendDownload(HttpServletResponse response, HttpServletRequest request) throws Exception {
        if (response instanceof GZIPResponseWrapper) {
            ((GZIPResponseWrapper) response).setCompress(false);
        }
            
        OutputStream out = null;
        try {
            ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
            for (int i = 0; i < uris.length; i++) {
                VFSResource res = DAVServlet.getDAVResource(launchSession, request, response, rootPath + "/" + uris[i]);
                zipit(zos, res, "");
            }
            zos.close();
        } catch (IOException ioe) {
            throw ioe;
        } finally {
            downloadCount++;
            Util.closeStream(out);
        }
    }

    /**
     * @param zos The ZipOutputStream going to the destination.
     * @param res The DAVResource to zip
     * @param path The path after the current location.
     * @throws Exception 
     */
    private void zipit(ZipOutputStream zos, VFSResource res, String path) throws Exception {
        if (res.isCollection()) {
            Iterator children = res.getChildren();
            while (children.hasNext()) {
                FileObjectVFSResource childResource = (FileObjectVFSResource) children.next();
                zipit(zos, childResource, DAVUtilities.concatenatePaths(path, res.getDisplayName()));
            }
        } else {
            String zipPath = DAVUtilities.stripLeadingSlash(DAVUtilities.concatenatePaths(path, res.getDisplayName()));
            ZipEntry idEntry = new ZipEntry(zipPath);
            zos.putNextEntry(idEntry);
            InputStream in = res.getInputStream();
            try {
                byte[] buf = new byte[4096];
                int read;
                while (true) {
                    read = in.read(buf, 0, buf.length);
                    if (read == -1) {
                        break;
                    }
                    zos.write(buf, 0, read);
                }
            } catch (Exception e){
              log.error("Failed to read data from source.", e);  
              throw e;
            } finally {
                zos.closeEntry();
                in.close();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.core.DownloadContent#getFilename()
     */
    public String getFileName() {
        return (uris.length == 1 ? DAVUtilities.stripTrailingSlash(uris[0]) : DAVUtilities.basename(rootPath, '/')) + ".zip";
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.core.DownloadContent#getMessageForward()
     */
    public ActionForward getMessageForward() {
        return messageForward;
    }

    public int getDownloadCount() {
        return downloadCount;
    }
}