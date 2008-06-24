package com.adito.sample;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.adito.core.CoreServlet;
import com.adito.core.Database;
import com.adito.policyframework.Resource;

/**
 * <p>
 * In Memory storage for the Sample Resorces.
 * 
 * @author James D Robinson <a href="mailto:james@localhost">&lt;james@localhost&gt;</a>
 * 
 */
public class SampleDatabase implements Database {

    private Map data;
    private int counter;

    /**
     * Constructor
     */
    public SampleDatabase() {
        this.data = new HashMap();
        this.counter = 0;
    }

    /**
     * @return A List of all SampleDatabase.
     */
    public List getSamples() {
        return new ArrayList(this.data.values());
    }

    /**
     * @param id The id of the Sample
     * @return The sample with the specified id.
     */
    public Sample getSample(int id) {
        if (this.data.containsKey(String.valueOf(id))) {
            return (Sample) this.data.get(String.valueOf(id));
        } else {
            return null;
        }
    }

    /**
     * @param name The name of the sample
     * @param description The description of the sample
     * @param parentResourcePermission The parent resource permission of the
     *        sample.
     * @return The newly created sample.
     * @throws Exception If the sample already exists with the latest id.
     */
    public Sample addSample(String name, String description, int parentResourcePermission) throws Exception {
        Calendar now = Calendar.getInstance();
        Sample sample = new DefaultSample(counter, name, description, parentResourcePermission, now, now);
        if (this.data.containsKey(String.valueOf(counter))) {
            throw new Exception("Sample id already exists.");
        } else {
            this.data.put(String.valueOf(sample.getResourceId()), sample);
            counter++;
            return sample;
        }
    }

    /**
     * @param name The name of the sample to retrieve.
     * @return The resource.
     */
    public Resource getSample(String name) {
        for (Iterator iter = this.data.values().iterator(); iter.hasNext();) {
            Sample element = (Sample) iter.next();
            if (element.getResourceName().equals(name)) {
                return element;
            }
            return null;
        }

        return null;
    }

    /**
     * @param resourceId The resource id to be removed.
     * @return the sample that was removed
     * @throws Exception If the resource doesnot exist.
     */
    public Sample removeSample(int resourceId) throws Exception {
        Sample s = getSample(resourceId);
        if (s != null) {
            this.data.remove(String.valueOf(resourceId));
        } else {
            throw new Exception("No Sample with id " + resourceId);
        }
        return s;
    }

    /**
     * @param sample The sample to update.
     * @throws Exception If there is not a resource with the same name.
     */
    public void updateSample(Sample sample) throws Exception {
        Calendar now = Calendar.getInstance();
        sample.setDateAmended(now);
        if (this.data.containsKey(String.valueOf(sample.getResourceId()))) {
            this.data.put(String.valueOf(sample.getResourceId()), sample);
        } else {
            throw new Exception("The resource id does not exist.");
        }
    }
    
    /* (non-Javadoc)
     * @see com.adito.core.Database#cleanup()
     */
    public void cleanup() throws Exception {
        
    }

    /* (non-Javadoc)
     * @see com.adito.core.Database#open(com.adito.core.CoreServlet)
     */
    public void open(CoreServlet controllingServlet) throws Exception {
        
    }

    /* (non-Javadoc)
     * @see com.adito.core.Database#close()
     */
    public void close() throws Exception {        
    }
}
