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

public class CProperty extends CAnyFunction {

    private final String name;
    private final CType type;
    private final String getter;
    private final String setter;

    public CProperty(String name, CType type, String getter, String setter) {
        this.name = name;
        this.type = type;
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean asAbstract) {
        StringBuilder out = new StringBuilder("\n");
        out.append(getJavadoc()).append("\tpublic ");
        if (asAbstract)
            out.append("abstract ");
        out.append(type).append(" ").append(getter).append("()").append(asAbstract ? ABSTRACTBODY : DUMMYBODY);
        if (setter != null) {
            out.append("\n").append(getJavadoc()).append("\tpublic ");
            if (asAbstract)
                out.append("abstract ");
            out.append("void").append(" ").append(setter);
            out.append("(").append(type).append(" ").append(name).append(")").append(asAbstract ? ABSTRACTBODY : DUMMYBODY);
        }
        return out.toString();
    }
}
