package net.openvpn.als.sample;

import java.util.Calendar;

import net.openvpn.als.policyframework.AbstractResource;

/**
 * <p>
 * Implemetation of {@link Sample}. Provides a default implementation for use
 * within the system.
 * 
 * @author James D Robinson <a href="mailto:james@localhost">&lt;james@localhost&gt;</a>
 * 
 */
public class DefaultSample extends AbstractResource implements Sample {

    /**
     * @param resourceId The resource id.
     * @param resourceName The resource name.
     * @param resourceDescription The resource description.
     * @param parentResourcePermission The parent resource permission.
     * @param dateCreated The date created.
     * @param dateAmended The date amended.
     */
    public DefaultSample(int resourceId, String resourceName, String resourceDescription, int parentResourcePermission,
                         Calendar dateCreated, Calendar dateAmended) {
        super(Sample.SAMPLE_RESOURCE_TYPE, resourceId, resourceName, resourceDescription, parentResourcePermission, dateCreated,
                        dateAmended);
        // TODO set up any other attributes u want access to.
    }
}
