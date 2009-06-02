
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
			

package net.openvpn.als.boot;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests {@link XMLPropertyDefinition}.
 */
public class XMLPropertyDefinitionTest {

    /**
     * @throws JDOMException
     */
    @Test
    public void fromXMLElement() throws JDOMException {
        Element el = new Element("definition");
        el.setAttribute("name", "def1");;
        el.setAttribute("type", String.valueOf(XMLPropertyDefinition.TYPE_INTEGER));
        el.setAttribute("typeMeta", "");
        el.setAttribute("category", "15");
        el.setAttribute("defaultValue", "20");
        el.setAttribute("sortOrder", "50");
        el.setAttribute("validation", "net.openvpn.als.input.validators.IntegerValidator");
        
        XMLPropertyDefinition def1 = new XMLPropertyDefinition(el);
        
        Assert.assertEquals(DefaultPropertyDefinition.TYPE_INTEGER, def1.getType());
        Assert.assertEquals("def1", def1.getName());
        Assert.assertEquals("", def1.getTypeMeta());
        Assert.assertEquals(15, def1.getCategory());
        Assert.assertEquals("20", def1.getDefaultValue());
        Assert.assertEquals(50, def1.getSortOrder());
        Assert.assertEquals("properties", def1.getMessageResourcesKey());
        Assert.assertNotNull(def1.getValidationString());
        Assert.assertEquals(false, def1.isHidden());
        
        try {
            def1.validate("20", getClass().getClassLoader());
        }
        catch(CodedException ce) {
            Assert.fail("Validation failed when it shouldn't.");
        }        
        try {
            def1.validate("10000000000000", getClass().getClassLoader());
            Assert.fail("Validation didn't fail when it should.");
        }
        catch(CodedException ce) {
        }

        el.setAttribute("messageResourcesKey", "test");
        el.setAttribute("hidden", "true");
        el.setAttribute("validation", "net.openvpn.als.input.validators.IntegerValidator(minValue=10,maxValue=100)");
        
        XMLPropertyDefinition def2 = new XMLPropertyDefinition(el);

        Assert.assertEquals("test", def2.getMessageResourcesKey());
        Assert.assertEquals(true, def2.isHidden());
        Assert.assertEquals("net.openvpn.als.input.validators.IntegerValidator(minValue=10,maxValue=100)", def2.getValidationString());

        try {
            def2.validate("20", getClass().getClassLoader());
        }
        catch(CodedException ce) {
            Assert.fail("Validation failed when it shouldn't.");
        }        
        try {
            def2.validate("101", getClass().getClassLoader());
            Assert.fail("Validation didn't fail when it should.");
        }
        catch(CodedException ce) {
        }
    }
}