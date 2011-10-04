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

import org.crossmobile.source.utils.StringUtils;

public class CExternal extends CProcedural {

    private final CType type;

    public CExternal(CType type, String name, String original, String filename) {
        super(name, original, filename);
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CExternal))
            return false;
        CExternal other = (CExternal) obj;
        return getName().equals(other.getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public String toString() {
        return "\n\t/**\n\t * " + original + "\n\t * " + filename + "\n\t */\n\t" + type.toString() + " " + getName() + " = ...;\n";
    }

    public static void create(CLibrary parent, boolean istypedef, String entry) {
        String original = entry;
        entry = entry.trim();
        if (entry.contains("typedef"))
            entry = entry.replaceAll("typedef", "").replaceAll("  ", " ").trim();
        if (entry.charAt(entry.length() - 1) == ';')
            entry = entry.substring(0, entry.length() - 1).trim();
        if (entry.length() == 0)
            return;
        int last = StringUtils.findLastWord(entry);
        String name = entry.substring(last).trim();
        String def = entry.substring(0, last).trim();
        if (istypedef) {
            if (def.endsWith("Ref"))
                parent.getObject(new CType(name).getProcessedName(), false).setSuperclass(def.substring(0, def.length() - 3));
            else
                CType.registerTypedef(def, name);
            return;
        } else
            parent.addCExternal(new CExternal(new CType(def), name, original, parent.getCurrentFile()));
    }
}
