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
import org.crossmobile.source.guru.Reporter;
import org.crossmobile.source.utils.StringUtils;

public class CEnum extends CProcedural {

    protected final List<String> values;
    private final boolean resetArgNames;

    public CEnum(String name, List<String> values, String original, String filename, boolean resetArgNames) {
        super(name, original, filename);
        this.values = values;
        if (values.isEmpty())
            throw new ArrayIndexOutOfBoundsException("Enumaration can not have an empty set");
        this.resetArgNames = resetArgNames;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("\n\tpublic static enum ");
        b.append(getName()).append(" {\n\t\t");
        for (String value : values)
            b.append(value).append(", ");
        b.delete(b.length() - 2, b.length());
        b.append(";\n\t}\n");
        return b.toString();
    }

    public boolean resetsArgNames() {
        return resetArgNames;
    }

    public static void create(CLibrary parent, String entry) {
        String orig = entry;
        if (entry.startsWith("typedef"))
            entry = entry.substring(7).trim();
        if (entry.startsWith("extern"))
            entry = entry.substring(7).trim();
        if (!entry.startsWith("enum"))
            throw new RuntimeException(entry);
        entry = entry.substring(4).trim();
        if (entry.endsWith(";"))
            entry = entry.substring(0, entry.length() - 1).trim();



        // Find begin enum name
        String firstname = null;
        int beginwordpos = StringUtils.findFirstWord(entry);
        if (beginwordpos >= 0) {
            firstname = entry.substring(0, beginwordpos);
            entry = entry.substring(beginwordpos).trim();
        }
        if (!entry.startsWith("{"))
            throw new RuntimeException(entry);

        int upto = StringUtils.matchFromStart(entry, '{', '}');
        if (upto < 0)
            return;
        String lastname = (upto + 1) < entry.length() ? entry.substring(upto, entry.length()) : null;
        if (firstname == null && lastname == null)
            // practically a typedef
            return;
        System.out.println(upto + (firstname == null ? "" : "firstname=" + firstname) + (lastname == null ? "" : (firstname == null ? "" : " ") + lastname));

        Reporter.INHERITANCE.report(null, entry);
    }
}
