package net.openvpn.als.policyframework;

import java.util.Calendar;
import java.util.List;

import net.openvpn.als.core.UserDatabaseManager;
import net.openvpn.als.security.Role;
import net.openvpn.als.security.User;

public class DefaultPolicy extends AbstractResource implements Policy {

    private int type;
    private boolean addToFavorite = false;
    private int attachedUsers = 0;
    private int attachedGroups = 0;

    /**
     * Create a blank policy
     * 
     * @param selectedRealmId
     */
    public DefaultPolicy(int selectedRealmId) {
        this(-1, "", "", Policy.TYPE_INVISIBLE, Calendar.getInstance(), Calendar.getInstance(), selectedRealmId);
    }

    /**
     * Create a new empty policy of the specified type, ID and name.
     * @param realmID 
     * @param uniqueId policy id
     * @param name name of policy
     * @param description description
     * @param type type
     * @param dateCreated the date / time this policy was created
     * @param dateAmended the date / time this policy was last amended
     */
    public DefaultPolicy(int uniqueId, String name, String description, int type,
                    Calendar dateCreated, Calendar dateAmended, int realmID) {
        super(realmID, PolicyConstants.POLICY_RESOURCE_TYPE, uniqueId, name, description, dateCreated, dateAmended);
        try {
            this.type = type;
            List<Principal> principalsGrantedPolicy = PolicyDatabaseFactory.getInstance().getPrincipalsGrantedPolicy(this, UserDatabaseManager.getInstance().getRealm(realmID));
            for (Principal principal : principalsGrantedPolicy) {
                if (principal instanceof User){
                    attachedUsers ++;
                }
                else if (principal instanceof Role){
                    attachedGroups ++;
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.policyframework.Policy#getType()
     */
    public int getType() {
        return type;
    }
    
    /* (non-Javadoc)
     * @see net.openvpn.als.policyframework.Policy#setType(int)
     */
    public void setType(int type) {
        this.type = type;
    }

    public boolean isAddToFavorite() {
        return addToFavorite;
    }

    public void setAddToFavorite(boolean addToFavorite) {
        this.addToFavorite = addToFavorite;
    }

    public int getAttachedGroups() {
        return attachedGroups;
    }

    public int getAttachedUsers() {
        return attachedUsers;
    }
}
