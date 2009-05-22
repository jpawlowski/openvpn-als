
				/*
 *  OpenVPN-ALS
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
			
package com.ovpnals.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import com.maverick.crypto.security.SecureRandom;
import com.ovpnals.boot.PropertyClassManager;
import com.ovpnals.boot.Util;
import com.ovpnals.core.CoreUtil;
import com.ovpnals.core.PageInterceptException;
import com.ovpnals.core.PageInterceptListener;
import com.ovpnals.core.RequestParameterMap;
import com.ovpnals.properties.Property;
import com.ovpnals.properties.attributes.AttributeDefinition;
import com.ovpnals.properties.impl.userattributes.UserAttributeKey;
import com.ovpnals.properties.impl.userattributes.UserAttributes;
import com.ovpnals.security.actions.SetPersonalAnswersAction;
import com.ovpnals.security.actions.ShowSetPersonalAnswersAction;

/**
 */
public class PersonalQuestionsAuthenticationModule implements AuthenticationModule {

    final static Log log = LogFactory.getLog(PersonalQuestionsAuthenticationModule.class);

    private AuthenticationScheme session;
    private PasswordCredentials credentials;
    private boolean required;

    public final static String[] SECURITY_QUESTIONS  = new String[] {"fathersFirstName", "placeOfBirth", "favoriteBook", "mothersMaidenName", "favoriteTVShow" };
    private int currentQuestion;
    private String currentQuestionLabel;
    
    public static final String MODULE_NAME = "PersonalQuestions";

    /**
     *  
     */
    PersonalQuestionsAuthenticationModule() {
        super();
    }
    
    public String getCurrentQuestion() {
        return currentQuestionLabel;
    }

    public String getName() {
        return MODULE_NAME;
    }

    /* (non-Javadoc)
     * @see com.ovpnals.security.AuthenticationModule#authenticate(javax.servlet.http.HttpServletRequest, com.ovpnals.core.RequestParameterMap)
     */
    public Credentials authenticate(HttpServletRequest request, RequestParameterMap parameters)
                    throws InvalidLoginCredentialsException, SecurityErrorException, AccountLockedException {

        
        if (session.getUser() == null) {
            throw new SecurityErrorException(SecurityErrorException.INTERNAL_ERROR, "Personal Question authentication module "
                            + "requires that a previous authentication module has already loaded the User.");
        }

        String answer = request.getParameter("answer");
        if(answer == null || "".equals(answer)) {
            throw new InvalidLoginCredentialsException("No answer has been provided.");
        }
        
        answer = normalizeAnswer(answer.trim());
        
        String actualAnswer = normalizeAnswer(Property.getProperty(new UserAttributeKey(session.getUser(), SECURITY_QUESTIONS[currentQuestion])));
        if(actualAnswer == null)
        	throw new InvalidLoginCredentialsException("Sorry but you do not have an answer configured. Contact your administrator");
        
        if(actualAnswer.equals(answer)) {
        	return new PersonalQuestionsCredentials(session.getUsername(), currentQuestion, answer);        	
        }

        throw new InvalidLoginCredentialsException("Your answer was incorrect.");
    }
    
    static String normalizeAnswer(String answer) {
        StringBuffer buf = new StringBuffer();
        answer = Util.trimBoth(answer.toLowerCase());
        char ch;
        for(int i = 0 ; i < answer.length() ; i++) {
            ch = answer.charAt(i);
            if(ch != ' ') {
                buf.append(ch);
            }
        }
        return buf.toString();
        
    }

    public String getInclude() {
        return "/WEB-INF/jsp/auth/personalQuestionsAuth.jspf";
    }

    public void init(AuthenticationScheme session) {
        this.session = session;

    }

    public void authenticationComplete() throws SecurityErrorException {
    }

    public ActionForward startAuthentication(ActionMapping mapping, HttpServletRequest request, HttpServletResponse response)
                    throws SecurityErrorException {
        required = true;

        try {
            
        	if(session==null)
        		throw new SecurityErrorException(SecurityErrorException.INTERNAL_ERROR, "Invalid use of personal questions module");
        	
        	String answer;
        	boolean canAuth = true;
        	for(int i=0; i< SECURITY_QUESTIONS.length;i++) {
        		answer = Property.getProperty(new UserAttributeKey(session.getUser(), SECURITY_QUESTIONS[i]));
        		if(answer == null || "".equals(answer)) {
        			canAuth = false;
        			break;
        		}
        	}

            
            if(canAuth) {
                /* 
                 * Keep the current question in the session as well so the only way a new question can
                 * be loaded is by restarting the browser
                 */
            	if(request.getSession().getAttribute(Constants.PERSONAL_QUESTION)==null) {
            	    currentQuestion = SecureRandom.getInstance().nextInt(5);
            	    request.getSession().setAttribute(Constants.PERSONAL_QUESTION, new Integer(currentQuestion));
            	} else {
            		currentQuestion = ((Integer)request.getSession().getAttribute(Constants.PERSONAL_QUESTION)).intValue();
            	
            	}

            	currentQuestionLabel = ((AttributeDefinition)PropertyClassManager.getInstance().getPropertyClass(UserAttributes.NAME).getDefinition(SECURITY_QUESTIONS[currentQuestion])).getLabel();
            	required = true;
            } else {
                CoreUtil.addPageInterceptListener(session.getServletSession(), new PersonalAnswersChangeInterceptListener());
                request.getSession().setAttribute(Constants.REQ_ATTR_PERSONAL_ANSWERS_CHANGE_REASON_MESSAGE,
                                	new ActionMessage("setPersonalAnswers.message.personalAnswersNotSet"));
                required = false;
            }
            
            
        } catch(SecurityErrorException ie) {
            throw ie;
        } catch (Exception e) {
            log.error(e);
            throw new SecurityErrorException(SecurityErrorException.INTERNAL_ERROR, e,  "Failed to get personal questions.");
        }
        return mapping.findForward("display");        
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.security.AuthenticationModule#isRequired()
     */
    public boolean isRequired() {
        return required;
    }

//    class PINChangeInterceptListener implements PageInterceptListener {
//
//        public String getId() {
//            return "changePIN";
//        }
//
//        public ActionForward checkForForward(Action action, ActionMapping mapping, HttpServletRequest request,
//                        HttpServletResponse response) throws PageInterceptException {
//            if (!(action instanceof ShowChangePINAction) && !(action instanceof ChangePINAction)) {
//                return new ActionForward("/showChangePIN.do", true);
//            }
//            return null;
//        }
//
//        /* (non-Javadoc)
//         * @see com.ovpnals.core.PageInterceptListener#isRedirect()
//         */
//        public boolean isRedirect() {
//            return false;
//        }
//    }
    


    class PersonalAnswersChangeInterceptListener implements PageInterceptListener {

        public String getId() {
            return "changePersonalAnswers";
        }

        public ActionForward checkForForward(Action action, ActionMapping mapping, HttpServletRequest request,
                        HttpServletResponse response) throws PageInterceptException {
            if (!(action instanceof ShowSetPersonalAnswersAction) && !(action instanceof SetPersonalAnswersAction)) {
                return new ActionForward("/showSetPersonalAnswers.do", true);
            }
            return null;
        }

        public boolean isRedirect() {
            return false;
        }
    }

}