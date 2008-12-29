
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
			
package com.adito.notification;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.management.relation.RoleNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.core.CoreAttributeConstants;
import com.adito.core.CoreEvent;
import com.adito.core.CoreEventConstants;
import com.adito.core.CoreServlet;
import com.adito.core.UserDatabaseManager;
import com.adito.policyframework.Policy;
import com.adito.policyframework.PolicyDatabase;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.policyframework.Principal;
import com.adito.realms.Realm;
import com.adito.security.LogonControllerFactory;
import com.adito.security.Role;
import com.adito.security.User;
import com.adito.security.UserDatabase;
import com.adito.security.UserDatabaseException;

public class Notifier {
    final static Log log = LogFactory.getLog(Notifier.class);
    private List<MessageWrapper> messages;
    private boolean started;
    private File queueDirectory;
    private long messageId;
    private MessageConsumer messageConsumer;
    private boolean stop;
    private List<MessageSink> sinks;
    private HashMap<String,Boolean> sinkEnabled;

    public Notifier(File queueDirectory) throws IOException {
        messages = new ArrayList<MessageWrapper>();
        this.queueDirectory = queueDirectory;
        sinks = new ArrayList<MessageSink>();
        sinkEnabled = new HashMap<String,Boolean>();
        loadFromDisk();
    }

    public List<MessageSink> getSinks() {
        return sinks;
    }
    
    public Collection<MessageSink> getEnabledSinks() {
        Collection<MessageSink> enabledSinks = new ArrayList<MessageSink>();
        for (MessageSink sink : sinks) {
            if(isEnabled(sink.getName())) {
                enabledSinks.add(sink);
            }
        }
        return enabledSinks;
    }

    public void addSink(MessageSink sink, boolean enabled) {
        if(enabled && started) {
        
            try {
            sink.start(this);
            } catch(Exception ex) {
                log.error("Failed to start " + sink.getName() + " message sink", ex);
                return;
            }
        }
        
        sinks.add(sink);
        sinkEnabled.put(sink.getName(), Boolean.valueOf(enabled));
    }

    public void removeSink(MessageSink sink) {
        sinks.remove(sink);
        sinkEnabled.remove(sink.getName());
    }

    public boolean isEnabled(String sinkName) {
        return Boolean.TRUE.equals(sinkEnabled.get(sinkName));
    }

    public void start() throws IllegalStateException {
        if (started) {
            throw new IllegalStateException("Already started.");
        }
        messageConsumer = new MessageConsumer();
        if (sinks.size() == 0) {
            throw new IllegalStateException(
                            "At least one message sink must have been registered for the notfication queue to be started.");
        }
        for (Iterator i = sinks.iterator(); i.hasNext();) {
            MessageSink sink = (MessageSink) i.next();
            try {
            	if (log.isDebugEnabled())
            		log.debug("Starting message sink " + sink.getName());
                sink.start(this);
            } catch (Exception e) {
                log.error("Failed to start sink " + sink.getName() + ".", e);
            }
        }
        started = true;
        messageConsumer.start();
        if (log.isInfoEnabled())
        	log.info("Notifier started");
    }

    void loadFromDisk() throws IOException {
        File[] f = queueDirectory.listFiles(new FileFilter() {
            public boolean accept(File f) {
                return f.getName().endsWith(".msg");
            }
        });
        // TODO better error handling in parsing of message files. Report on
        // non-existant / unreadable directory
        if (f == null) {
            throw new IOException("Could not list queue directory " + queueDirectory.getAbsolutePath());
        }
        for (int i = 0; i < f.length; i++) {
            FileInputStream fin = new FileInputStream(f[i]);
            try {
                DataInputStream din = new DataInputStream(fin);
                long id = din.readLong();
                String sinkName = din.readUTF();
                messageId = Math.max(id, messageId);
                boolean urgent = din.readBoolean();
                String subject = din.readUTF();
                List<Recipient> recipientList = new ArrayList<Recipient>();
                while (true) {
                    int recipientType = din.readInt();
                    if (recipientType == Recipient.EOF) {
                        break;
                    } else {
                        String recipientAlias = din.readUTF();
                        String realmName = din.readUTF();
                        Recipient recipient = new Recipient(recipientType, recipientAlias, realmName);
                        recipientList.add(recipient);
                    }
                }
                Properties parameters = new Properties();
                while (true) {
                    int parameterType = din.readInt();
                    if (parameterType < 1) {
                        break;
                    } else {
                        String key = din.readUTF();
                        String val = din.readUTF();
                        parameters.setProperty(key, val);
                    }
                }
                String content = din.readUTF();
                String lastMessage = din.readUTF();
                Message msg = new Message(subject, content, urgent);
                msg.setId(id);
                msg.setRecipients(recipientList);
                msg.setSinkName(sinkName);
                msg.setLastMessage(lastMessage);
                queue(msg);
            } finally {
                fin.close();
            }
        }
    }

    public void stop() throws IllegalStateException {
        if (!started) {
            throw new IllegalStateException("Notifier is not started.");
        }
        stop = true;
        queueNotify();
        started = false;
        if (log.isInfoEnabled())
        	log.info("Waiting for up to 120 seconds message consumer to stop");
        try {
            messageConsumer.join(120000);
        } catch (InterruptedException ie) {
        }
        for (Iterator i = sinks.iterator(); i.hasNext();) {
            MessageSink sink = (MessageSink) i.next();
            try {
                sink.stop();
            } catch (Exception e) {
                log.error("Failed to stop sink " + sink.getName() + ".", e);
            }
        }
        if (log.isInfoEnabled())
        	log.info("Notifier stopped");
    }

	public boolean isStarted() {
		return started;
	}

    public void clearAllMessages() {
        synchronized (messages) {
            try {
                messages.clear();
                File[] f = queueDirectory.listFiles(new FileFilter() {
                    public boolean accept(File f) {
                        return f.getName().endsWith(".msg");
                    }
                });
                if (f != null) {
                    for (int i = 0; i < f.length; i++) {
                        f[i].delete();
                    }
                }
                CoreServlet.getServlet().fireCoreEvent(new CoreEvent(this, CoreEventConstants.MESSAGE_QUEUE_CLEARED, null, null, CoreEvent.STATE_SUCCESSFUL));
            } catch (Exception e) {
                log.error("Failed to clear messages from queue", e);
                CoreServlet.getServlet().fireCoreEvent(new CoreEvent(this, CoreEventConstants.MESSAGE_QUEUE_CLEARED, null, null, CoreEvent.STATE_UNSUCCESSFUL));
            }
        }
    }

    public void sendToAll(Message message) {
        sendToSink("*", message);
    }

    public void sendToAllExcept(Message message, String except) {
        sendToSink("!" + except, message);
    }

    public void sendToFirst(Message message) {
        sendToSink("^", message);
    }

    public void sendToSink(String sinkName, Message message) throws IllegalArgumentException {
        messageId++;
        message.setSinkName(sinkName);
        message.setId(messageId);
        if (log.isDebugEnabled())
        	log.debug("Sending message " + message.getId() + " '" + message.getSubject() + "' to sink '" + sinkName + "'");
        queue(message);
        try {
            write(message);
        } catch (IOException ioe) {
        }
    }

    public void setEnabled(String sinkName, boolean enabled) {
        sinkEnabled.put(sinkName, Boolean.valueOf(enabled));
        if (enabled) {
            queueNotify();
        }
    }

    public MessageWrapper getMessage(long messageId) {
        for (MessageWrapper wrapper : getMessages()) {
            if(wrapper.getMessage().getId() == messageId)
                return wrapper;
        }
        return null;
    }
    
    public List<MessageWrapper> getMessages() {
        return messages;
    }

    public int send(Collection<Recipient> recipients, MessageSender sender) throws Exception {
        for (Recipient recipient : recipients) {
            if (recipient.getRecipientType() == Recipient.USER) {
                sender.sendMessage(recipient.getRecipientAlias(), recipient);
            } else if (recipient.getRecipientType() == Recipient.POLICY) {
                sendByPolicy(sender, recipient);
            } else if (recipient.getRecipientType() == Recipient.ROLE) {
                sendByRole(sender, recipient);
            } else if (recipient.getRecipientType() == Recipient.ADMINS) {
                sendByAdmin(sender, recipient);
            }
        }
        return sender.getSentMessageCount();
    }

    private void sendByPolicy(MessageSender sender, Recipient recipient) throws Exception, UserDatabaseException {
        UserDatabase userDatabase = UserDatabaseManager.getInstance().getUserDatabase(recipient.getRealmName());
        PolicyDatabase policyDatabase = PolicyDatabaseFactory.getInstance();
        Realm realm = userDatabase.getRealm();
        Policy policy = policyDatabase.getPolicyByName(recipient.getRecipientAlias(), realm.getResourceId());
        int everyonePolicyId = policyDatabase.getEveryonePolicyIDForRealm(realm);
        
        if (everyonePolicyId == policy.getResourceId()) {
            sendToPrincipals(sender, userDatabase, userDatabase.allRoles());
            sendToPrincipals(sender, userDatabase, userDatabase.allUsers());
        } else {
            List<Principal> principals = policyDatabase.getPrincipalsGrantedPolicy(policy, realm);
            sendToPrincipals(sender, userDatabase, principals);
        }
    }

    private void sendToPrincipals(MessageSender sender, UserDatabase userDatabase, Iterable<? extends Principal> principals)
                    throws UserDatabaseException, Exception {
        for (Principal principal : principals) {
            if (principal instanceof Role) {
                sendByRole(sender, userDatabase, (Role) principal);
            } else {
                Realm realm = principal.getRealm();
                Recipient newRecipient = new Recipient(Recipient.USER, principal.getPrincipalName(), realm.getResourceName());
                sender.sendMessage(principal.getPrincipalName(), newRecipient);
            }
        }
    }

    private void sendByRole(MessageSender sender, Recipient recipient) throws Exception, RoleNotFoundException, UserDatabaseException {
        UserDatabase userDatabase = UserDatabaseManager.getInstance().getUserDatabase(recipient.getRealmName());
        Role role = userDatabase.getRole(recipient.getRecipientAlias());
        sendByRole(sender, userDatabase, role);
    }

    private void sendByRole(MessageSender sender, UserDatabase userDatabase, Role role) throws UserDatabaseException, Exception {
        User[] usersInRole = userDatabase.getUsersInRole(role);
        for (User user : usersInRole) {
            Recipient newRecipient = new Recipient(Recipient.USER, user.getPrincipalName(), user.getRealm().getResourceName());
            sender.sendMessage(user.getPrincipalName(), newRecipient);
        }
    }
    
    private void sendByAdmin(MessageSender sender, Recipient recipient) throws Exception, UserDatabaseException {
        UserDatabase userDatabase = UserDatabaseManager.getInstance().getUserDatabase(recipient.getRealmName());
        for (User user : userDatabase.allUsers()) {
            if (LogonControllerFactory.getInstance().isAdministrator(user)) {
                Recipient newRecipient = new Recipient(Recipient.USER, user.getPrincipalName(), user.getRealm().getResourceName());
                sender.sendMessage(user.getPrincipalName(), newRecipient);
            }
        }
    }
    
    MessageSink getSink(String sinkName) {
        MessageSink s;
        for (Iterator i = sinks.iterator(); i.hasNext();) {
            s = (MessageSink) i.next();
            if (s.getName().equals(sinkName)) {
                return s;
            }
        }
        return null;
    }

    void write(Message message) throws IOException {
    	if (log.isDebugEnabled())
    		log.debug("Writing message " + message.getId() + " '" + message.getSubject() + "' to disk");
        FileOutputStream fout = new FileOutputStream(new File(queueDirectory, String.valueOf(message.getId()) + ".msg"));
        try {
            DataOutputStream dout = new DataOutputStream(fout);
            dout.writeLong(message.getId());
            dout.writeUTF(message.getSinkName());
            dout.writeBoolean(message.isUrgent());
            dout.writeUTF(message.getSubject());
            for (Iterator i = message.getRecipients().iterator(); i.hasNext();) {
                Recipient r = (Recipient) i.next();
                dout.writeInt(r.getRecipientType());
                dout.writeUTF(r.getRecipientAlias() == null ? "" : r.getRecipientAlias());
                dout.writeUTF(r.getRealmName() == null ? "" : r.getRealmName());
            }
            dout.writeInt(0);
            for (Iterator i = message.getParameterNames(); i.hasNext();) {
                String key = (String) i.next();
                dout.writeInt(1);
                dout.writeUTF(key);
                dout.writeUTF(message.getParameter(key));
            }
            dout.writeInt(0);
            dout.writeUTF(message.getContent());
            dout.writeUTF(message.getLastMessage());
        } finally {
            fout.close();
        }
    }

    void queue(Message message) {
    	if (log.isDebugEnabled())
    		log.debug("Queueing message " + message.getId() + " '" + message.getSubject() + "'");
        MessageWrapper wrapper = new MessageWrapper(message);
        if (message.isUrgent()) {
            messages.add(0, wrapper);
            fireQueuedEvent(message);
            queueNotify();
        } else {
            messages.add(wrapper);
            fireQueuedEvent(message);
        }
    }

    void fireQueuedEvent(Message message) {
        fireMessageEvent(message, new CoreEvent(this, CoreEventConstants.MESSAGE_QUEUED, message, null, CoreEvent.STATE_SUCCESSFUL));
    }

    void fireSentEvent(Message message, int state) {
        fireMessageEvent(message, new CoreEvent(this, CoreEventConstants.MESSAGE_SENT, message, null, state));
    }

    void fireMessageEvent(Message message, CoreEvent evt) {
        List l = message.getRecipients();
        StringBuffer userR = new StringBuffer();
        StringBuffer roleR = new StringBuffer();
        StringBuffer policyR = new StringBuffer();
        for (Iterator i = l.iterator(); i.hasNext();) {
            Recipient r = (Recipient) i.next();
            StringBuffer rb = userR;
            if (r.getRecipientType() == Recipient.ROLE) {
                rb = roleR;
            } else if (r.getRecipientType() == Recipient.POLICY) {
                rb = policyR;
            }
            if (rb.length() > 0) {
                rb.append(",");
            }
            rb.append(r.getRecipientAlias());
        }
        CoreServlet.getServlet().fireCoreEvent(
                        evt.addAttribute(CoreAttributeConstants.EVENT_ATTR_MESSAGE_ID, String.valueOf(message.getId()))
                                        .addAttribute(CoreAttributeConstants.EVENT_ATTR_MESSAGE_URGENT,
                                                        String.valueOf(message.isUrgent())).addAttribute(
                                                        CoreAttributeConstants.EVENT_ATTR_MESSAGE_SUBJECT,
                                                        String.valueOf(message.getSubject()))
                                        .addAttribute(CoreAttributeConstants.EVENT_ATTR_MESSAGE_ROLE_RECIPIENTS, roleR.toString())
                                        .addAttribute(CoreAttributeConstants.EVENT_ATTR_MESSAGE_POLICY_RECIPIENTS,
                                                        policyR.toString()));
    }

    void queueNotify() {
    	if (log.isDebugEnabled())
    		log.debug("Notify queue");
        synchronized (messages) {
            messages.notifyAll();
        }
        if (log.isDebugEnabled())
        	log.debug("Queue notified");
    }

    public boolean doSend(String sinkName, Message message) {
        message.setSinkName(sinkName);
        return doSend(message);
    }

    boolean doSend(Message message) {
        
        if(log.isDebugEnabled())
            log.debug("Sending message with subject of " + message.getSubject() + " urgent = " + message.isUrgent());

        if (log.isDebugEnabled()) {
            for (Iterator i = message.getRecipients().iterator(); i.hasNext();) {
                Recipient r = (Recipient) i.next();
                log.debug("    " + r.getRecipientType() + "/" + r.getRecipientAlias());
            }
            log.debug("Content = " + message.getContent());
            log.debug("Sink name  = " + message.getSinkName());
        }

        boolean sent = false;
        if (message.getSinkName().equals("*")) {
            for (Iterator i = sinks.iterator(); i.hasNext();) {
                MessageSink sink = (MessageSink) i.next();
                if (Boolean.TRUE == sinkEnabled.get(sink.getName())) {
                    try {
                        if (sink.send(message)) {
                            sent = true;
                        }
                    } catch (Exception e) {
                        log.error("Failed to send message " + message.getId() + ".", e);
                    }
                }
            }
        } else if (message.getSinkName().startsWith("!")) {
            String[] except = message.getSinkName().substring(1).split(",");
            for (Iterator i = sinks.iterator(); i.hasNext();) {
                MessageSink sink = (MessageSink) i.next();
                boolean found = false;
                for (int j = 0; j < except.length && !found; j++) {
                    if (sink.getName().equals(except[j])) {
                        found = true;
                    }
                }
                if (!found && Boolean.TRUE == sinkEnabled.get(sink.getName())) {
                    try {
                        if (sink.send(message)) {
                            sent = true;
                        }
                    } catch (Exception e) {
                        log.error("Failed to send message " + message.getId() + ".", e);
                    }
                }
            }
        } else if (message.getSinkName().equals("^")) {
            for (Iterator i = sinks.iterator(); !sent && i.hasNext();) {
                MessageSink sink = (MessageSink) i.next();
                if (Boolean.TRUE == sinkEnabled.get(sink.getName())) {
                    try {
                        if (sink.send(message)) {
                            sent = true;
                        }
                    } catch (Exception e) {
                        log.error("Failed to send message " + message.getId() + ".", e);
                    }
                }
            }
        } else {
            MessageSink s = getSink(message.getSinkName());
            if (s == null) {
                log.error("No message sink named " + message.getSinkName());
            } else {
                if (Boolean.TRUE == sinkEnabled.get(s.getName())) {
                    try {
                        sent = s.send(message);
                    } catch (Exception e) {
                        message.setLastMessage(e.getMessage());
                        log.error("Failed to send message " + message.getId() + ".", e);
                        ;
                    }
                }
            }
        }
        if (!sent) {
            log.error("No message sink sent message " + message.getId());
            fireSentEvent(message, CoreEvent.STATE_UNSUCCESSFUL);
        }
        else {
            fireSentEvent(message, CoreEvent.STATE_SUCCESSFUL);
        }
        return sent;
    }

    class MessageConsumer extends Thread {
        MessageConsumer() {
            super("Notification Message Consumer");
        }

        public void run() {
            stop = false;
            MessageWrapper msg = null;
            int valid = -1;
            boolean waitOnce = false;
            while (!stop) {
                synchronized (messages) {
                    while ((waitOnce || messages.size() == 0) && !stop) {
                        try {
                            messages.wait(30000);
                        } catch (InterruptedException ie) {
                            log.error("MessageConsumer interrupted.", ie);
                        }
                        waitOnce = false;
                    }
                    if (!stop) {
                    	if (log.isDebugEnabled())
                    		log.debug("Checking message queue");
                        int i = 0;
                        valid = -1;
                        while (i < messages.size()) {
                            msg = (MessageWrapper) messages.get(i);
                            if (log.isDebugEnabled())
                            	log.debug("Checking if message " + msg.getMessage().getId() + " is valid");
                            // If the message has been attempted before, check
                            // whether its ready for retry
                            if (msg.attempt == 0 || (msg.attempt > 0 && (msg.time.getTime() + 60000) < System.currentTimeMillis())) {
                                valid = i;
                                break;
                            } else {
                                i++;
                            }
                        }
                        if (valid != -1) {
                            messages.remove(valid);
                        }
                    }
                }
                if (!stop) {
                    boolean sent = false;
                    if (valid != -1) {
                        sent = doSend(msg.message);
                        if (!sent) {
                            msg.attempt++;
                            msg.time = new Date();
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException ie) {
                            }
                            log.error("Failed to send message. " + msg.getMessage().getLastMessage());
                            messages.add(msg);
                        } else {
                            new File(queueDirectory, msg.message.getId() + ".msg").delete();
                        }
                    } else {
                        waitOnce = true;
                    }
                }
            }
        }
    }

    public class MessageWrapper {
        private final Message message;
        private Date time;
        private int attempt;

        private MessageWrapper(Message message) {
            this.message = message;
            this.time = new Date();
        }

        public Message getMessage() {
            return message;
        }

        public int getAttempt() {
            return attempt;
        }

        public Date getTime() {
            return time;
        }
        
        public String getFormattedTime() {
            DateFormat sdf = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);
            return sdf.format(getTime());
        }
        
        public String getUserRecipients() {
            return getRecipients(Recipient.USER);
        }
        
        public String getRoleRecipients() {
            return getRecipients(Recipient.ROLE);
        }
        
        public String getPolicyRecipients() {
            return getRecipients(Recipient.POLICY);
        }
        
        private String getRecipients(int type) {
            List<String> recipients = new ArrayList<String>();
            for (Recipient recipient : message.getRecipients()) {
                if(recipient.getRecipientType() == type) {
                    recipients.add(recipient.getRecipientAlias());
                }
            }
            Collections.sort(recipients);

            StringBuffer buffer = new StringBuffer();
            for (String value : recipients) {
                buffer.append(value).append(", ");
            }
            return (buffer.length() == 0 ) ? "" : buffer.substring(0, buffer.length() - 2);
        }
    }
}