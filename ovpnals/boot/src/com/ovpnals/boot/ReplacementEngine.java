
				/*
 *  OpenVPN-ALS
 *
 *  Copyright (C) 2003-2006 3SP LTD. All Rights Reserved
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
			
package com.ovpnals.boot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * A helper that provides facilities for a list of regular expressions to be
 * built up and the performed on some content in one go, with the replacement
 * value being provided by a callback method.
 * <p>
 * This class represents the notion of a replacement engine. The user of this
 * object creates an instance of a replacement engine then adds all of the
 * patterns that they wish to be search for (using
 * {@link #addPattern(String, Replacer, String)}.
 * <p>
 * This method also expects a {@link com.ovpnals.boot.Replacer}
 * implementation to be provided. When the engine is instructed to process some
 * content, every time a match is found the appropriate
 * {@link com.ovpnals.boot.Replacer#getReplacement(Pattern, Matcher, String)}
 * method will be called. The value that is returned from this method will then
 * replace the matched string.
 * <p>
 * The two primary uses for this class can be found in the <i>Replacement proxy</i>
 * feature and the <i>Appplication Extension</i>. Replacement proxy uses it to
 * replace hyperlinks with HTML content with proxied links and the Application
 * Extension uses it to dynamically replace provided application arguments with
 * pre-defined strings.
 * <p>
 * As processing regular expressions can be a fairly intensive task , a cached
 * pool of compile regular expression is also maintained as they may be re-used.
 */

public class ReplacementEngine {
    
    final static Log log = LogFactory.getLog(ReplacementEngine.class);

    // Private instance variables
    private final StringBuffer inputBuffer = new StringBuffer();
    private final StringBuffer workBuffer = new StringBuffer();
    private List replacementsList = new ArrayList();
    private boolean caseSensitive = true;
    private boolean dotAll = false;
    private static PatternPool patternPool;
    private String charset;
    private Encoder encoder;

    /**
     * Constructor
     */
    public ReplacementEngine() {
        patternPool = new PatternPool();
    }
    
    /**
     * Set the {@link Encoder} implementation to use. This is a callback
     * interface that has the oppurtunity to do post processing on any
     * replaced values.
     * 
     *  @param encoder
     */
    public void setEncoder(Encoder encoder) {
        this.encoder = encoder;
    }

    /**
     * Set the character encoding of the content to replace
     * 
     * @param charset character encoding
     */
    public void setEncoding(String charset) {
        this.charset = charset;
    }

    /**
     * Get the instance of the pattern pool.
     * 
     * @return instance of pattern pool
     */
    public static PatternPool getPatternPool() {
        if (patternPool == null) {
            patternPool = new PatternPool();
        }
        return patternPool;
    }

    /**
     * Set whether every match on every record should be processed
     * 
     * @param dotAll process every match on every record
     */
    public void setDotAll(boolean dotAll) {
        this.dotAll = dotAll;
    }

    /**
     * Set whether the matching is case sensitive
     * 
     * @param caseSensitive is case sensitve
     */
    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    /**
     * Add a new pattern to the replacement engine.
     * 
     * @param pattern patter to search for
     * @param replacer replace containing callback to get replacement value
     * @param replacementPattern optional replacement pattern (replacer
     *        implementation may require it)
     */
    public synchronized void addPattern(String pattern, Replacer replacer, String replacementPattern) {
        // Pattern p = Pattern.compile(pattern, ( caseSensitive ? 0 :
        // Pattern.CASE_INSENSITIVE ) + ( dotAll ? Pattern.DOTALL : 0 ) );
        replacementsList.add(new ReplaceOp(replacer, pattern, replacementPattern));
    }

    /**
     * Replace all occurences of all registered pattern in the provided string
     * and return the result as a second string
     * 
     * @param input input string
     * @return processed string
     */
    public synchronized String replace(String input) {
        Iterator it = replacementsList.iterator();

        inputBuffer.setLength(0);
        inputBuffer.append(input);

        workBuffer.setLength(0);
        workBuffer.ensureCapacity(input.length());
        
        if (log.isDebugEnabled())
        	log.debug("Starting replacement on string on " + input.length() + " characters");
        
        while (it.hasNext()) {
            ReplaceOp op = (ReplaceOp) it.next();
            
            if (log.isDebugEnabled())
            	log.debug("Replacemnt " + op.replacePattern + " [" + op.pattern + "]");
            
            Pattern p = getPatternPool().getPattern(op.pattern, caseSensitive, dotAll);
            
            if (log.isDebugEnabled())
            	log.debug("Got pattern from pool");
            
            try {
                replaceInto(p, op.replacePattern, op.replacer, inputBuffer, workBuffer);
                if (log.isDebugEnabled())
                	log.debug("Replacement complete");
            } catch (Throwable t) {                
            	if (log.isDebugEnabled())
            		log.debug("Error replacing.", t);
            } finally {
            	if (log.isDebugEnabled())
            		log.debug("Releasing pattern from pool.");
                patternPool.releasePattern(p);
            }
            inputBuffer.setLength(0);
            inputBuffer.append(workBuffer);
        }
        if (log.isDebugEnabled())
        	log.debug("Finished replacing. Returning string of " +inputBuffer.length() + "characters");
        return (inputBuffer.toString());
    }

    /**
     * Replace all occurences of all registered patterns in all data read from
     * the input stream and write the processed content back to the provided
     * output stream.
     * <p>
     * <b>Note, this method current reads the entire stream into memory as a
     * string before performing the replacements. Beware of this when are using
     * this method. A more efficient method may come later.</b>
     * 
     * @param in input stream
     * @param out output stream
     * @return bytes ready
     * @throws IOException on any IO error
     */
    public long replace(InputStream in, OutputStream out) throws IOException {
    	if (log.isDebugEnabled())
    		log.debug("Replacing using streams, reading stream into memory");
        StringBuffer str = new StringBuffer(4096);
        byte[] buf = new byte[32768];
        int read;
        while ((read = in.read(buf)) > -1) {
            str.append(charset == null ? new String(buf, 0, read) : new String(buf, 0, read, charset));
            if (log.isDebugEnabled())
            	log.debug("Got block of " + read + ", waiting for next one");
        }
        if (log.isDebugEnabled())
        	log.debug("Read all blocks, performing replacement");
        byte[] b = charset == null ? replace(str.toString()).getBytes() : replace(str.toString()).getBytes(charset);
        if (log.isDebugEnabled())
        	log.debug("Writing replaced content back (" + b.length + " bytes)");
        out.write(b);
        return b.length;
    }

    // Supporting methods

    private void replaceInto(Pattern pattern, String replacementPattern, Replacer replacer, StringBuffer input, StringBuffer work) {
        work.ensureCapacity(input.length());
        work.setLength(0);
        if (log.isDebugEnabled())
        	log.debug("Getting matcher for " + pattern.pattern());
        Matcher m = pattern.matcher(input);
        log.debug("Got matcher, finding first occurence.");
        while (m.find()) {
        	if (log.isDebugEnabled())
            	log.debug("Found occurence '" + m.group() + "'");
            String repl = replacer.getReplacement(pattern, m, replacementPattern);
            if (repl != null) {
            	if (log.isDebugEnabled())
            		log.debug("Found replacement, appending '" + repl + "'");
                if(encoder == null) {
                    m.appendReplacement(work, Util.escapeForRegexpReplacement(repl));
                }
                else {
                    m.appendReplacement(work, encoder.encode(Util.escapeForRegexpReplacement(repl)));                    
                }
            }
        }
        if (log.isDebugEnabled())
        	log.debug("Processed matches, appending replacement.");
        m.appendTail(work);
    }

    // Supporting classes

    class ReplaceOp {
        String pattern;
        Replacer replacer;
        String replacePattern;

        ReplaceOp(Replacer replacer, String pattern, String replacePattern) {
            this.replacer = replacer;
            this.pattern = pattern;
            this.replacePattern = replacePattern;
        }
    }

    /**
     * A cached pool of compiled regular expressions.
     */
    public static class PatternPool {
        private HashMap patterns;
        private HashMap locks;

        PatternPool() {
            patterns = new HashMap();
            locks = new HashMap();
        }

        /**
         * Get a compiled {@link Pattern} given the regular expression as text,
         * whether the match should be case sensitive and whether all matches on
         * every record (line) should be processed.
         * <p>
         * When a pattern is first requested a pool of 10 instances are created.
         * Sub-sequent requests for the same pattern will then return one of
         * these instances. The pattern will then be locked until
         * {@link #releasePattern(Pattern)} is called. If there are no unlocked
         * patterns in the pool, the caller will be blocked until one becomes
         * available.
         * 
         * @param pattern pattern
         * @param caseSensitive case sensitive match
         * @param dotAll match all matches on a single record
         * @return compiled patter
         */
        public Pattern getPattern(String pattern, boolean caseSensitive, boolean dotAll) {
            String cacheKey = pattern + "_" + caseSensitive + "_" + dotAll;
            List pool = null;
            synchronized (patterns) {
                pool = (List) patterns.get(cacheKey);
                if (pool == null) {
                    pool = new ArrayList();
                    patterns.put(cacheKey, pool);
                }
            }
            synchronized (pool) {
                while (true) {
                    if (pool.size() < 10) {
                        Pattern p = Pattern.compile(pattern, (!caseSensitive ? Pattern.CASE_INSENSITIVE : 0)
                                        + (dotAll ? Pattern.DOTALL : 0));
                        pool.add(p);
                        locks.put(p, p);
                        if (log.isDebugEnabled())
                        	log.debug("Created new pattern and locked");
                        return p;
                    } else {
                        for (Iterator i = pool.listIterator(); i.hasNext();) {
                            Pattern p = (Pattern) i.next();
                            if (!locks.containsKey(p)) {
                            	if (log.isDebugEnabled())
                            		log.debug("Found a free pattern");
                                locks.put(p, p);
                                return p;
                            }
                        }
                        synchronized (locks) {
                            try {
                            	if (log.isDebugEnabled())
                            		log.debug("No free patterns, waiting for one to become available");
                                locks.wait();
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }
        }

        /**
         * Release a patterns lock.
         * 
         * @param pattern pattern to release
         * @see #getPattern(String, boolean, boolean)
         */
        public void releasePattern(Pattern pattern) {
            synchronized (locks) {
                locks.remove(pattern);
                locks.notifyAll();
            }
        }
    }
    
    public interface Encoder {
        public String encode(String decoded);
    }

}