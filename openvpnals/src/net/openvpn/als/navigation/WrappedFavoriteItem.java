package net.openvpn.als.navigation;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.struts.taglib.TagUtils;
import org.apache.struts.util.MessageResources;

import net.openvpn.als.policyframework.ResourceType;
import net.openvpn.als.table.TableItem;

/**
 * Implementation of a {@link net.openvpn.als.table.TableItem} that wraps an
 * {@link AbstractFavoriteItem}. This is used for the <i>Favorites</i> page
 * that lists the favorites of all resource types.
 */
public class WrappedFavoriteItem implements TableItem {

    // Private instance variables
    private AbstractFavoriteItem favoriteItem;
    private String type;

    /**
     * Constructor.
     * 
     * @param favoriteItem item
     * @param type will be one of {@link AbstractFavoriteItem#GLOBAL_FAVORITE} or
     *        {@link AbstractFavoriteItem#USER_FAVORITE}.
     */
    public WrappedFavoriteItem(AbstractFavoriteItem favoriteItem, String type) {
        this.favoriteItem = favoriteItem;
        this.type = type;
    }

    /**
     * Get the favorite type. Will be one of {@link AbstractFavoriteItem#GLOBAL_FAVORITE} or
     * {@link AbstractFavoriteItem#USER_FAVORITE}.
     * 
     * @return favorite type
     */
    public String getFavoriteType() {
        return type;
    }

    /**
     * Get the favorite item this table item is wrapping
     * 
     * @return favorite item
     */
    public AbstractFavoriteItem getFavoriteItem() {
        return favoriteItem;
    }

    /**
     * Get the resource type as a string
     * 
     * @param pageContext page context from which to get the message resources
     * @return resource type string
     * @throws JspException on error retrieving message resources
     */
    public String getResourceTypeName(PageContext pageContext) throws JspException {
        ResourceType rt = getFavoriteItem().getResource() == null ? null : getFavoriteItem().getResource().getResourceType();
        if (rt == null) {
            return "?";
        } else {
            MessageResources mr = TagUtils.getInstance().retrieveMessageResources(pageContext, rt.getBundle(), false);
            rt.getBundle();
            if (mr == null) {
                return "!Invalid bundle!";
            } else {
                String msg = mr.getMessage("resourceType." + rt.getResourceTypeId() + ".title");
                return msg == null || msg.equals("") ? "!No message!" : msg;
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.table.TableItem#getColumnValue(int)
     */
    public Object getColumnValue(int col) {
        return favoriteItem.getResource().getResourceName();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        try {
            return getFavoriteItem().getResource().getResourceId() == ((WrappedFavoriteItem) obj).getFavoriteItem().getResource()
                .getResourceId()
                            && getFavoriteItem().getResource().getResourceType() == ((WrappedFavoriteItem) obj).getFavoriteItem()
                                .getResource().getResourceType();
        } catch (ClassCastException cse) {
            return false;
        }
    }

}