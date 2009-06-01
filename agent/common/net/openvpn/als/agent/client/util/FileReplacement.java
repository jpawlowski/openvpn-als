package net.openvpn.als.agent.client.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

/**
 * This utility will take an input file and replace any number of
 * tokens before outputing it to an output file, which may or may
 * not be temporary.
 *
 * @author Lee David Painter
 */
public class FileReplacement {
	
	public final static String DOS_EOL = "dos";
	public final static String UNIX_EOL = "unix";
	public final static String MAC_EOL = "mac";
	public final static String PLATFORM_EOL = "platform";
    
    // Private instance variables

    private Hashtable tokens = new Hashtable();
    private File templateFile;
    private File outputFile;
    private File cwd;
    private String parameter;
    private String encoding = "UTF8"; //$NON-NLS-1$
    private String convertLineEndings;

    /**
     * Constructor.
     *
     * @param cwd working directory
     */
    public FileReplacement(File cwd) {
        this.cwd = cwd;
    }

    /**
     * Get the ID. This will only be available of {@link #processReplacementXML(XMLElement, AbstractApplicationLauncher)}
     * has been called.
     * 
     * @return id
     */
    public String getId() {
        return parameter;
    }

    void processReplacementXML(XMLElement el, AbstractApplicationLauncher launcher) throws IOException {
        if (!el.getName().equalsIgnoreCase("replacements")) { //$NON-NLS-1$
            throw new IOException(Messages.getString("FileReplacement.elementIsNotReplacements")); //$NON-NLS-1$
        }

        if (el.getAttribute("templateFile") == null) { //$NON-NLS-1$
            throw new IOException(Messages.getString("FileReplacement.elementRequiresTemplateFileAttribute")); //$NON-NLS-1$
        }
        
        convertLineEndings = el.getStringAttribute("convertLineEndings"); 

        templateFile = new File(cwd, el.getAttribute("templateFile").toString()); //$NON-NLS-1$

        if (launcher.events != null)
            launcher.events.debug(MessageFormat.format(Messages.getString("FileReplacement.templateFile"), new Object[] { templateFile.getAbsolutePath() } ) ); //$NON-NLS-1$

        if (el.getAttribute("outputFile") != null) { //$NON-NLS-1$
            outputFile = new File(cwd, el.getAttribute("outputFile").toString()); //$NON-NLS-1$
        } else {
            outputFile = getTempFile(cwd);
        }
        
        FileCleaner.deleteOnExit(outputFile);

        if (launcher.events != null)
            launcher.events.debug(MessageFormat.format(Messages.getString("FileReplacement.outputFile"), new Object[] { outputFile.getAbsolutePath() } ) ); //$NON-NLS-1$

        if (el.getAttribute("parameter") == null) { //$NON-NLS-1$
            throw new IOException(Messages.getString("FileReplacement.replacementsRequiresParameterAttribute")); //$NON-NLS-1$
        }

        parameter = el.getAttribute("parameter").toString(); //$NON-NLS-1$
        launcher.addParameter(parameter, outputFile.getAbsolutePath());

        if (el.getAttribute("encoding") != null) //$NON-NLS-1$
            encoding = el.getAttribute("encoding").toString(); //$NON-NLS-1$

        if (launcher.events != null)
            launcher.events.debug(MessageFormat.format(Messages.getString("FileReplacement.outputFileEncoding"), new Object[] { encoding } ) ); //$NON-NLS-1$

        for (Enumeration e = el.getChildren().elements(); e.hasMoreElements();) {
            XMLElement child = (XMLElement) e.nextElement();

            if (!child.getName().equalsIgnoreCase("replace")) { //$NON-NLS-1$
                throw new IOException(MessageFormat.format(Messages.getString("FileReplacement.notSupported"), new Object[] { child.getName() } ) ) ;//$NON-NLS-1$
            }

            if (child.getAttribute("token") == null || child.getAttribute("value") == null) { //$NON-NLS-1$ //$NON-NLS-2$
                throw new IOException(Messages.getString("FileReplacement.replaceRequiresTokenAndValue")); //$NON-NLS-1$
            }

            tokens.put(child.getAttribute("token").toString(), child.getAttribute("value").toString()); //$NON-NLS-1$ //$NON-NLS-2$
        }

    }

    void createReplacementsFile(AbstractApplicationLauncher launcher) throws IOException {

        FileInputStream in = new FileInputStream(templateFile);
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();

        byte[] buf = new byte[4096];
        int read;

        while ((read = in.read(buf)) > -1) {
            tmp.write(buf, 0, read);
        }

        // First replace all standard parameters etc
        String outputContent = launcher.replaceTokens(new String(tmp.toByteArray(), encoding));

        for (Enumeration e = tokens.keys(); e.hasMoreElements();) {

            String token = (String) e.nextElement();
            String value = (String) tokens.get(token);

            if (launcher.events != null)
                launcher.events.debug(MessageFormat.format(Messages.getString("FileReplacement.processingReplacement"), new Object[] { token, value } ) ); //$NON-NLS-1$ //$NON-NLS-2$

            // Perform replacement in String
            outputContent = AbstractApplicationLauncher.replaceAllTokens(outputContent, token, value);

        }
        
        if(convertLineEndings != null && !convertLineEndings.equals(PLATFORM_EOL)) {
	        BufferedReader br = new BufferedReader(new StringReader(outputContent));
	        String line = null;
	        PrintWriter pw = new PrintWriter(new FileOutputStream(outputFile));
	        try {
	        	while( ( line = br.readLine() ) != null) {
	        		if(DOS_EOL.equals(convertLineEndings)) {
	        			pw.print(line + "\r\n");
	        		}
	        		else if(UNIX_EOL.equals(convertLineEndings)) {
	        			pw.print(line + "\n");
	        		}
	        		else if(MAC_EOL.equals(convertLineEndings)) {
	        			pw.print(line + "\r");
	        		}
	        		else {
	        			pw.println(line);
	        		}
	        	}
	        } finally {
	            pw.close();
	        }
        }
        else {
            FileOutputStream out = new FileOutputStream(outputFile);
            try {
                out.write(outputContent.getBytes(encoding));
            } finally {
                out.close();
            }
		}


    }

    static File getTempFile(File near) throws IOException {
        String path = null;
        if (near != null)
            if (near.isFile())
                path = near.getParent();
            else if (near.isDirectory())
                path = near.getPath();

        Random wheel = new Random(); // seeded from the clock
        File tempFile = null;
        do {
            // generate random a number 10,000,000 .. 99,999,999
            int unique = (wheel.nextInt() & Integer.MAX_VALUE) % 90000000 + 10000000;
            tempFile = new File(path, Integer.toString(unique) + ".tmp"); //$NON-NLS-1$
        } while (tempFile.exists());
        // We "finally" found a name not already used. Nearly always the first
        // time.
        // Quickly stake our claim to it by opening/closing it to create it.
        // In theory somebody could have grabbed it in that tiny window since
        // we checked if it exists, but that is highly unlikely.
        new FileOutputStream(tempFile).close();

        // debugging peek at the name generated.
        if (false) {
            System.out.println(tempFile.getCanonicalPath());
        }
        return tempFile;
    } // end getTempFile
}
