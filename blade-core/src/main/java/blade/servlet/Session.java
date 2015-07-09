/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package blade.servlet;

import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpSession;

/**
 * HttpSession包装
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class Session {

    private HttpSession session;

    Session(HttpSession session) {
        if (session == null) {
            throw new IllegalArgumentException("session cannot be null");
        }
        this.session = session;
    }

    public HttpSession raw() {
        return session;
    }

    @SuppressWarnings("unchecked")
    public <T> T attribute(String name) {
        return (T) session.getAttribute(name);
    }

    public void attribute(String name, Object value) {
        session.setAttribute(name, value);
    }

    public Set<String> attributes() {
        TreeSet<String> attributes = new TreeSet<String>();
        Enumeration<String> enumeration = session.getAttributeNames();
        while (enumeration.hasMoreElements()) {
            attributes.add(enumeration.nextElement());
        }
        return attributes;
    }

    public long creationTime() {
        return session.getCreationTime();
    }

    public String id() {
        return session.getId();
    }

    public long lastAccessedTime() {
        return session.getLastAccessedTime();
    }

    public int maxInactiveInterval() {
        return session.getMaxInactiveInterval();
    }

    public void maxInactiveInterval(int interval) {
        session.setMaxInactiveInterval(interval);
    }

    public void invalidate() {
        session.invalidate();
    }

    public boolean isNew() {
        return session.isNew();
    }

    public void removeAttribute(String name) {
        session.removeAttribute(name);
    }
}