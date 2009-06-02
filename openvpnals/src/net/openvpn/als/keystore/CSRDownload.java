package net.openvpn.als.keystore;

import java.io.File;

import org.apache.struts.action.ActionForward;

import net.openvpn.als.core.FileDownload;

/**
 * <p>
 * Extension of {@link net.openvpn.als.core.FileDownload} for
 * downloading a CSR.
 */
public class CSRDownload extends FileDownload {

    /**
     * @param file file
     * @param filename filename
     * @param mimeType The mimeType 
     * @param forward The forward to go to after the operation.
     * @param messageKey The message key
     * @param messageResourcesKey The message resource key.
     */
    public CSRDownload(File file, String filename, String mimeType, ActionForward forward, String messageKey,
                       String messageResourcesKey) {
        super(file, filename, mimeType, forward, messageKey, messageResourcesKey, null, null, null, null, null);
    }

    /**
     * @param file file
     * @param filename filename
     * @param mimeType The mimeType 
     * @param forward The forward to go to after the operation.
     * @param messageKey The message key
     * @param messageResourcesKey The message resource key.
     * @param messageArg1 The value for the first argument
     */
    public CSRDownload(File file, String filename, String mimeType, ActionForward forward, String messageKey,
                    String messageResourcesKey, String messageArg1) {
        super(file, filename, mimeType, forward, messageKey, messageResourcesKey, messageArg1, null, null, null, null);
    }
}
