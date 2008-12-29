package com.adito.extensions.types;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import com.adito.boot.ContextHolder;
import com.adito.boot.SystemProperties;
import com.adito.boot.Util;
import com.adito.extensions.ExtensionDescriptor;
import com.adito.extensions.ExtensionException;
import com.adito.extensions.ExtensionType;
import com.adito.language.Language;
import com.adito.language.LanguagePackDefinition;
import com.adito.language.LanguagePackManager;
import com.adito.policyframework.Resource.LaunchRequirement;
import com.adito.security.SessionInfo;

/**
 * Extension type that adds a new language pack.
 */
public class LanguagePackType implements ExtensionType {

    final static Log log = LogFactory.getLog(LanguagePackType.class);

    /**
     * Type name
     */
    public final static String TYPE = "languagePack";

    // Private instance variables

    private LanguagePackDefinition packDefinition;
    private boolean classpathAdded;

    public void start(ExtensionDescriptor descriptor, Element element) throws ExtensionException {

        if (element.getName().equals(TYPE)) {

            // Plugin name
            String name = element.getAttributeValue("name");
            if (name == null || name.equals("")) {
                throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
                    "The name attribute must be supplied for <languagePack> elements.");
            }

            // Create the definition
            packDefinition = new LanguagePackDefinition(descriptor, name);
            LanguagePackManager.getInstance().addLanguagePackDefinition(packDefinition);

            for (Iterator i = element.getChildren().iterator(); i.hasNext();) {
                Element el = (Element) i.next();
                if (el.getName().equals("classpath")) {
                    String path =  Util.trimmedBothOrBlank(el.getText());
                    if (path != null && !path.equals("")) {
                        File f = new File(descriptor.getApplicationBundle().getBaseDir(), path);
                        if (f.exists()) {
                            try {
                                URL u = f.toURL();
                                if (log.isInfoEnabled())
                                    log.info("Adding " + u + " to classpath");
                                packDefinition.addClassPath(u);
                            } catch (MalformedURLException murle) {
                            }
                        } else {
                            if (!"true".equals(SystemProperties.get("adito.useDevConfig"))) {
                                log.warn("Plugin classpath element " + f.getAbsolutePath() + " does not exist.");
                            }
                        }
                    }
                } else if (el.getName().equals("date")) {
                    String date = Util.trimmedBothOrBlank(el.getText());
                    if (Util.isNullOrTrimmedBlank(date)) {
                        throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
                            "The content of the <date> must contain the creation date of the language in the pack.");
                    }
                    packDefinition.setDate(date);
                } else if (el.getName().equals("language")) {
                    String code = el.getAttributeValue("code");
                    if (code == null || code.equals("")) {
                        throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
                            "The code attribute must be supplied for <language> elements.");
                    }
                    String description = Util.trimmedBothOrBlank(el.getText());
                    if (Util.isNullOrTrimmedBlank(description)) {
                        throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
                            "The content of the <language> must contain the description of the language in the pack.");
                    }
                    packDefinition.addLanguage(new Language(packDefinition, code, description));
                } else {
                    throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
                        "The <language> element only supports the nested <classpath> elements");
                }
            }
            
            // Now add the classpath. We will only do this once until the dynamic classloader is written
            if(!classpathAdded) {
                for (Iterator j = packDefinition.getClassPath().iterator(); j.hasNext();) {
                    URL u = (URL) j.next();
                    ContextHolder.getContext().addContextLoaderURL(u);
                }
                classpathAdded = true;
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.extensions.ExtensionType#verifyRequiredElements()
     */
    public void verifyRequiredElements() throws ExtensionException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.extensions.ExtensionType#isHidden()
     */
    public boolean isHidden() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.extensions.ExtensionType#getType()
     */
    public String getType() {
        return TYPE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.extensions.ExtensionType#stop()
     */
    public void stop() throws ExtensionException {
        if (packDefinition != null) {
            LanguagePackManager.getInstance().removeLanguagePack(packDefinition);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.extensions.ExtensionType#active()
     */
    public void activate() throws ExtensionException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.extensions.ExtensionType#canStop()
     */
    public boolean canStop() throws ExtensionException {
        return true;
    }

	/* (non-Javadoc)
	 * @see com.adito.extensions.ExtensionType#descriptorCreated(org.jdom.Element)
	 */
	public void descriptorCreated(Element element, SessionInfo session) throws IOException {		
	}

	/* (non-Javadoc)
	 * @see com.adito.extensions.ExtensionType#getTypeBundle()
	 */
	public String getTypeBundle() {
		return "extensions";
	}

    /* (non-Javadoc)
     * @see com.adito.extensions.ExtensionType#getLaunchRequirement()
     */
    public LaunchRequirement getLaunchRequirement() {
        return LaunchRequirement.NOT_LAUNCHABLE;
    }
}