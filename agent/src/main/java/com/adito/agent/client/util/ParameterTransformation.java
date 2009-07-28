package com.adito.agent.client.util;

import java.io.IOException;
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

/**
 * Processes &lt;transform&gt; elements in application extension descriptors and
 * dynamically loads a transform class to process the input and output provided.
 */
public class ParameterTransformation {

    private XMLElement el;
    private AbstractApplicationLauncher launcher;
    private String outputParam;
    private String inputParam;

    /**
     * Constructor. The processing won't take place until {@link #processTransformation()}
     * is invoked.
     * 
     * @param el
     * @param launcher
     * @throws IOException
     */
    ParameterTransformation(XMLElement el, AbstractApplicationLauncher launcher) throws IOException {

        if (el.getAttribute("class") == null || el.getAttribute("input") == null || el.getAttribute("output") == null) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            throw new IOException(Messages.getString("ParameterTransformation.transformRequiresClassInputAndOutputAttributes")); //$NON-NLS-1$

        this.el = el;
        this.launcher = launcher;

    }

    /**
     * Process the transformation.
     * 
     * @throws IOException on any error
     */
    public void processTransformation() throws IOException {

        String classFile = el.getAttribute("class").toString(); //$NON-NLS-1$
        inputParam = el.getAttribute("input").toString(); //$NON-NLS-1$
        outputParam = el.getAttribute("output").toString(); //$NON-NLS-1$

        File f = new File(launcher.getInstallDir(), classFile + ".class"); //$NON-NLS-1$

        if (!f.exists())
            throw new IOException(classFile + Messages.getString("ParameterTransformation.8")); //$NON-NLS-1$

        FileInputStream in = new FileInputStream(f);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] buf = new byte[16384];
        int read;

        while ((read = in.read(buf)) > -1) {
            out.write(buf, 0, read);
        }

        in.close();
        buf = out.toByteArray();
        try {

            launcher.events.debug(Messages.getString("ParameterTransformation.loadingTransformationClass") + classFile); //$NON-NLS-1$
            Class cls = ByteArrayClassLoader.getInstance().createFromByteArray(classFile, buf, 0, buf.length);

            launcher.events.debug(Messages.getString("ParameterTransformation.creatingTransformationInstance")); //$NON-NLS-1$
            Transformation t = (Transformation) cls.newInstance();

            launcher.events.debug(Messages.getString("ParameterTransformation.invokingTransformation")); //$NON-NLS-1$
            launcher.addParameter(outputParam, t.transform(launcher.replaceTokens(inputParam)));
        } catch (Exception ex) {
            launcher.events.debug(Messages.getString("ParameterTransformation.exceptionInTransformation") + ex.getMessage()); //$NON-NLS-1$
        }

    }
}
