
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
			
package net.openvpn.als.notification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;


/**
 * Encapsulate a single message that is delivered by the {@link Notifier}
 * to one or more {@link MessageSink} implementations.
 * <p>
 * A message must have a least one {@link Recipient}.
 * <p>
 * A message may be marked as <i>Urgent</i> in which case it is placed
 * at the top of the queue.
 */
public class Message {
	
	//	Private instance variables
	
    private String subject;
    private String content;
    private List<Recipient> recipients;
    private boolean urgent;
    private long id;
    private String sinkName;
    private Properties parameters;
    private String lastMessage;

    /**
     * Constructor.
     * 
     * @param subject subject
     * @param content content
     * @param urgent urgent
     */
    public Message(String subject, String content, boolean urgent) {
        this.subject = subject;
        this.content = content;
        this.urgent = urgent;
        recipients = new ArrayList<Recipient>();
        parameters = new Properties();
        lastMessage = "";
    }

    /**
     * Constructor.
     * 
     * @param subject subject
     * @param content content
     * @param urgent urgent
     * @param recipient recipient
     */
    public Message(String subject, String content, boolean urgent, Recipient recipient) {
        this(subject, content, urgent);
        recipients.add(recipient);
    }

    /**
     * @return Returns the id.
     */
    public long getId() {
        return id;
    }

    /**
     * @param id The id to set.
     */
    public void setId(long id) {
        this.id = id;
    }
    
    /**
     * Set a parameter. It will be up to the {@link net.openvpn.als.notification.MessageSink}
     * to understand these parameters and deal with them accordingly.
     * 
     * @param key parameter key
     * @param value parameter value
     */
    public void setParameter(String key, String value) {
        parameters.setProperty(key, value);
    }
    
    /**
     * Get a parameter. It will be up to the {@link net.openvpn.als.notification.MessageSink}
     * to understand these parameters and deal with them accordingly.
     * 
     * @param key parameter key
     * @return value parameter value or <code>null</code> if no such parameter exists
     */
    public String getParameter(String key) {
        return parameters.getProperty(key);
    }
    
    /**
     * Get a parameter. It will be up to the {@link net.openvpn.als.notification.MessageSink}
     * to understand these parameters and deal with them accordingly.
     * 
     * @param key parameter key
     * @param defaultValue default value if no parameter exists
     * @return value parameter value or <code>null</code> if no such parameter exists
     */
    public String getParameter(String key, String defaultValue) {
        return parameters.getProperty(key, defaultValue);
    }
    
    /**
     * Get an {@link Iterator} of parameter names
     * 
     * @return parameter names
     */
    public Iterator getParameterNames() {
        return parameters.keySet().iterator();
    }

    /**
     * Get the content of this message.
     * 
     * @return content
     */
    public String getContent() {
        return content;
    }

    /**
     * Set the content of this message.
     * 
     * @param content content of message
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Get the list of recipients this message should be sent to
     * 
     * @return recipients
     */
    public List<Recipient> getRecipients() {
        return recipients;
    }

    /**
     * Set the list of recipients for this messages.
     * 
     * @param recipients recipients
     */
    public void setRecipients(List<Recipient> recipients) {
        this.recipients = recipients;
    }

    /**
     * Get the subject for this message.
     * 
     * @return subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Set the subject for this message.
     * 
     * @param subject subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Get if this message is urgent. If so it will be placed at the 
     * top of the message queue.
     * 
     * @return urgent
     */
    public boolean isUrgent() {
        return urgent;
    }

    /**
     * Set if this message is urgent. If so it will be placed at the 
     * top of the message queue.
     * 
     * @param urgent urgent
     */
    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }
    
    /**
     * Get the name of the sink this message should be delivered to.
     * 
     * @return sink name
     */
    public String getSinkName() {
        return sinkName;
    }

    /**
     * Get the any status text that may have been generated the last
     * time this message was sent.
     * 
     * @return last status message
     */
    public String getLastMessage() {
        return lastMessage;
    }


    /**
     * Set the any status text that may have been generated the last
     * time this message was sent.
     * 
     * @param lastMessage last status message
     */
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    
    protected void setSinkName(String sinkName) {
        this.sinkName = sinkName;
    }
}
