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

package org.eclipse.jetty.security.authentication;

import java.security.Principal;

import javax.security.auth.Subject;


/**
 * This is similar to the jaspi PasswordValidationCallback but includes user
 * principal and group info as well.
 *
 * @version $Rev: 4792 $ $Date: 2009-03-18 22:55:52 +0100 (Wed, 18 Mar 2009) $
 */
public interface LoginCallback
{
    public Subject getSubject();

    public String getUserName();

    public Object getCredential();

    public boolean isSuccess();

    public void setSuccess(boolean success);

    public Principal getUserPrincipal();

    public void setUserPrincipal(Principal userPrincipal);

    public String[] getRoles();

    public void setRoles(String[] roles);

    public void clearPassword();


}
