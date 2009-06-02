/*
 * ====================================================================
 * Copyright (c) 1995-1999 Purple Technology, Inc. All rights
 * reserved.
 *
 * PLAIN LANGUAGE LICENSE: Do whatever you like with this code, free
 * of charge, just give credit where credit is due. If you improve it,
 * please send your improvements to alex@purpletech.com. Check
 * http://www.purpletech.com/code/ for the latest version and news.
 *
 * LEGAL LANGUAGE LICENSE: Redistribution and use in source and binary
 * forms, with or without modification, are permitted provided that
 * the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *
 * 3. The names of the authors and the names "Purple Technology,"
 * "Purple Server" and "Purple Chat" must not be used to endorse or
 * promote products derived from this software without prior written
 * permission. For written permission, please contact
 * server@purpletech.com.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHORS AND PURPLE TECHNOLOGY ``AS
 * IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE
 * AUTHORS OR PURPLE TECHNOLOGY BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 *
 **/

package net.openvpn.als.util;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Utils {

    /**
     * fills the left side of a number with zeros <br>
     * e.g. zerofill(14, 3) -> "014" <br>
     * e.g. zerofill(187, 6) -> "000014" <br>
     * e.g. zerofill(-33, 4) -> "-033" <br>
     **/
    public static String zerofill(int x, int desiredWidth) {
        StringBuffer buf = new StringBuffer();
        if (x < 0) {
            buf.append("-");
            desiredWidth--;
            x = -x;
        }
        while (desiredWidth>7) {
            buf.append("0");
            desiredWidth--;
        }
        switch (desiredWidth) {
        case 7:
            if (x<1000000) buf.append("0");
        case 6:
            if (x<100000) buf.append("0");
        case 5:
            if (x<10000) buf.append("0");
        case 4:
            if (x<1000) buf.append("0");
        case 3:
            if (x<100) buf.append("0");
        case 2:
            if (x<10) buf.append( "0" );
        }
        buf.append(x);
        return buf.toString();
    }

    public static void printIndent(PrintWriter out, int indent) {
        out.print(indent(indent));
    }

    public static String indent(int indent) {
        switch (indent) {
        case 8:
            return("        ");
        case 7:
            return("       ");
        case 6:
            return("      ");
        case 5:
            return("     ");
        case 4:
            return("    ");
        case 3:
            return("   ");
        case 2:
            return("  ");
        case 1:
            return(" ");
        default:
            StringBuffer buf = new StringBuffer();
            for (int i=0; i<indent; ++i) { buf.append(" "); }
            return buf.toString();
        }
    }

    /**
     * @deprecated use org.apache.commons.lang.StringUtils.join()
     **/
    public static String commaList(Object[] a) {
        return commaList(Arrays.asList(a).iterator());
    }

    /**
     * @deprecated use org.apache.commons.lang.StringUtils.join()
     **/
    public static String commaList(Collection c) {
        return commaList(c.iterator());
    }

    /**
     * @deprecated use org.apache.commons.lang.StringUtils.join()
     **/
    public static String commaList(Iterator i) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        printCommaList(pw, i);
        pw.close();
        return sw.toString();
    }


    /**
     * Given an iterator, prints it as a comma-delimited list
     * (actually a comma-and-space delimited list).  E.g. If the
     * iterator contains the strings { "my", "dog", "has fleas" } it
     * will print "my, dog, has fleas".
     *
     * @param out the stream to write to
     * @param i an iterator containing printable (toString) objects, e.g. strings
     **/
    public static void printCommaList(PrintWriter out, Iterator i) {
        boolean first = true;
        while (i.hasNext()) {
            if (first) first = false;
            else out.print(", ");
            out.print(i.next());
        }
    }

    /**
     * @return true if all characters in the string are whitespace characters, or the string is empty
     **/
    public static boolean isWhitespace(String s) {
        for (int i=0; i<s.length(); ++i) {
            if (!Character.isWhitespace(s.charAt(i))) return false;
        }
        return true;
    }

    /**
     * Class encapsulating information from an exec call -- slightly
     * easier than the standard API
     **/
    public static class ExecInfo {
        public int exit;
        public String stdout;
        public String stderr;

        public String toString() {
            return "ExecInfo[exit=" + exit + "," +
                "stdout=" + javaEscape(stdout) + "," +
                "stderr=" + javaEscape(stderr) + "]";
        }
    }

    /**
     * Wrapper for Runtime.exec. Takes input as a String. Times out
     * after sleep msec. Returns an object containing exit value,
     * standard output, and error output.
     * @param command the command-line to execute
     * @param input a string to pass to the process as standard input
     * @param sleep msec to wait before terminating process (if <= 0, waits forever)
     **/
    /*public static ExecInfo exec(String command, String input, long sleep) throws IOException {
        Process process = null;
        ExecInfo info = new ExecInfo();
        try {
            Alarm a = null;
            if (sleep>0) {
                a = new Alarm(Thread.currentThread(), sleep);
                a.start();
            }

            process = Runtime.getRuntime().exec(command);

            if (input != null) {
                PrintWriter pw = new PrintWriter(process.getOutputStream());
                pw.print(input);
                pw.close();
            }

            info.stdout = IOUtils.readStream(process.getInputStream());

            info.stderr = IOUtils.readStream(process.getErrorStream());

            process.waitFor();
            if (a!=null)    a.stop = true;
        }
        catch (InterruptedIOException iioe) {
            throw new IOException("Process '" + command + "' took more than " + sleep/1000 + " sec");
        }
        catch (InterruptedException ie) {
            throw new IOException("Process '" + command + "' took more than " + sleep/1000 + " sec");
        }

        finally {
            if (process != null)
                process.destroy();
        }

        info.exit = process.exitValue();
        return info;
    }*/

    /**
     * Turn "Now is the time for all good men" into "Now is the time for..."
     * <p>
     * Specifically:
     * <p>
     * If str is less than max characters long, return it.
     * Else abbreviate it to (substring(str, 0, max-3) + "...").
     * If max is less than 3, throw an IllegalArgumentException.
     * In no case will it return a string of length greater than max.
     *
     * @param max maximum length of result string
     **/
    public static String abbreviate(String s, int max) {
        if (max < 4)
            throw new IllegalArgumentException("Minimum abbreviation is 3 chars");
        if (s.length() <= max) return s;
        // todo: break into words
        return s.substring(0, max-3) + "...";
    }

    /**
     * pad or truncate
     **/
    public static String pad(String s, int length) {
        if (s.length() < length) return s + indent(length - s.length());
        else return s.substring(0,length);
    }

    /**
     * Compare two strings, and return the portion where they differ.
     * (More precisely, return the remainder of the second string,
     * starting from where it's different from the first.)
     * <p>
     * E.g. strdiff("i am a machine", "i am a robot") -> "robot"
     *
     **/
    public static String strdiff(String s1, String s2) {
        int at = strdiffat(s1, s2);
        if (at == -1)
            return "";
        return s2.substring(at);
    }

    /**
     * Compare two strings, and return the index at which the strings begin to diverge<p>
     * E.g. strdiff("i am a machine", "i am a robot") -> 7<p>
     * @return -1 if they are the same
     *
     **/
    public static int strdiffat(String s1, String s2)
    {
        int i;
        for (i=0; i<s1.length() && i<s2.length(); ++i)
        {
            if (s1.charAt(i) != s2.charAt(i)) {
                break;
            }
        }
        if (i<s2.length() || i<s1.length())
            return i;
        return -1;
    }

    /**
     * Compare two strings, and return a verbose description of how
     * they differ. Shows a window around the location to provide
     * context.  E.g. strdiffVerbose("i am a robot", "i am a machine")
     * might return a string containing <pre>
     * strings differ at character 7
     * Expected: ...am a robot
     *   Actual: ...am a machine</pre>
     *
     * This was developed in order to provide some sanity to JUnit's
     * assertEquals routine.
     **/

    public static String strdiffVerbose(String expected, String actual)
    {
        int at = Utils.strdiffat(actual, expected);
        if (at == -1)
            return null;
        int length = 60;        // todo: parameterize
        int back = 20;          // todo: parameterize
        int start = at - back;
        if (start < 3) start = 0;

        StringBuffer buf = new StringBuffer(length*2 + 100);
        buf.append("strings differ at character ").append(at);
        buf.append("\n");

        buf.append("Expected: ");
        appendWithEllipses(buf, expected, start, length);
        buf.append("\n");

        buf.append("  Actual: ");
        appendWithEllipses(buf, actual, start, length);
        buf.append("\n");

        return buf.toString();
    }

    private static void appendWithEllipses(StringBuffer buf, String s, int start, int length)
    {
        if (start > 0) buf.append("...");
        buf.append
            (javaEscape   // note that escapes may add \, making final string more than 60 chars
             (abbreviate  // abbreviate adds the final ... if necessary
              (s.substring(start), length)));
    }

    /**
     * count the number of occurences of ch inside s
     * @deprecated use org.apache.commons.lang.StringUtils.countMatches instead
     **/
    public static int count(String s, char ch) {
        int c=0;
        for (int i=0; i<s.length(); ++i) {
            if (s.charAt(i) == ch) c++;
        }
        return c;
    }

    /**
     * Replace all occurences of target inside source with replacement.
     * E.g. replace("fee fie fo fum", "f", "gr") -> "gree grie gro grum"
     **/
    public static String replace(String source, String target, String replacement)
    {
        // could use a regular expression, but this keeps it portable
        StringBuffer result = new StringBuffer(source.length());
        int i = 0, j = 0;
        int len = source.length();
        while (i < len) {
            j = source.indexOf(target, i);
            if (j == -1) {
                result.append( source.substring(i,len) );
                break;
            }
            else {
                result.append( source.substring(i,j) );
                result.append( replacement );
                i = j + target.length();
            }
        }
        return result.toString();
    }

    /**
     * <p>
     *  Trim the whitespace off the right side of a <code>String</code>.
     * </p>
     *
     * @param orig <code>String</code> to rtrim.
     * @return <code>String</code> - orig with no right spaces
     */
    public static String rtrim(String orig) {
        int len = orig.length();
        int st = 0;
        int off = 0;
        char[] val = orig.toCharArray();

        while ((st < len) && (val[off + len - 1] <= ' ')) {
            len--;
        }
        return ((st > 0) || (len < orig.length())) ? orig.substring(st, len) : orig;
    }

    /**
     * <p>
     *  Trim the left spacing off of a <code>String</code>.
     * </p>
     *
     * @param orig <code>String</code> to rtrim.
     * @return <code>String</code> - orig with no left spaces
     */
    public static String ltrim(String orig) {
        int len = orig.length();
        int st = 0;
        int off = 0;
        char[] val = orig.toCharArray();

        while ((st < len) && (val[off + st] <= ' ')) {
            st++;
        }
        return ((st > 0) || (len < orig.length())) ? orig.substring(st, len) : orig;
    }

    /**
     * calculate the maximum length of all strings in i.  If i
     * contains other than strings, uses toString() value.
     **/
    public static int getMaxLength(Iterator i) {
        int max = 0;
        while (i.hasNext()) {
            String s = i.next().toString();
            int c = s.length();
            if (c>max) max=c;
        }
        return max;
    }

    // see http://hotwired.lycos.com/webmonkey/reference/special_characters/
    static Object[][] entities = {
       // {"#39", new Integer(39)},       // ' - apostrophe
        {"quot", new Integer(34)},      // " - double-quote
        {"amp", new Integer(38)},       // & - ampersand
        {"lt", new Integer(60)},        // < - less-than
        {"gt", new Integer(62)},        // > - greater-than
        {"nbsp", new Integer(160)},     // non-breaking space
        {"copy", new Integer(169)},     // � - copyright
        {"reg", new Integer(174)},      // � - registered trademark
        {"Agrave", new Integer(192)},   // � - uppercase A, grave accent
        {"Aacute", new Integer(193)},   // � - uppercase A, acute accent
        {"Acirc", new Integer(194)},    // � - uppercase A, circumflex accent
        {"Atilde", new Integer(195)},   // � - uppercase A, tilde
        {"Auml", new Integer(196)},     // � - uppercase A, umlaut
        {"Aring", new Integer(197)},    // � - uppercase A, ring
        {"AElig", new Integer(198)},    // � - uppercase AE
        {"Ccedil", new Integer(199)},   // � - uppercase C, cedilla
        {"Egrave", new Integer(200)},   // � - uppercase E, grave accent
        {"Eacute", new Integer(201)},   // � - uppercase E, acute accent
        {"Ecirc", new Integer(202)},    // � - uppercase E, circumflex accent
        {"Euml", new Integer(203)},     // � - uppercase E, umlaut
        {"Igrave", new Integer(204)},   // � - uppercase I, grave accent
        {"Iacute", new Integer(205)},   // � - uppercase I, acute accent
        {"Icirc", new Integer(206)},    // � - uppercase I, circumflex accent
        {"Iuml", new Integer(207)},     // � - uppercase I, umlaut
        {"ETH", new Integer(208)},      // � - uppercase Eth, Icelandic
        {"Ntilde", new Integer(209)},   // � - uppercase N, tilde
        {"Ograve", new Integer(210)},   // � - uppercase O, grave accent
        {"Oacute", new Integer(211)},   // � - uppercase O, acute accent
        {"Ocirc", new Integer(212)},    // � - uppercase O, circumflex accent
        {"Otilde", new Integer(213)},   // � - uppercase O, tilde
        {"Ouml", new Integer(214)},     // � - uppercase O, umlaut
        {"Oslash", new Integer(216)},   // � - uppercase O, slash
        {"Ugrave", new Integer(217)},   // � - uppercase U, grave accent
        {"Uacute", new Integer(218)},   // � - uppercase U, acute accent
        {"Ucirc", new Integer(219)},    // � - uppercase U, circumflex accent
        {"Uuml", new Integer(220)},     // � - uppercase U, umlaut
        {"Yacute", new Integer(221)},   // � - uppercase Y, acute accent
        {"THORN", new Integer(222)},    // � - uppercase THORN, Icelandic
        {"szlig", new Integer(223)},    // � - lowercase sharps, German
        {"agrave", new Integer(224)},   // � - lowercase a, grave accent
        {"aacute", new Integer(225)},   // � - lowercase a, acute accent
        {"acirc", new Integer(226)},    // � - lowercase a, circumflex accent
        {"atilde", new Integer(227)},   // � - lowercase a, tilde
        {"auml", new Integer(228)},     // � - lowercase a, umlaut
        {"aring", new Integer(229)},    // � - lowercase a, ring
        {"aelig", new Integer(230)},    // � - lowercase ae
        {"ccedil", new Integer(231)},   // � - lowercase c, cedilla
        {"egrave", new Integer(232)},   // � - lowercase e, grave accent
        {"eacute", new Integer(233)},   // � - lowercase e, acute accent
        {"ecirc", new Integer(234)},    // � - lowercase e, circumflex accent
        {"euml", new Integer(235)},     // � - lowercase e, umlaut
        {"igrave", new Integer(236)},   // � - lowercase i, grave accent
        {"iacute", new Integer(237)},   // � - lowercase i, acute accent
        {"icirc", new Integer(238)},    // � - lowercase i, circumflex accent
        {"iuml", new Integer(239)},     // � - lowercase i, umlaut
        {"igrave", new Integer(236)},   // � - lowercase i, grave accent
        {"iacute", new Integer(237)},   // � - lowercase i, acute accent
        {"icirc", new Integer(238)},    // � - lowercase i, circumflex accent
        {"iuml", new Integer(239)},     // � - lowercase i, umlaut
        {"eth", new Integer(240)},      // � - lowercase eth, Icelandic
        {"ntilde", new Integer(241)},   // � - lowercase n, tilde
        {"ograve", new Integer(242)},   // � - lowercase o, grave accent
        {"oacute", new Integer(243)},   // � - lowercase o, acute accent
        {"ocirc", new Integer(244)},    // � - lowercase o, circumflex accent
        {"otilde", new Integer(245)},   // � - lowercase o, tilde
        {"ouml", new Integer(246)},     // � - lowercase o, umlaut
        {"oslash", new Integer(248)},   // � - lowercase o, slash
        {"ugrave", new Integer(249)},   // � - lowercase u, grave accent
        {"uacute", new Integer(250)},   // � - lowercase u, acute accent
        {"ucirc", new Integer(251)},    // � - lowercase u, circumflex accent
        {"uuml", new Integer(252)},     // � - lowercase u, umlaut
        {"yacute", new Integer(253)},   // � - lowercase y, acute accent
        {"thorn", new Integer(254)},    // � - lowercase thorn, Icelandic
        {"yuml", new Integer(255)},     // � - lowercase y, umlaut
        {"euro", new Integer(8364)},    // Euro symbol
    };
    static Map e2i = new HashMap();
    static Map i2e = new HashMap();
    static {
        for (int i=0; i<entities.length; ++i) {
            e2i.put(entities[i][0], entities[i][1]);
            i2e.put(entities[i][1], entities[i][0]);
        }
    }

    /**
     * Turns funky characters into HTML entity equivalents<p>
     * e.g. <tt>"bread" & "butter"</tt> => <tt>&amp;quot;bread&amp;quot; &amp;amp; &amp;quot;butter&amp;quot;</tt>.
     * Update: supports nearly all HTML entities, including funky accents. See the source code for more detail.
     * @see #htmlunescape(String)
     **/
    public static String htmlescape(String s1)
    {
        StringBuffer buf = new StringBuffer();
        int i;
        for (i=0; i<s1.length(); ++i) {
            char ch = s1.charAt(i);
            String entity = (String)i2e.get( new Integer((int)ch) );
            if (entity == null) {
                if (((int)ch) > 128) {
                    buf.append("&#" + ((int)ch) + ";");
                }
                else {
                    buf.append(ch);
                }
            }
            else {
                buf.append("&" + entity + ";");
            }
        }
        return buf.toString();
    }

    /**
     * Given a string containing entity escapes, returns a string
     * containing the actual Unicode characters corresponding to the
     * escapes.
     *
     * Note: nasty bug fixed by Helge Tesgaard (and, in parallel, by
     * Alex, but Helge deserves major props for emailing me the fix).
     * 15-Feb-2002 Another bug fixed by Sean Brown <sean@boohai.com>
     *
     * @see #htmlescape(String)
     **/
    public static String htmlunescape(String s1) {
        StringBuffer buf = new StringBuffer();
        int i;
        for (i=0; i<s1.length(); ++i) {
            char ch = s1.charAt(i);
            if (ch == '&') {
                int semi = s1.indexOf(';', i+1);
                if (semi == -1) {
                    buf.append(ch);
                    continue;
                }
                String entity = s1.substring(i+1, semi);
                Integer iso;
                if (entity.charAt(0) == '#') {
                    iso = new Integer(entity.substring(1));
                }
                else {
                    iso = (Integer)e2i.get(entity);
                }
                if (iso == null) {
                    buf.append("&" + entity + ";");
                }
                else {
                    buf.append((char)(iso.intValue()));
                }
                i = semi;
            }
            else {
                buf.append(ch);
            }
        }
        return buf.toString();
    }

    /**
     * Prepares a string for output inside a JavaScript string,
     * e.g. for use inside a document.write("") command.
     *
     * Example:
     * <pre>
     * input string: He didn't say, "Stop!"
     * output string: He didn\'t say, \"Stop!\"
     * </pre>
     *
     * Deals with quotes and control-chars (tab, backslash, cr, ff, etc.)
     * Bug: does not yet properly escape Unicode / high-bit characters.
     *
     * @see #jsEscape(String, Writer)
     **/
    public static String jsEscape(String source) {
        try {
            StringWriter sw = new StringWriter();
            jsEscape(source, sw);
            sw.flush();
            return sw.toString();
        }
        catch (IOException ioe) {
            // should never happen writing to a StringWriter
            ioe.printStackTrace();
            return null;
        }
    }

    /**
     * @see #javaEscape(String, Writer)
     **/
    public static String javaEscape(String source) {
        try {
            StringWriter sw = new StringWriter();
            javaEscape(source, sw);
            sw.flush();
            return sw.toString();
        }
        catch (IOException ioe) {
            // should never happen writing to a StringWriter
            ioe.printStackTrace();
            return null;
        }
    }
    
    public static String readLine(InputStream in) throws IOException, EOFException {
    	
    	    StringBuffer buf = new StringBuffer();
    	    
    	    int ch;
    	    
    	    while((ch = in.read()) > -1) {
    	    	  
    	    	   if(ch=='\n')
    	    		   break;
    	       buf.append((char)ch);	
    	    }
    	    
    	    if(ch != '\n' && buf.length()==0)
    	    	   return null;
    	    
    	    
    	    return buf.toString();
    }


    /**
     * Prepares a string for output inside a JavaScript string,
     * e.g. for use inside a document.write("") command.
     *
     * Example:
     * <pre>
     * input string: He didn't say, "stop!"
     * output string: He didn\'t say, \"stop!\"
     * </pre>
     *
     * Deals with quotes and control-chars (tab, backslash, cr, ff, etc.)
     *
     * @see #jsEscape(String)
     **/
    public static void jsEscape(String source, Writer out) throws IOException {
        stringEscape(source, out, true);
    }

    /**
     * Prepares a string for output inside a Java string,
     *
     * Example:
     * <pre>
     * input string: He didn't say, "stop!"
     * output string: He didn't say, \"stop!\"
     * </pre>
     *
     * Deals with quotes and control-chars (tab, backslash, cr, ff, etc.)
     *
     * @see #jsEscape(String,Writer)
     **/
    public static void javaEscape(String source, Writer out) throws IOException {
        stringEscape(source, out, false);
    }

    private static void stringEscape(String source, Writer out, boolean escapeSingleQuote) throws IOException {
        char[] chars = source.toCharArray();
        for (int i=0; i<chars.length; ++i) {
            char ch = chars[i];
            switch (ch) {
            case '\b':  // backspace (ASCII 8)
                out.write("\\b");
                break;
            case '\t':  // horizontal tab (ASCII 9)
                out.write("\\t");
                break;
            case '\n':  // newline (ASCII 10)
                out.write("\\n");
                break;
            case 11:    // vertical tab (ASCII 11)
                out.write("\\v");
                break;
            case '\f':  // form feed (ASCII 12)
                out.write("\\f");
                break;
            case '\r':  // carriage return (ASCII 13)
                out.write("\\r");
                break;
            case '"':   // double-quote (ASCII 34)
                out.write("\\\"");
                break;
            case '\'':  // single-quote (ASCII 39)
                if (escapeSingleQuote) out.write("\\'");
                else out.write("'");
                break;
            case '\\':  // literal backslash (ASCII 92)
                out.write("\\\\");
                break;
            default:
                    if ((int) ch < 32 || (int) ch > 127)
                    {
                        out.write("\\u");
                        zeropad(out, Integer.toHexString(ch).toUpperCase(), 4);
                    }
                    else
                    {
                        out.write(ch);
                    }
                    break;
            }
        }
    }

    private static void zeropad(Writer out, String s, int width) throws IOException
    {
        while (width > s.length()) {
            out.write('0');
            width--;
        }
        out.write(s);
    }


    /**
     *  Filter out Windows and Mac curly quotes, replacing them with
     *  the non-curly versions. Note that this doesn't actually do any
     *  checking to verify the input codepage. Instead it just
     *  converts the more common code points used on the two platforms
     *  to their equivalent ASCII values. As such, this method
     *  <B>should not be used</b> on ISO-8859-1 input that includes
     *  high-bit-set characters, and some text which uses other
     *  codepoints may be rendered incorrectly.
     *
     * @author Ian McFarland
     **/
    public static String uncurlQuotes(String input)
    {
        if (input==null)
            return "";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < input.length(); i++)
        {
            char ch = input.charAt(i);
            int code = (int) ch;
            if (code == 210 || code == 211 || code == 147 || code == 148)
            {
                ch = (char) 34; // double quote
            }
            else if (code == 212 || code == 213 || code == 145 || code == 146)
            {
                ch = (char) 39; // single quote
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    /**
     * capitalize the first character of s
     **/
    public static String capitalize(String s) {
        return s.substring(0,1).toUpperCase() + s.substring(1);
    }

    /**
     * lowercase the first character of s
     **/
    public static String lowerize(String s) {
        return s.substring(0,1).toLowerCase() + s.substring(1);
    }

    /**
     * turn String s into a plural noun (doing the right thing with
     * "story" -> "stories" and "mess" -> "messes")
     **/
    public static String pluralize(String s) {
        if (s.endsWith("y"))
            return s.substring(0, s.length()-1) + "ies";

        else if (s.endsWith("s"))
            return s + "es";

        else
            return s + "s";
    }

    public static boolean ok(String s) {
        return (!(s == null || s.equals("")));
    }

    /**
     * Converts camelCaseVersusC to camel_case_versus_c
     **/
    public static String toUnderscore(String s) {
        StringBuffer buf = new StringBuffer();
        char[] ch = s.toCharArray();
        for (int i=0; i<ch.length; ++i) {
            if (Character.isUpperCase(ch[i])) {
                buf.append('_');
                buf.append(Character.toLowerCase(ch[i]));
            }
            else {
                buf.append(ch[i]);
            }
        }
        //System.err.println(s + " -> " + buf.toString());
        return buf.toString();
    }

    /**
     * @deprecated use org.apache.commons.lang.StringUtils deleteSpaces instead
     **/
    public static String stripWhitespace(String s) {
        StringBuffer buf = new StringBuffer();
        char[] ch = s.toCharArray();
        for (int i=0; i<ch.length; ++i) {
            if (Character.isWhitespace(ch[i])) {
                continue;
            }
            else {
                buf.append(ch[i]);
            }
        }
        return buf.toString();
    }

    public static String getStackTrace(Throwable t) {
        StringWriter s = new StringWriter();
        PrintWriter p = new PrintWriter(s);
        t.printStackTrace(p);
        p.close();
        return s.toString();
    }

    public static void sleep(long msec) {
        try {
            Thread.sleep(msec);
        }
        catch (InterruptedException ie) {}
    }
    
	public static boolean isSupportedPlatform(String os) {
		if (os != null && !os.equals("")) {
			// If the os does not start with the current platform then ignore
			// this
			String platform = System.getProperty("os.name").toUpperCase(); //$NON-NLS-1$
			if(os.startsWith("!")) { //$NON-NLS-1$
				return !platform.startsWith(os.substring(1).toUpperCase());
			} else
				return platform.startsWith(os.toUpperCase());
		} else
			return true;
	}

	public static boolean isSupportedArch(String arch) {
		if (arch != null && !arch.equals("")) {
			String platformArch = System.getProperty("os.arch").toUpperCase(); //$NON-NLS-1$
			if(arch.startsWith("!")) { //$NON-NLS-1$
				if(isWindows64JREAvailable())
					return !arch.substring(1).toUpperCase().equals("AMD64");
				else 
					return !platformArch.startsWith(arch.substring(1).toUpperCase());
			} else {
				if(isWindows64JREAvailable())
					return arch.toUpperCase().equals("AMD64");
				else 
					return platformArch.startsWith(arch.toUpperCase());
			}
		} else
			return true;
	}
	
	public static boolean isWindows64JREAvailable() {
		
		try {
			String javaHome = new File(System.getProperty("java.home")).getCanonicalPath();
			
			try {
				if(System.getProperty("os.name").startsWith("Windows")) {
					int dataModel = Integer.parseInt(System.getProperty("sun.arch.data.model"));
	
					if(dataModel!=64) {
				    	int idx = javaHome.indexOf(" (x86)");
				    	if(idx > -1) {
				    		// Looks like we have a 32bit Java version installed on 64 bit Windows
				    		String programFiles = javaHome.substring(0, idx);
				    		File j = new File(programFiles, "Java");
				    		if(j.exists()) {
				    			// We may have a 64 bit version of Java installed.
				    			String[] jres = j.list();
				    			for(int i=0;i<jres.length;i++) {
	
				    				File h = new File(j, jres[i]);
				    				File exe = new File(h, "bin\\java.exe");
				    				if(exe.exists()) {
				    					// Found a 64bit version of java
				    					return true;
				    				}
				    			}
				    		}
				    	}
				    }
				}
			} catch(NumberFormatException ex) {
			}
			
			return false;
		} catch(IOException ex) {
			return false;
		}
	}
} // class Utils

