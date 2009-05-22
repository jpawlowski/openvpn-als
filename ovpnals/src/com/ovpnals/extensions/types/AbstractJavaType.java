/*
 */
package com.ovpnals.extensions.types;

import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import com.ovpnals.extensions.ExtensionDescriptor;
import com.ovpnals.extensions.ExtensionException;
import com.ovpnals.extensions.ExtensionType;
import com.ovpnals.security.SessionInfo;

/**
 * Abstract implementation of an {@link ExtensionType} that allows execution of
 * Java applications.
 */
public abstract class AbstractJavaType implements ExtensionType {

    static Log log = LogFactory.getLog(AbstractJavaType.class);

    // Private instance variables
    private String jre;
    private ExtensionDescriptor descriptor;
    private String typeName;
    private boolean canStop;

    /**
     * Constructor.
     * 
     * @param typeName type name
     */
    public AbstractJavaType(String typeName, boolean canStop) {
        this.typeName = typeName;
        this.canStop = canStop;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.extensions.ExtensionType#start(com.ovpnals.extensions.ExtensionDescriptor,
     *      org.jdom.Element)
     */
    public void start(ExtensionDescriptor descriptor, Element element) throws ExtensionException {
        this.descriptor = descriptor;
        if (element.getName().equals(typeName)) {

            jre = element.getAttribute("jre").getValue();

            if (jre == null) {
                throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
                    "<application> element requires attribute 'jre'");
            }

            try {
                ExtensionDescriptor.getVersion(jre);
            } catch (Throwable ex) {
                throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR, "Invalid value '" + jre
                    + "' specified for 'jre' attribute");
            }

            for (Iterator it = element.getChildren().iterator(); it.hasNext();) {
                Element e = (Element) it.next();

                if (e.getName().equalsIgnoreCase("classpath")) {
                    verifyClasspath(e);
                } else if (e.getName().equalsIgnoreCase("main")) {
                    verifyMain(e);
                } else {
                    throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR, "Unexpected element <"
                        + e.getName() + "> found in <application>");
                }
            }

        }

    }

    public void verifyRequiredElements() throws ExtensionException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.extensions.ExtensionType#isHidden()
     */
    public boolean isHidden() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.extensions.ExtensionType#getType()
     */
    public String getType() {
        return typeName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.extensions.ExtensionType#stop()
     */
    public void stop() throws ExtensionException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.extensions.ExtensionType#canStop()
     */
    public boolean canStop() {
        return canStop;
    }
    
    /* (non-Javadoc)
     * @see com.ovpnals.extensions.ExtensionType#active()
     */
    public void activate() throws ExtensionException {        
    }

    private void verifyClasspath(Element element) throws ExtensionException {
        for (Iterator it = element.getChildren().iterator(); it.hasNext();) {
            Element e = (Element) it.next();
            if (e.getName().equalsIgnoreCase("jar")) {
                descriptor.processFile(e);
            } else if (e.getName().equals("if")) {
                verifyClasspath(e);
            } else {
                throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR, "Invalid element <" + e.getName()
                    + "> found in <classpath>");
            }
        }
    }

    private void verifyMain(Element element) throws ExtensionException {
        for (Iterator it = element.getChildren().iterator(); it.hasNext();) {
            Element e = (Element) it.next();
            if (e.getName().equalsIgnoreCase("if")) {
                verifyMain(e);
            } else if (!e.getName().equalsIgnoreCase("env")  && !e.getName().equalsIgnoreCase("arg") && !e.getName().equalsIgnoreCase("jvm")) {
                throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR, "Unexpected element <" + e.getName()
                    + "> found in <main>");
            }
        }
    }

	/* (non-Javadoc)
	 * @see com.ovpnals.extensions.ExtensionType#descriptorCreated(org.jdom.Element)
	 */
	public void descriptorCreated(Element element, SessionInfo session) throws IOException {		
	}
}