/* Copyright (c) 2011 by crossmobile.org
 *
 * CrossMobile is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2.
 *
 * CrossMobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Jubler; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.xmlvm.iphone;

import java.util.Map;

public class NSError extends NSObject {

    private String domain;
    private int code;
    private Map<Object, Object> userInfo;

    public NSError(String domain, int code, Map<Object, Object> userInfo) {
        this.domain = domain;
        this.code = code;
        this.userInfo = userInfo;
    }

    public static NSError error(String domain, int code, Map<Object, Object> userInfo) {
        return new NSError(domain, code, userInfo);
    }

    public String domain() {
        return domain;
    }

    public int code() {
        return code;
    }

    public Map<Object, Object> userInfo() {
        return userInfo;
    }

    public String description() {
        return "Error: <" + code + "> <" + domain + ">";
    }

    @Override
    public String toString() {
        return description();
    }
}
