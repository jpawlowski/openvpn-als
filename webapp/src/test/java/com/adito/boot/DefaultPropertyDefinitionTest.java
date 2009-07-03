package com.adito.boot;


				/*
 *  Adito
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
			

import org.junit.Assert;
import org.junit.Test;

import com.adito.boot.CodedException;
import com.adito.boot.DefaultPropertyDefinition;

/**
 * Tests {@link DefaultPropertyDefinition}.
 */
public class DefaultPropertyDefinitionTest {

    /**
     */
    @Test
    public void constructors() {

        DefaultPropertyDefinition def1 = new DefaultPropertyDefinition(DefaultPropertyDefinition.TYPE_BOOLEAN, "con1", "on,off",
                        10, "true", 50, true);

        Assert.assertEquals(DefaultPropertyDefinition.TYPE_BOOLEAN, def1.getType());
        Assert.assertEquals("con1", def1.getName());
        Assert.assertEquals("on,off", def1.getTypeMeta());
        Assert.assertEquals(10, def1.getCategory());
        Assert.assertEquals("true", def1.getDefaultValue());
        Assert.assertEquals(50, def1.getSortOrder());
        Assert.assertEquals("properties", def1.getMessageResourcesKey());
        Assert.assertEquals(null, def1.getValidationString());
        Assert.assertEquals(true, def1.isHidden());

        DefaultPropertyDefinition def2 = new DefaultPropertyDefinition(DefaultPropertyDefinition.TYPE_BOOLEAN, "con2", "on,off",
                        10, "true", 50, "test", true, null, null);

        Assert.assertEquals(DefaultPropertyDefinition.TYPE_BOOLEAN, def2.getType());
        Assert.assertEquals("con2", def2.getName());
        Assert.assertEquals("on,off", def2.getTypeMeta());
        Assert.assertEquals(10, def2.getCategory());
        Assert.assertEquals("true", def2.getDefaultValue());
        Assert.assertEquals(50, def2.getSortOrder());
        Assert.assertEquals("test", def2.getMessageResourcesKey());
        Assert.assertEquals(null, def2.getValidationString());
        Assert.assertEquals(true, def2.isHidden());

        DefaultPropertyDefinition def3 = new DefaultPropertyDefinition(DefaultPropertyDefinition.TYPE_BOOLEAN, "con3", "on,off",
                        10, "true", 50, true, null);

        Assert.assertEquals(DefaultPropertyDefinition.TYPE_BOOLEAN, def3.getType());
        Assert.assertEquals("con3", def3.getName());
        Assert.assertEquals("on,off", def3.getTypeMeta());
        Assert.assertEquals(10, def3.getCategory());
        Assert.assertEquals("true", def3.getDefaultValue());
        Assert.assertEquals(50, def3.getSortOrder());
        Assert.assertEquals("properties", def3.getMessageResourcesKey());
        Assert.assertEquals(null, def3.getValidationString());
        Assert.assertEquals(true, def3.isHidden());

        DefaultPropertyDefinition def4 = new DefaultPropertyDefinition(DefaultPropertyDefinition.TYPE_BOOLEAN, "con4", "on,off",
                        10, "true", 50, "test", true, null, null, null);

        Assert.assertEquals(DefaultPropertyDefinition.TYPE_BOOLEAN, def4.getType());
        Assert.assertEquals("con4", def4.getName());
        Assert.assertEquals("on,off", def4.getTypeMeta());
        Assert.assertEquals(10, def4.getCategory());
        Assert.assertEquals("true", def4.getDefaultValue());
        Assert.assertEquals(50, def4.getSortOrder());
        Assert.assertEquals("test", def4.getMessageResourcesKey());
        Assert.assertEquals(null, def4.getValidationString());
        Assert.assertEquals(true, def4.isHidden());

    }

    /**
     */
    @Test
    public void defaultBooleanPropertyDefinitionWithNoValidator() {
        DefaultPropertyDefinition def = new DefaultPropertyDefinition(DefaultPropertyDefinition.TYPE_BOOLEAN, "bool1", "on,off",
                        10, "on", 50, "test", true, "com.adito.input.validators.BooleanValidator", null, null);
        try {
            def.validate("off", getClass().getClassLoader());
        } catch (CodedException ce) {
            Assert.fail("Validation failed when it shouldn't.");
        }
    }

    /**
     */
    @Test
    public void defaultIntegerPropertyDefinitionWithDefaultValidator() {
        DefaultPropertyDefinition def = new DefaultPropertyDefinition(DefaultPropertyDefinition.TYPE_INTEGER, "int1", "", 10, "5",
                        50, "test", true, "com.adito.input.validators.IntegerValidator", null, null);
        try {
            def.validate("10", getClass().getClassLoader());
        }
        catch(CodedException ce) {
            Assert.fail("Validation failed when it shouldn't.");
        }        
        try {
            def.validate("10000000000000", getClass().getClassLoader());
            Assert.fail("Validation didn't fail when it should.");
        }
        catch(CodedException ce) {
        }
    }
}