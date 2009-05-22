
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
			
package com.ovpnals.setup.forms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.ovpnals.core.CoreServlet;
import com.ovpnals.notification.MessageSink;
import com.ovpnals.notification.Notifier;
import com.ovpnals.setup.MessageSinkItem;
import com.ovpnals.setup.MessageTableItem;
import com.ovpnals.table.AbstractTableItemTableModel;
import com.ovpnals.table.forms.AbstractPagerForm;

/**
 */
public class MessageQueueForm extends AbstractPagerForm {
    private List<MessageSinkItem> messageSinks;
    private String selectedSink;
    
    /**
     */
    public MessageQueueForm() {
        super(new MessageQueueTableModel());
    } 
    
    /**
     * @return List<MessageSinkItem>
     */
    public List<MessageSinkItem> getMessageSinks() {
        return messageSinks;
    }
    
    /**
     * @param session
     */
    public void initialize(HttpSession session) {
        super.initialize(session, "subject");
        getModel().clear();
        Notifier notifier = CoreServlet.getServlet().getNotifier();
        synchronized(notifier) {
            List messages = notifier.getMessages();
            for(Iterator itr = messages.iterator(); itr.hasNext(); ) {       
                Notifier.MessageWrapper next = (Notifier.MessageWrapper)itr.next();
                getModel().addItem(new MessageTableItem((next)));                
            }
            List<MessageSink> messageSinks = CoreServlet.getServlet().getNotifier().getSinks();
            List<MessageSinkItem> sinks = new ArrayList<MessageSinkItem>();
            for(Iterator i = messageSinks.iterator(); i.hasNext(); ) {
                MessageSink ms = (MessageSink)i.next();
                sinks.add(new MessageSinkItem(ms));
            }
            setMessageSinks(sinks);
            getPager().rebuild(null);
        }
    }

    /**
     * @param messageSinks
     */
    public void setMessageSinks(List<MessageSinkItem> messageSinks) {
        this.messageSinks = messageSinks;
    }
    
    /**
     * @return String
     */
    public String getSelectedSink() {
        return selectedSink;
    }

    /**
     * @param selectedSink
     */
    public void setSelectedSink(String selectedSink) {
        this.selectedSink = selectedSink;
    }
    
    private static final class MessageQueueTableModel extends AbstractTableItemTableModel {

        public int getColumnWidth(int col) {
            return 0;
        }

        public String getId() {
            return "mesageQueue.messages";
        }

        public int getColumnCount() {
            return 4;
        }

        public String getColumnName(int col) {
            switch(col) {
                case 0:
                    return "subject";
                case 1:
                    return "urgent";
                case 2:
                    return "attempts";
                case 3:
                    return "lastTime";
                default:
                    return "";
            }
        }

        public Class getColumnClass(int col) {
            switch(col) {
                case 0:
                    return Boolean.class;
                default:
                    return String.class;
            }
        }
    }
}