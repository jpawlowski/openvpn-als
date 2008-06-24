
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
			
package com.adito.security;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class PersonalQuestion {
    
    /* TODO At the moment, personal questions are mostly hard-coded although the text may
     * be changed by directly editing the resource files
     */
    public static List PERSONAL_QUESTIONS;
    
    static {
        PERSONAL_QUESTIONS = new ArrayList();
        PERSONAL_QUESTIONS.add(new PersonalQuestion("personalQuestion.1"));
        PERSONAL_QUESTIONS.add(new PersonalQuestion("personalQuestion.2"));
        PERSONAL_QUESTIONS.add(new PersonalQuestion("personalQuestion.3"));
        PERSONAL_QUESTIONS.add(new PersonalQuestion("personalQuestion.4"));
        PERSONAL_QUESTIONS.add(new PersonalQuestion("personalQuestion.5"));
    }
    
    
    private String id;
    
    /**
     * @param string
     */
    public PersonalQuestion(String id) {
        super();
        this.id = id;
    }

    /**
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }
    
    /**
     * @param id The id to set.
     */
    public void setId(String id) {
        this.id = id;
    }
}
