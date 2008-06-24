
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
			
package com.adito.setup;

import com.adito.notification.Message;
import com.adito.notification.Notifier.MessageWrapper;
import com.adito.table.TableItem;

/**
 */
public class MessageTableItem implements TableItem {
    
    private final MessageWrapper messageWrapper;
    
    /**
     * @param messageWrapper
     */
    public MessageTableItem(MessageWrapper messageWrapper) {
        this.messageWrapper = messageWrapper;
    }
    
    /**
     * @return String
     */
    public long getId() {
        return messageWrapper.getMessage().getId();
    }
    
    /**
     * @return Message
     */
    public Message getMessage() {
        return messageWrapper.getMessage();
    }
    
    /**
     * @return String
     */
    public String getAttempts() {
        return String.valueOf(messageWrapper.getAttempt());
    }
    
    /**
     * @return String
     */
    public String getLastAttempt() {
        return messageWrapper.getFormattedTime();
    }

    public Object getColumnValue(int col) {
        switch(col) {
            case 0:
                return getMessage().getSubject();
            case 1:
                return new Boolean(getMessage().isUrgent());                   
            case 2:
                return new Integer(getAttempts());                   
            case 3:
                return messageWrapper.getTime();
            default:
                return "";
        }
    }
    
    /**
     * @return String
     */
    public String getLink() {
        return "#";
    }
    
    /**
     * @return String
     */
    public String getOnClick() {
        return "";
    }
}