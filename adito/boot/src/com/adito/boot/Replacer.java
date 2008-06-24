/* HEADER  */
package com.adito.boot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * When replacement patterns are registered with a
 * {@link com.adito.boot.ReplacementEngine}, an implementation of this
 * interface must also be provided.
 * <p>
 * When the engine is processing some content and finds a match the
 * {@link #getReplacement(Pattern, Matcher, String)} method will be invoked
 * expecting the replacement value to be returned.
 * <p>
 * A replacement pattern <i>may</i> also has been provided which the
 * implementation may or may not make use of.
 */
public interface Replacer {

    /**
     * Get the value to replace the matched content with.
     * 
     * @param pattern pattern that made the match
     * @param matcher the matcher that made the match
     * @param replacementPattern the optional replacement pattern that may have
     *        been provided when the pattern was registered with the replacement
     *        engine
     * @return replacement value or <code>null</code> to replace nothing
     */
    public String getReplacement(Pattern pattern, Matcher matcher, String replacementPattern);
}