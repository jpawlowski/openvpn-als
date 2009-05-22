package com.ovpnals.security;

public class UserDatabaseDefinition {
    
    private Class userDatabaseClass;
    private int installationCategory;
    private String name, messageResourcesKey;
    
    public UserDatabaseDefinition(Class udbClass, String name, String messageResourcesKey, int installationCategory) {
        this.userDatabaseClass = udbClass;
        this.installationCategory = installationCategory;
        this.name = name;
        this.messageResourcesKey = messageResourcesKey;
    }

    public int getInstallationCategory() {
        return installationCategory;
    }

    public Class getUserDatabaseClass() {
        return userDatabaseClass;
    }
    
    public String getMessageResourcesKey() {
        return messageResourcesKey;
    }

    public String getName() {
        return name;
    }

}
