//
//  ========================================================================
//  Copyright (c) 1995-2016 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.security;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.security.auth.Subject;


import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.util.PathWatcher;
import org.eclipse.jetty.util.PathWatcher.PathWatchEvent;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.security.Credential;

/**
 * PropertyUserStore
 * <p>
 * This class monitors a property file of the format mentioned below and notifies registered listeners of the changes to the the given file.
 *
 * <pre>
 *  username: password [,rolename ...]
 * </pre>
 *
 * Passwords may be clear text, obfuscated or checksummed. The class com.eclipse.Util.Password should be used to generate obfuscated passwords or password
 * checksums.
 *
 * If DIGEST Authentication is used, the password must be in a recoverable format, either plain text or OBF:.
 */
public class PropertyUserStore extends AbstractLifeCycle implements PathWatcher.Listener
{
    private static final Logger LOG = Log.getLogger(PropertyUserStore.class);

    protected Path _configPath;
    protected Resource _configResource;
    
    protected PathWatcher pathWatcher;
    protected boolean hotReload = false; // default is not to reload

    protected IdentityService _identityService = new DefaultIdentityService();
    protected boolean _firstLoad = true; // true if first load, false from that point on
    protected final List<String> _knownUsers = new ArrayList<String>();
    protected final Map<String, UserIdentity> _knownUserIdentities = new HashMap<String, UserIdentity>();
    protected List<UserListener> _listeners;

    /**
     * Get the config (as a string)
     * @return the config path as a string
     * @deprecated use {@link #getConfigPath()} instead
     */
    @Deprecated
    public String getConfig()
    {
        return _configPath.toString();
    }

    /**
     * Set the Config Path from a String reference to a file
     * @param configFile the config file
     * @deprecated use {@link #setConfigPath(String)} instead
     */
    @Deprecated
    public void setConfig(String configFile)
    {
        setConfigPath(configFile);
    }
    
    /**
     * Get the Config {@link Path} reference.
     * @return the config path
     */
    public Path getConfigPath()
    {
        return _configPath;
    }

    /**
     * Set the Config Path from a String reference to a file
     * @param configFile the config file
     */
    public void setConfigPath(String configFile)
    {
        if (configFile == null)
        {
            _configPath = null;
        }
        else
        {
            _configPath = new File(configFile).toPath();
        }
    }

    /**
     * Set the Config Path from a {@link File} reference
     * @param configFile the config file
     */
    public void setConfigPath(File configFile)
    {
        _configPath = configFile.toPath();
    }

    /**
     * Set the Config Path
     * @param configPath the config path
     */
    public void setConfigPath(Path configPath)
    {
        _configPath = configPath;
    }
    
    /* ------------------------------------------------------------ */
    public UserIdentity getUserIdentity(String userName)
    {
        return _knownUserIdentities.get(userName);
    }

    /* ------------------------------------------------------------ */
    /**
     * @return the resource associated with the configured properties file, creating it if necessary
     * @throws IOException if unable to get the resource
     */
    public Resource getConfigResource() throws IOException
    {
        if (_configResource == null)
        {
            _configResource = new PathResource(_configPath);
        }

        return _configResource;
    }
    
    /**
     * Is hot reload enabled on this user store
     * 
     * @return true if hot reload was enabled before startup
     */
    public boolean isHotReload()
    {
        return hotReload;
    }

    /**
     * Enable Hot Reload of the Property File
     * 
     * @param enable true to enable, false to disable
     */
    public void setHotReload(boolean enable)
    {
        if (isRunning())
        {
            throw new IllegalStateException("Cannot set hot reload while user store is running");
        }
        this.hotReload = enable;
    }

   
    
    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        s.append(this.getClass().getName());
        s.append("[");
        s.append("users.count=").append(this._knownUsers.size());
        s.append("identityService=").append(this._identityService);
        s.append("]");
        return s.toString();
    }

    /* ------------------------------------------------------------ */
    protected void loadUsers() throws IOException
    {
        if (_configPath == null)
            return;

        if (LOG.isDebugEnabled())
        {
            LOG.debug("Loading " + this + " from " + _configPath);
        }
        
        Properties properties = new Properties();
        if (getConfigResource().exists())
            properties.load(getConfigResource().getInputStream());
        
        Set<String> known = new HashSet<String>();

        for (Map.Entry<Object, Object> entry : properties.entrySet())
        {
            String username = ((String)entry.getKey()).trim();
            String credentials = ((String)entry.getValue()).trim();
            String roles = null;
            int c = credentials.indexOf(',');
            if (c > 0)
            {
                roles = credentials.substring(c + 1).trim();
                credentials = credentials.substring(0,c).trim();
            }

            if (username != null && username.length() > 0 && credentials != null && credentials.length() > 0)
            {
                String[] roleArray = IdentityService.NO_ROLES;
                if (roles != null && roles.length() > 0)
                {
                    roleArray = StringUtil.csvSplit(roles);
                }
                known.add(username);
                Credential credential = Credential.getCredential(credentials);

                Principal userPrincipal = new AbstractLoginService.UserPrincipal(username,credential);
                Subject subject = new Subject();
                subject.getPrincipals().add(userPrincipal);
                subject.getPrivateCredentials().add(credential);

                if (roles != null)
                {
                    for (String role : roleArray)
                    {
                        subject.getPrincipals().add(new AbstractLoginService.RolePrincipal(role));
                    }
                }

                subject.setReadOnly();

                _knownUserIdentities.put(username,_identityService.newUserIdentity(subject,userPrincipal,roleArray));
                notifyUpdate(username,credential,roleArray);
            }
        }

        synchronized (_knownUsers)
        {
            /*
             * if its not the initial load then we want to process removed users
             */
            if (!_firstLoad)
            {
                Iterator<String> users = _knownUsers.iterator();
                while (users.hasNext())
                {
                    String user = users.next();
                    if (!known.contains(user))
                    {
                        _knownUserIdentities.remove(user);
                        notifyRemove(user);
                    }
                }
            }

            /*
             * reset the tracked _users list to the known users we just processed
             */

            _knownUsers.clear();
            _knownUsers.addAll(known);

        }

        /*
         * set initial load to false as there should be no more initial loads
         */
        _firstLoad = false;
        
        if (LOG.isDebugEnabled())
        {
            LOG.debug("Loaded " + this + " from " + _configPath);
        }
    }
    
    /* ------------------------------------------------------------ */
    /**
     * Depending on the value of the refresh interval, this method will either start up a scanner thread that will monitor the properties file for changes after
     * it has initially loaded it. Otherwise the users will be loaded and there will be no active monitoring thread so changes will not be detected.
     *
     *
     * @see AbstractLifeCycle#doStart()
     */
    protected void doStart() throws Exception
    {
        super.doStart();

        loadUsers();
        if ( isHotReload() && (_configPath != null) )
        {
            this.pathWatcher = new PathWatcher();
            this.pathWatcher.watch(_configPath);
            this.pathWatcher.addListener(this);
            this.pathWatcher.setNotifyExistingOnStart(false);
            this.pathWatcher.start();
        }
       
    }
    
    @Override
    public void onPathWatchEvent(PathWatchEvent event)
    {
        try
        {
            loadUsers();
        }
        catch (IOException e)
        {
            LOG.warn(e);
        }
    }

    /* ------------------------------------------------------------ */
    /**
     * @see AbstractLifeCycle#doStop()
     */
    protected void doStop() throws Exception
    {
        super.doStop();
        if (this.pathWatcher != null)
            this.pathWatcher.stop();
        this.pathWatcher = null;
    }

    /**
     * Notifies the registered listeners of potential updates to a user
     *
     * @param username
     * @param credential
     * @param roleArray
     */
    private void notifyUpdate(String username, Credential credential, String[] roleArray)
    {
        if (_listeners != null)
        {
            for (Iterator<UserListener> i = _listeners.iterator(); i.hasNext();)
            {
                i.next().update(username,credential,roleArray);
            }
        }
    }

    /**
     * notifies the registered listeners that a user has been removed.
     *
     * @param username
     */
    private void notifyRemove(String username)
    {
        if (_listeners != null)
        {
            for (Iterator<UserListener> i = _listeners.iterator(); i.hasNext();)
            {
                i.next().remove(username);
            }
        }
    }

    /**
     * registers a listener to be notified of the contents of the property file
     * @param listener the user listener
     */
    public void registerUserListener(UserListener listener)
    {
        if (_listeners == null)
        {
            _listeners = new ArrayList<UserListener>();
        }
        _listeners.add(listener);
    }

    /**
     * UserListener
     */
    public interface UserListener
    {
        public void update(String username, Credential credential, String[] roleArray);

        public void remove(String username);
    }
}
