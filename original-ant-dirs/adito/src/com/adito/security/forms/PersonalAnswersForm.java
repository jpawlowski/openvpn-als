
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
			
package com.adito.security.forms;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import com.adito.core.forms.CoreForm;
import com.adito.security.PersonalAnswer;

public class PersonalAnswersForm extends CoreForm {

    static Log log = LogFactory.getLog(PersonalAnswersForm.class);

    private List personalAnswers;

    public void initialize(List personalAnswers) {
        this.personalAnswers = personalAnswers;
    }
    
    public List getPersonalAnswers() {
        return personalAnswers;
    }
    
    public void setPersonalAnswers(List personalAnswers) {
        this.personalAnswers = personalAnswers;
    }
    
    public PersonalAnswer getPersonalAnswer(int idx) {
      return (PersonalAnswer)personalAnswers.get(idx);
    }
    
    public void setPersonalAnswer(int idx, PersonalAnswer personalAnswer) {
        personalAnswers.set(idx, personalAnswer);
    }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors err = new ActionErrors();
        for(Iterator i = personalAnswers.iterator(); i.hasNext();  ) {
            PersonalAnswer answer = (PersonalAnswer)i.next();
            if(answer.getAnswer().equals("")) {
                err.add(Globals.ERROR_KEY, new ActionMessage("setPersonalAnswers.error.emptyAnswer"));
                break;
            }
        }
        return err;
    }
}
