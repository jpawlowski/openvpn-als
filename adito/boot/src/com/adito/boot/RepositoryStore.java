package com.adito.boot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A single {@link Repository} may contain multiple stores. This interface
 * abstracts these stores.
 * <p>
 * A single store may contain multiple named <i>Entries</i>. Each of these
 * entries is simple a blob of data that may accessed via I/O streams.
 */
public interface RepositoryStore {

    /**
     * Get a named entry as an input stream. If not such entry exists then
     * a {@link java.io.FileNotFoundException} will be thrown.
     * 
     * @param name name of entry
     * @return input stream
     * @throws IOException on any error including file not found
     */
	public InputStream getEntryInputStream(String name) throws IOException;
	
    /**
     * Get an output stream for a named entry which may be written to. If
     * an entry with the same name already exists it will be overwritten.
     * 
     * @param name name of entry
     * @return output stream which may be written to
     * @throws IOException on any error
     */
	public OutputStream getEntryOutputStream(String name) throws IOException;
	
    /**
     * Remove an entry given its name.
     * 
     * @param name name of entry to remove
     * @throws IOException on any error
     */
	public void removeEntry(String name) throws IOException;
	
    /**
     * Get if a named entry exists.
     * 
     * @param name name of entry
     * @return entry exists
     */
	public boolean hasEntry(String name);
	
    /**
     * Get a list of all entry names.
     * 
     * @return entry names
     */
	public String[] listEntries();
}
