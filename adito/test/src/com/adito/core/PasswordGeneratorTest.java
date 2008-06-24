
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
			
package com.adito.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

/**
 */
public class PasswordGeneratorTest {

    /**
     */
    @Test
    public void generateNumericPassword() {
        testNumbers(10);
    }

    /**
     */
    @Test
    public void generateNumericPasswordLarge() {
        testNumbers(100);
    }
    
    /**
     */
    @Test
    public void generateNumericPasswordSmall() {
        testNumbers(1);
    }
    
    /**
     */
    @Test
    public void generateNumericPasswordHuge() {
        testNumbers(1000);
    }
    
    private void testNumbers(int length) {
        String generatePassword = generatePassword("numeric", length);
        assertOnly(generatePassword, 48, 57); // ASCII 0-9 range
    }
    
    /**
     */
    @Test
    public void generateAlphaPassword() {
        testLetters(10);
    }
    
    /**
     */
    @Test
    public void generateAlphaPasswordLarge() {
        testLetters(100);
    }
    
    /**
     */
    @Test
    public void generateAlphaPasswordSmall() {
        testLetters(1);
    }
    
    /**
     */
    @Test
    public void generateAlphaPasswordHuge() {
        testLetters(1000);
    }
    
    private void testLetters(int length) {
        String generatePassword = generatePassword("alpha", length);
        assertOnly(generatePassword, 97, 122); // ASCII a-z
    }

    /**
     */
    @Test
    public void generateAlphaNumericPassword() {
        testNumbersAndLetters(10);
    }
    
    /**
     */
    @Test
    public void generateAlphaNumericPasswordLarge() {
        testNumbersAndLetters(100);
    }
    
    /**
     */
    @Test
    public void generateAlphaNumericPasswordSmall() {
        testNumbersAndLetters(1);
    }
    
    /**
     */
    @Test
    public void generateAlphaNumericPasswordHuge() {
        testNumbersAndLetters(1000);
    }
    
    private void testNumbersAndLetters(int length) {
        String generatePassword = generatePassword("alphanumeric", length);
        generatePassword = replaceCharactersWithWhiteSpace(generatePassword, 48, 57); // ASCII 0-9
        assertOnly(generatePassword, 97, 122); // ASCII a-z
    }
    
    /**
     */
    @Test
    public void generateASCIIPassword() {
        testAscii(10);
    }
    
    /**
     */
    @Test
    public void generateASCIIPasswordLarge() {
        testAscii(100);
    }
    
    /**
     */
    @Test
    public void generateASCIIPasswordSmall() {
        testAscii(1);
    }
    
    /**
     */
    @Test
    public void generateASCIIPasswordHuge() {
        testAscii(1000);
    }
    
    private void testAscii(int length) {
        String generatePassword = generatePassword("ascii", length);
        assertOnly(generatePassword, 32, 126); // ASCII 'space' - ~
    }
    
    /**
     */
    @Test
    public void generatePhoneticPassword() {
        testPhonetic(10);
    }
    
    /**
     */
    @Test
    public void generatePhoneticPasswordLarge() {
        testPhonetic(100);
    }
    
    /**
     */
    @Test
    public void generatePhoneticPasswordSmall() {
        testPhonetic(1);
    }
    
    /**
     */
    @Test
    public void generatePhoneticPasswordHuge() {
        testPhonetic(1000);
    }
    
    private void testPhonetic(int length) {
        String generatePassword = generatePassword("phonetic", length);
        assertOnly(generatePassword, 97, 122); // ASCII a-z
    }

    /**
     */
    @Test
    public void generateThousandNumericPasswordsAllDifferent() {
        testRandomPasswordGeneration("numeric");
    }
    
    /**
     */
    @Test
    public void generateThousandAlphaPasswordsAllDifferent() {
        testRandomPasswordGeneration("alpha");
    }
    
    /**
     */
    @Test
    public void generateThousandAlphaNumericPasswordsAllDifferent() {
        testRandomPasswordGeneration("alphanumeric");
    }
    
    /**
     */
    @Test
    public void generateThousandASCIIPasswordsAllDifferent() {
        testRandomPasswordGeneration("ascii");
    }
    
    /**
     */
    @Test
    public void generateThousandPhoneticPasswordsAllDifferent() {
        testRandomPasswordGeneration("phonetic");
    }
    
    private void testRandomPasswordGeneration(String generationType) {
        Collection<String> passwords = new ArrayList<String>();
        final int generationSize = 1000;
        for (int index = 0; index < generationSize; index++) {
            String generatePassword = PasswordGenerationFactory.generatePassword(generationType, 10);
            if (!passwords.contains(generatePassword)){
                passwords.add(generatePassword);
            }
        }
        assertEquals("There should be 1000 different passwords", generationSize, passwords.size());
    }
    
    /**
     */
    @Test(expected = IllegalArgumentException.class)
    public void unknownGenerationType() {
        PasswordGenerationFactory.generatePassword("qweqwe", 10);
    }
    
    private String generatePassword(String generationType, int length) {
        String generatePassword = PasswordGenerationFactory.generatePassword(generationType, length);
        assertNotNull(generatePassword);
        if (!generationType.equals("phonetic")){ // length test not valid fro phonetic.
            assertEquals(length, generatePassword.length());
        }
        return generatePassword;
    }
    
    private void assertOnly(String generatePassword, int startIndex, int endIndex) {
        generatePassword = replaceCharactersWithWhiteSpace(generatePassword, startIndex, endIndex);
        assertEquals("The password will be empty as all numbers have been removed.", "", generatePassword.trim());
    }

    private String replaceCharactersWithWhiteSpace(String generatePassword, int startIndex, int endIndex) {
        for(int index = startIndex; index <= endIndex; index++) {
            generatePassword = generatePassword.replace((char) index, ' ');
        }
        return generatePassword;
    }
}