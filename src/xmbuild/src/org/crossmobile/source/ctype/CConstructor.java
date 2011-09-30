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
 * along with CrossMobile; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.crossmobile.source.ctype;

import java.util.List;

public class CConstructor extends CSelector {

    private final String objname;
    private final CEnum overloadenum;

    public CConstructor(String objname, List<CArgument> arguments, List<String> nameparts, CEnum overloadenum) {
        super("", arguments, nameparts);
        this.objname = objname;
        this.overloadenum = overloadenum;
        if (overloadenum != null && overloadenum.resetsArgNames())
            for (int i = 0; i < arguments.size(); i++)
                arguments.get(i).name = "arg" + (i + 1);
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder("\n");
        out.append(getJavadoc());
        out.append("\tpublic ").append(objname).append("(").append(CArgument.fromList(arguments));
        if (overloadenum != null) {
            if (!arguments.isEmpty())
                out.append(", ");
            out.append(objname).append(".").append(overloadenum.getName()).append(" ").append(overloadenum.getName().toLowerCase());
        }
        out.append(") {}\n");
        return out.toString();
    }

    public boolean isOverloaded() {
        return overloadenum != null;
    }

    public String getEnumAsString() {
        return overloadenum == null ? "" : overloadenum.toString();
    }
}
