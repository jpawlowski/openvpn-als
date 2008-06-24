
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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Password Generation Factory for creating passwords with various restrictions.
 */
public final class PasswordGenerationFactory {

    // set of unwanted characters for alpha numeric.
    private static final Set<Integer> invalidChars = new HashSet<Integer>();
    private static final Map<String, Generator> generators = new HashMap<String, Generator>();

    static {
        invalidChars.add(58);
        invalidChars.add(59);
        invalidChars.add(60);
        invalidChars.add(61);
        invalidChars.add(62);
        invalidChars.add(63);
        invalidChars.add(64);
        
        generators.put("phonetic", new Generator(){
            public String generate(int length) {
                return phonetic(length);
            }
        });
        generators.put("alpha", new Generator(){
            public String generate(int length) {
                return alpha(length);
            }
        });
        generators.put("numeric", new Generator(){
            public String generate(int length) {
                return numeric(length);
            }
        });
        generators.put("alphanumeric", new Generator(){
            public String generate(int length) {
                return alphaNumeric(length);
            }
        });
        generators.put("ascii", new Generator(){
            public String generate(int length) {
                return ascii(length);
            }
        });

    }

    private PasswordGenerationFactory(){
    }
    
    private static String phonetic(int length) {
        Random rnd = new Random();
        int seed = rnd.nextInt();
        if (seed < 0) {
            seed = -seed;
        }
        return generatePhoneticPassword(seed, length);
    }

    private static String generatePhoneticPassword(int seed, int length) {
        PasswordGenerator pwd = new PasswordGenerator();
        return pwd.generate(seed, length);
    }

    private static String ascii(int length) {
        return generate(length, 93, 33);
    }

    private static String alpha(int length) {
        return generate(length, 26, 65);
    }

    private static String numeric(int length) {
        return generate(length, 10, 48);
    }

    private static String alphaNumeric(int length) {
        return generate(length, 36, 48, invalidChars);
    }

    private static String generate(int length, int bound, int asciiRange) {
        return generate(length, bound, asciiRange, Collections.<Integer> emptySet());
    }

    private static String generate(int length, int bound, int asciiRange, Set<Integer> invalidChars) {
        Random rand = new Random();
        char[] password = new char[length];
        for (int index = 0; index < length; index++) {
            int randDecimalAsciiVal = rand.nextInt(bound) + asciiRange;
            if (!invalidChars.contains(randDecimalAsciiVal)) {
                password[index] = (char) randDecimalAsciiVal;
            } else {
                index--;
            }
        }
        return String.valueOf(password).toLowerCase();
    }

    /**
     * Generate a password for the given type and a random seed.
     * 
     * @param generationType
     * @param length
     * @return String the generated Password
     * @throws IllegalArgumentException
     */
    public static String generatePassword(String generationType, int length) throws IllegalArgumentException {
        if (!generators.containsKey(generationType)){
            throw new IllegalArgumentException("The generation type '" + generationType + "' is not supported");
        }
        return generators.get(generationType).generate(length);
        
    }

    /**
     * Generate a phonetic password with a random seed.
     * 
     * @param length
     * @return String the generated Password
     */
    public static String generatePassword(int length) {
        return phonetic(length);
    }

    /**
     * Generate a phonetic password with the specified seed.
     * 
     * @param seed
     * @param length
     * @return String the generated Password
     */
    public static String generatePassword(int seed, int length) {
        return generatePhoneticPassword(seed, length);
    }

    interface Generator{
        
        /**
         * @param length
         * @return String
         */
        String generate(int length);
    }
}
