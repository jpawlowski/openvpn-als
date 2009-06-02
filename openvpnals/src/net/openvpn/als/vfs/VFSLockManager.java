
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
			
package net.openvpn.als.vfs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;

import net.openvpn.als.security.LogonController;
import net.openvpn.als.security.LogonControllerFactory;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.vfs.webdav.DAVUtilities;
import net.openvpn.als.vfs.webdav.LockedException;

/**
 * 
 */
public class VFSLockManager {
    /**
     * A simple class to maintain global locks on files. Locks should only be
     * requested when a resource is being edited. 
     */
    private final static Log log = LogFactory.getLog(VFSLockManager.class);
    private static long HANDLE = 0;
    private static final Object LOCK = new Object();
    private static VFSLockManager instance;

    private final Map<String, NonExclusiveLock> nonExclusiveLocks_ = new HashMap<String, NonExclusiveLock>();
    private final Map<String, ExclusiveLock> exclusiveLocks_ = new HashMap<String, ExclusiveLock>();
    private final Map<String, Collection<Lock>> locksByHandle_ = new HashMap<String, Collection<Lock>>();
    private final boolean debug_ = false;
    
    VFSLockManager() {
        addLogging();
    }

    private void addLogging() {
        if (!log.isDebugEnabled() && !debug_)
            return;
        
        Thread thread = new Thread() {
            public void run() {
                while (true) {
                    sleepPlease();

                    if (exclusiveLocks_.size() > 0) {
                        log.debug("***Exclusive Locks***");
                        for (ExclusiveLock lock : exclusiveLocks_.values())
                            log.debug("   " + lock.getSession() + " - " + lock.getResource().getFullPath());
                    } else {
                        log.debug("***No outstanding exclusive locks***");
                    }
                    if (nonExclusiveLocks_.size() > 0) {
                        log.debug("***Non-exclusive Locks***");
                        for (NonExclusiveLock lock : nonExclusiveLocks_.values()) {
                            log.debug("   " + lock.getResource().getFullPath() + " open by ");
                            for (SessionInfo sessionInfo : lock.getSessions())
                                log.debug("    - " + sessionInfo.toString());
                        }
                    } else {
                        log.debug("***No outstanding non-exclusive locks***");
                    }
                }
            }
        };
         thread.start();
    }

    private static void sleepPlease() {
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
        }
    }

    /**
     * Generates a unique handle for a file.
     * 
     * @return the handle
     */
    public static String getNewHandle() {
        synchronized (LOCK) {
            return System.currentTimeMillis() + "" + (HANDLE++);
        }
    }

    /**
     * Does what it says on the tin.
     * 
     * @return the instance
     */
    public static VFSLockManager getInstance() {
        return instance == null ? instance = new VFSLockManager() : instance;
    }

    /**
     * 
     * @param resource
     * @param session
     * @param exclusive
     * @param lockParent
     * @param handle
     * @throws LockedException
     * @throws IOException
     */
    public synchronized void lock(VFSResource resource, SessionInfo session, boolean exclusive, boolean lockParent, String handle)
                    throws LockedException, IOException {
        String key = resource.getWebFolderPath();
        if (log.isDebugEnabled())
            log.debug("Attempting to " + (exclusive ? "exclusively" : "non-exclusively") + " lock resource " + key);

        // First of all try to soft lock the parent
        if (lockParent && resource.getParent() != null) {
            lock(resource.getParent(), session, false, false, handle);
        }

        if (!locksByHandle_.containsKey(handle))
            locksByHandle_.put(handle, new ArrayList<Lock>());

        Collection<Lock> locks = locksByHandle_.get(handle);
        if (exclusive && exclusiveLocks_.containsKey(key)) {
            ExclusiveLock lock = exclusiveLocks_.get(key);
            if (!lock.isLockOwner(session))
                throw new LockedException("File is currently locked exclusively by session " + lock.getSession());
        } else if (nonExclusiveLocks_.containsKey(key)) {
            NonExclusiveLock lock = nonExclusiveLocks_.get(key);
            if (exclusive && !lock.isLockOwner(session))
                throw new LockedException("Cannot lock file exclusivley; there are currently " + lock.getSessions().size()
                                + " sessions using it non-exclusivley");
            lock.incrementLock(session);
        } else if (exclusive) {
            ExclusiveLock lock = new ExclusiveLock(resource, session, handle);
            exclusiveLocks_.put(key, lock);
            locks.add(lock);
        } else {
            NonExclusiveLock lock = new NonExclusiveLock(resource, session, handle);
            nonExclusiveLocks_.put(key, lock);
            locks.add(lock);
        }

        if (log.isDebugEnabled())
            log.debug((exclusive ? "Exclusively" : "Non-exclusively") + " locked " + key);
    }

    /** 
     * @param handle
     */
    public synchronized void unlock(String handle) {
        Collection<Lock> locks = locksByHandle_.get(handle);
        
        try {
            for (Iterator itr = locks.iterator(); itr.hasNext();) {
                Lock lock = (Lock) itr.next();
                unlock(lock);
                itr.remove();
            }
        } finally {
            if(locks.isEmpty())
                locksByHandle_.remove(handle);
        }
    }
    
    /**
     * @param session
     * @param handle
     */
    public synchronized void unlock(SessionInfo session, String handle) {
        Collection<Lock> locks = locksByHandle_.get(handle);

        try {
            for (Iterator itr = locks.iterator(); itr.hasNext();) {
                Lock lock = (Lock) itr.next();
                if (lock.removeLock(handle, session)) {
                    unlock(lock);
                    itr.remove();
                }                
            }
        } finally {
            if(locks.isEmpty())
                locksByHandle_.remove(handle);
        }
    }

    private void unlock(Lock lock) {
        String webFolderPath = lock.getResource().getWebFolderPath();
        Map lockMap = lock instanceof ExclusiveLock ? exclusiveLocks_ : nonExclusiveLocks_;
        lockMap.remove(webFolderPath);
    }
    
    /**
     * @return <code>java.util.Collection<VFSFileLock></code> containing all the currently held locks
     */
    public synchronized Collection<VFSFileLock> getCurrentLocks () {
        Collection<VFSFileLock> lockedFiles = new TreeSet<VFSFileLock> ();
        for (Map.Entry<String, Collection<Lock>> entry : locksByHandle_.entrySet()) {
            String handle = entry.getKey();
            for (Lock lock : entry.getValue()) {
                try {
                    FileObject file = lock.getResource().getFile();
                    if(file!=null)
                    {
                        FileName name = file.getName();
                        String baseName = name.getBaseName();
                        String friendlyURI = DAVUtilities.stripUserInfo(name.getURI().toString());
                        boolean sessionsActive = areSessionsActive(lock);
                        lockedFiles.add(new VFSFileLock(baseName, friendlyURI, sessionsActive, handle));
                    }
                } catch (IOException e) {
                    // ignore
                }                
            }
        }
        return lockedFiles;
    }
    
    @SuppressWarnings("unchecked")
    private static boolean areSessionsActive (Lock lock)
    {
        Collection<SessionInfo> lockOwners = lock.getLockOwners();
        if (lockOwners.isEmpty())
            return false;
        
        LogonController logonController = LogonControllerFactory.getInstance();
        Collection<SessionInfo> sessions = logonController.getActiveSessions().values();
        
        int lockOwnerCount = lockOwners.size();
        lockOwners.removeAll(sessions);
        return lockOwners.isEmpty() || (lockOwners.size() != lockOwnerCount);
    }

    private static final class ExclusiveLock implements Lock {
        private final VFSResource resource_;
        private final SessionInfo session_;
        private final String handle_;

        private ExclusiveLock(VFSResource resource, SessionInfo session, String handle) {
            resource_ = resource;
            session_ = session;
            handle_ = handle;
        }

        public VFSResource getResource() {
            return resource_;
        }

        private SessionInfo getSession() {
            return session_;
        }

        private String getHandle() {
            return handle_;
        }

        public boolean removeLock(String handle, SessionInfo sessionInfo) {
            if (!getHandle().equals(handle)) {
                log.error("User attempting to unlock resource is not the lock owner");
                return false;
            }

            if (log.isDebugEnabled())
                log.debug("Exclusive lock for " + getResource().getWebFolderPath() + " has been removed");
            return true;
        }

        public boolean isLockOwner(SessionInfo sessionInfo) {
            return session_.getUser().equals(sessionInfo.getUser());
        }
        
        public Collection<SessionInfo> getLockOwners() {
            Collection<SessionInfo> lockOwners = new HashSet<SessionInfo>(1);
            lockOwners.add(session_);
            return lockOwners;
        }

        @Override
        public String toString() {
            StringBuffer buffer = new StringBuffer();
            buffer.append("ExclusiveLock@");
            buffer.append("[").append("Resource='").append(resource_.toString()).append("', ");
            buffer.append("Session='").append(getSession().toString()).append("', ");
            buffer.append("Handle='").append(getHandle()).append("']");
            return buffer.toString();
        }
    }

    private static final class NonExclusiveLock implements Lock {
        private final VFSResource resource_;
        private final HashSet<SessionInfo> sessions_;
        private final String handle_;
        
        NonExclusiveLock(VFSResource resource, SessionInfo session, String handle) {
            resource_ = resource;
            sessions_ = new HashSet<SessionInfo>();
            incrementLock(session);
            handle_ = handle;
        }

        @SuppressWarnings("unchecked")
        private Collection<SessionInfo> getSessions() {
            return (Collection<SessionInfo>) sessions_.clone();
        }

        public VFSResource getResource() {
            return resource_;
        }
        
        private String getHandle() {
            return handle_;
        }

        private int getCount() {
            return sessions_.size();
        }

        private void incrementLock(SessionInfo session) {
            sessions_.add(session);
        }

        private boolean decrementLock(SessionInfo session) {
            if (sessions_.isEmpty())
                return true;
            sessions_.remove(session);
            return sessions_.isEmpty();
        }
        
        public boolean removeLock(String handle, SessionInfo sessionInfo) {
            if (!getHandle().equals(handle)) {
                log.error("User attempting to unlock resource is not the lock owner");
                return false;
            }
            
            if (log.isDebugEnabled())
                log.debug("There are " + getCount() + " non-exclusive locks remaining.. decrementing by 1");

            boolean decrementLock = decrementLock(sessionInfo);
            if (decrementLock && log.isDebugEnabled())
                log.debug("All non-exclusive locks for " + getResource().getWebFolderPath() + " have been removed");
            return decrementLock;
        }

        public boolean isLockOwner(SessionInfo sessionInfo) {
            for (SessionInfo session : sessions_) {
                if (!session.getUser().equals(sessionInfo.getUser()))
                    return false;
            }
            return true;
        }
        
        public Collection<SessionInfo> getLockOwners() {
            return getSessions();
        }

        @Override
        public String toString() {
            StringBuffer buffer = new StringBuffer();
            buffer.append("NonExclusiveLock@");
            buffer.append("[").append("Resource='").append(resource_.toString()).append("', ");
            buffer.append("Sessions='").append(getCount()).append("', ");
            buffer.append("Handle='").append(getHandle()).append("']");
            return buffer.toString();
        }
    }

    private interface Lock {
        /**
         * The resource associated with the lock.
         * @return the resource
         */
        VFSResource getResource();

        /**
         * Removes the lock from the file.
         * @param handle of the file
         * @param sessionInfo associated with the lock
         * @return true if the lock can be removed
         */
        boolean removeLock(String handle, SessionInfo sessionInfo);

        /**
         * Verifies if this lock is owned by the supplied Session.
         * @param sessionInfo
         * @return true if this session owns the lock.
         */
        boolean isLockOwner(SessionInfo sessionInfo);
        
        /**
         * @return the owners of the lock
         */
        Collection<SessionInfo> getLockOwners ();
    }
}