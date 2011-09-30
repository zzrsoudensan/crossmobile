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

package org.crossmobile.source.guru;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;
import org.crossmobile.source.utils.WritableObject;

public enum Reporter implements WritableObject {

    RESIDUE_WHILE_PARSING,
    ARGUMENT_PARSING,
    MISMATCH_ID_RESOLVER,
    STATIC_INTERFACE,
    UNKNOWN_OVERRIDE,
    UNKNOWN_ID,
    ADVISOR_LOADING_ERROR,
    INHERITANCE,
    FUNCTION_POINTER,
    VARARGS_MISSING_COMMA,
    PROPERTY_ERROR,
    PROCEDURAL_PROBLEM,
    GROUPING_ERROR;
    //
    private static String file;
    private static String object;
    private final LinkedHashMap<Tuplet, List<Tuplet>> notepad = new LinkedHashMap<Tuplet, List<Tuplet>>();

    @Override
    public void writeTo(Writer out) throws IOException {
        if (notepad.isEmpty())
            return;
        for (Tuplet ctx : notepad.keySet()) {
            out.append("<context file=\"").append(ctx.item).append("\"");
            if (ctx.value != null)
                out.append(" object=\"").append(ctx.value).append("\"");
            out.append(">\n");
            for (Tuplet item : notepad.get(ctx)) {
                out.append("\t<item");
                if (item.item != null && !item.item.equals(""))
                    out.append(" info=\"").append(item.item).append("\"");
                out.append(">").append(item.value).append("</item>\n");
            }
            out.append("</context>\n");
        }
    }

    public static void setFile(String file) {
        Reporter.file = file;
    }

    public static void setObject(String object) {
        Reporter.object = object;
    }

    public void report(String info, String value) {
        value = value.trim();
        if (info != null)
            info = info.trim();
        if (value.trim().length() == 0)
            return;
        Tuplet context = new Tuplet(file, object);
        List<Tuplet> block = notepad.get(context);
        if (block == null) {
            block = new ArrayList<Tuplet>();
            notepad.put(context, block);
        }
        block.add(new Tuplet(info, value));
    }

    public static void addResidue(String info, String residue) {
        StringBuilder out = new StringBuilder();
        StringTokenizer tk = new StringTokenizer(residue, ";", false);
        while (tk.hasMoreTokens()) {
            String token = tk.nextToken().trim();
            if (token.length() > 0)
                out.append(token.trim()).append(";\n");
        }
        RESIDUE_WHILE_PARSING.report(info, out.toString());
    }

    private static class Tuplet {

        String item;
        String value;

        public Tuplet(String item, String value) {
            this.item = item;
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final Tuplet other = (Tuplet) obj;
            if ((this.item == null) ? (other.item != null) : !this.item.equals(other.item))
                return false;
            if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value))
                return false;
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 53 * hash + (this.item != null ? this.item.hashCode() : 0);
            hash = 53 * hash + (this.value != null ? this.value.hashCode() : 0);
            return hash;
        }
    }
}
