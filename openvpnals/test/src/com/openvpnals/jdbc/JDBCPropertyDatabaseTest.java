
				/*
 *  OpenVPNALS
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
			
package net.openvpn.als.jdbc;

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.openvpn.als.boot.PropertyClassManager;
import net.openvpn.als.core.CoreServlet;
import net.openvpn.als.properties.attributes.AbstractXMLDefinedAttributesPropertyClass;
import net.openvpn.als.properties.attributes.AttributeDefinition;
import net.openvpn.als.properties.impl.policyattributes.PolicyAttributeKey;
import net.openvpn.als.properties.impl.policyattributes.PolicyAttributes;
import net.openvpn.als.properties.impl.userattributes.UserAttributeKey;
import net.openvpn.als.properties.impl.userattributes.UserAttributes;
import net.openvpn.als.security.User;
import net.openvpn.als.testcontainer.AbstractTest;

/**
 * 
 */
public class JDBCPropertyDatabaseTest extends AbstractTest {

    private static JDBCPropertyDatabase propertyDatabase;
    
    /**
     * @throws Exception
     */
    @BeforeClass
    public static void oneTimeSetUp() throws Exception {
        setUp("");
        propertyDatabase = new JDBCPropertyDatabase();
        propertyDatabase.open(CoreServlet.getServlet());
    }

    /**
     * @throws Exception
     */
    @AfterClass
    public static void after() throws Exception {
        propertyDatabase.close();
    }
    
    /**
     * @param attribute
     * @param attributeDefinition
     * @throws Exception
     */

    public void createAttributeDefinition(AbstractXMLDefinedAttributesPropertyClass attribute, AttributeDefinition attributeDefinition) throws Exception {
        attribute.registerPropertyDefinition(attributeDefinition);
        attributeDefinition.init(attribute);
        propertyDatabase.createAttributeDefinition(attributeDefinition);
        PropertyClassManager.getInstance().registerPropertyClass(attribute);
    }
    
    /**
     * @param attribute
     * @param attributeDefinition
     * @throws Exception
     */
    public void updateAttributeDefinition(AbstractXMLDefinedAttributesPropertyClass attribute, AttributeDefinition attributeDefinition) throws Exception {
        propertyDatabase.updateAttributeDefinition(attributeDefinition);
    }
    
    /**
     * @param attribute
     * @param definitionName
     * @throws Exception
     */
    public void deletAttributeDefinitionName(AbstractXMLDefinedAttributesPropertyClass attribute, String definitionName) throws Exception {
        propertyDatabase.deleteAttributeDefinition(attribute.getName(), definitionName);
        attribute.deregisterPropertyDefinition(definitionName);
        PropertyClassManager.getInstance().deregisterPropertyClass(attribute.getName());
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void loadAttributeDefinitions() throws Exception {
        propertyDatabase.loadAttributeDefinitions();
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void createUserAttributesDefinition() throws Exception {
        UserAttributes attribute = new UserAttributes();
        AttributeDefinition attributeDefinition = attribute.createAttributeDefinition(AttributeDefinition.TYPE_UNDEFINED, "AttributName", "typeMeta", -1, "categorylabel", "default value", AttributeDefinition.TYPE_UNDEFINED, 0, "", false, "Label", "Description", false, true, "");
        createAttributeDefinition(attribute, attributeDefinition);
        Assert.assertTrue("Should exist.", attribute.isDefinitionExists(attributeDefinition.getName()));
        deletAttributeDefinitionName(attribute, attributeDefinition.getName());
        Assert.assertFalse("Should not exist.", attribute.isDefinitionExists(attributeDefinition.getName()));
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void createPolicyAttributesDefinition() throws Exception {
        UserAttributes attribute = new UserAttributes();
        AttributeDefinition attributeDefinition = attribute.createAttributeDefinition(AttributeDefinition.TYPE_UNDEFINED, "AttributName", "typeMeta", -1, "categorylabel", "default value", AttributeDefinition.TYPE_UNDEFINED, 0, "", false, "Label", "Description", false, true, "");
        createAttributeDefinition(attribute, attributeDefinition);
        Assert.assertTrue("Should exist.", attribute.isDefinitionExists(attributeDefinition.getName()));
        deletAttributeDefinitionName(attribute, attributeDefinition.getName());
        Assert.assertFalse("Should not exist.", attribute.isDefinitionExists(attributeDefinition.getName()));
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void updateUserAttributeDefinitionName() throws Exception {
        UserAttributes attribute = new UserAttributes();
        AttributeDefinition attributeDefinition = attribute.createAttributeDefinition(AttributeDefinition.TYPE_UNDEFINED, "NewAttributName", "typeMeta", -1, "categorylabel", "default value", AttributeDefinition.TYPE_UNDEFINED, 0, "", false, "Label", "Description", false, true, "");
        createAttributeDefinition(attribute, attributeDefinition);
        Assert.assertTrue("Should exist.", attribute.isDefinitionExists(attributeDefinition.getName()));
        updateAttributeDefinition(attribute, attributeDefinition);
        deletAttributeDefinitionName(attribute, attributeDefinition.getName());
        Assert.assertFalse("Should not exist.", attribute.isDefinitionExists(attributeDefinition.getName()));
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void updatePolicyAttributeDefinitionName() throws Exception {
        PolicyAttributes attribute = new PolicyAttributes();
        AttributeDefinition attributeDefinition = attribute.createAttributeDefinition(AttributeDefinition.TYPE_UNDEFINED, "NewAttributName", "typeMeta", -1, "categorylabel", "default value", AttributeDefinition.TYPE_UNDEFINED, 0, "", false, "Label", "Description", false, true, "");
        createAttributeDefinition(attribute, attributeDefinition);
        Assert.assertTrue("Should exist.", attribute.isDefinitionExists(attributeDefinition.getName()));
        updateAttributeDefinition(attribute, attributeDefinition);
        deletAttributeDefinitionName(attribute, attributeDefinition.getName());
        Assert.assertFalse("Should not exist.", attribute.isDefinitionExists(attributeDefinition.getName()));
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void updateSystemUserAttributeDefinitionName() throws Exception {
        User user = createAccount();
        UserAttributes attribute = new UserAttributes();
        UserAttributeKey attributeKey = new UserAttributeKey(user, "fathersFirstName");
        AttributeDefinition attributeDefinition = (AttributeDefinition)attribute.getDefinition(attributeKey.getName());
        try {
            updateAttributeDefinition(attribute, attributeDefinition); 
            fail("This should have failed");
        } catch (Exception e) {
            // ignore
        }        
        deleteAccount(user);
    }    
    
    /**
     * @throws Exception
     */
    @Test
    public void deleteSystemUserAttributeDefinitionName() throws Exception {
        UserAttributes attribute = new UserAttributes();
        try {
            deletAttributeDefinitionName(attribute, "fathersFirstName");
            fail("This should have failed");
        } catch (Exception e) {
            // ignore
        }   
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void deleteNoExistingAttributeDefinition() throws Exception {
        UserAttributes attribute = new UserAttributes();
        try {
            deletAttributeDefinitionName(attribute, "none");
            fail("This should have failed");
        } catch (Exception e) {
            // ignore
        } 
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void storeAndRetrieveUserAttributeValue() throws Exception {
        String value = "new value";
        User user = createAccount();
        UserAttributeKey key = new UserAttributeKey(user, "newKey");
        propertyDatabase.storeAttributeValue(key, value);
        String retrievedValue = propertyDatabase.retrieveAttributeValue(key);
        Assert.assertEquals("Attribute value should be this one.", value, retrievedValue);
        propertyDatabase.storeAttributeValue(key, null);
        deleteAccount(user);
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void storeAndRetrievePolicyAttributeValue() throws Exception {
        String value = "new value";
        PolicyAttributeKey key = new PolicyAttributeKey(0, "newKey");
        propertyDatabase.storeAttributeValue(key, value);
        String retrievedValue = propertyDatabase.retrieveAttributeValue(key);
        Assert.assertEquals("Attribute value should be this one.", value, retrievedValue);
        propertyDatabase.storeAttributeValue(key, null);
    }
    
}
