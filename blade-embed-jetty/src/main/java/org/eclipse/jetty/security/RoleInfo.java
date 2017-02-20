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

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * RoleInfo
 * 
 * Badly named class that holds the role and user data constraint info for a
 * path/http method combination, extracted and combined from security
 * constraints.
 * 
 * @version $Rev: 4793 $ $Date: 2009-03-19 00:00:01 +0100 (Thu, 19 Mar 2009) $
 */
public class RoleInfo
{
    private boolean _isAnyAuth;
    private boolean _isAnyRole;
    private boolean _checked;
    private boolean _forbidden;
    private UserDataConstraint _userDataConstraint;

    /**
     * List of permitted roles
     */
    private final Set<String> _roles = new CopyOnWriteArraySet<String>();

    public RoleInfo()
    {    
    }
    
    public boolean isChecked()
    {
        return _checked;
    }

    public void setChecked(boolean checked)
    {
        this._checked = checked;
        if (!checked)
        {
            _forbidden=false;
            _roles.clear();
            _isAnyRole=false;
            _isAnyAuth=false;
        }
    }

    public boolean isForbidden()
    {
        return _forbidden;
    }

    public void setForbidden(boolean forbidden)
    {
        this._forbidden = forbidden;
        if (forbidden)
        {
            _checked = true;
            _userDataConstraint = null;
            _isAnyRole=false;
            _isAnyAuth=false;
            _roles.clear();
        }
    }

    public boolean isAnyRole()
    {
        return _isAnyRole;
    }

    public void setAnyRole(boolean anyRole)
    {
        this._isAnyRole=anyRole;
        if (anyRole)
            _checked = true;
    }
    
    public boolean isAnyAuth ()
    {
        return _isAnyAuth;
    }
    
    public void setAnyAuth(boolean anyAuth)
    {
        this._isAnyAuth=anyAuth;
        if (anyAuth)
            _checked = true;
    }

    public UserDataConstraint getUserDataConstraint()
    {
        return _userDataConstraint;
    }

    public void setUserDataConstraint(UserDataConstraint userDataConstraint)
    {
        if (userDataConstraint == null) throw new NullPointerException("Null UserDataConstraint");
        if (this._userDataConstraint == null)
        {
           
            this._userDataConstraint = userDataConstraint;
        }
        else
        {
            this._userDataConstraint = this._userDataConstraint.combine(userDataConstraint);
        }
    }

    public Set<String> getRoles()
    {
        return _roles;
    }
    
    public void addRole(String role)
    {
        _roles.add(role);
    }

    public void combine(RoleInfo other)
    {
        if (other._forbidden)
            setForbidden(true);
        else if (!other._checked) // TODO is this the right way around???
            setChecked(true);
        else if (other._isAnyRole)
            setAnyRole(true);
        else if (other._isAnyAuth)
            setAnyAuth(true);
        else if (!_isAnyRole)
        {
            for (String r : other._roles)
                _roles.add(r);
        }
        
        setUserDataConstraint(other._userDataConstraint);
    }
    
    @Override
    public String toString()
    {
        return "{RoleInfo"+(_forbidden?",F":"")+(_checked?",C":"")+(_isAnyRole?",*":_roles)+(_userDataConstraint!=null?","+_userDataConstraint:"")+"}";
    }
}
