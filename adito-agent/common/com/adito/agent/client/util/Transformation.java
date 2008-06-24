package com.adito.agent.client.util;

import java.io.IOException;

/**
 * Interface to be implemented by classes that can take a block of text (maybe
 * the contents of a file) and transform it in some way. These are loaded by the
 * {@link ParameterTransformation} that process &lt;transform&gt; elements in an
 * application extension descriptor.
 */
public interface Transformation {

    /**
     * Transform a block of text
     * 
     * @param input
     * @return transformed text
     * @throws IOException
     */
    public String transform(String input) throws IOException;
}
