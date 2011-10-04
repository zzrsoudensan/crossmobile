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

public class CStruct extends CProcedural {

    public CStruct(String name, String original, String filename) {
        super(name, original, filename);
    }

    public static void create(CLibrary parent, boolean isTypedef, String entry) {
        String original = entry;
        if (entry.startsWith("typedef"))
            entry = entry.substring(7).trim();
        if (entry.startsWith("extern"))
            entry = entry.substring(7).trim();
        if (entry.startsWith("struct"))
            entry = entry.substring(6).trim();
        if (entry.charAt(entry.length() - 1) == ';')
            entry = entry.substring(0, entry.length() - 1).trim();

        String corename = null;
        if (entry.indexOf('{') < 0)
            if (isTypedef) {
                int namepos = StringUtils.findLastWord(entry);
                corename = CType.registerTypedef(entry.substring(0, namepos).trim(), entry.substring(namepos).trim());
            } else if (StringUtils.findFirstWord(entry) == entry.length())
                return;
            else
                throw new RuntimeException("Unknown struct: " + original);
        else {
            int begin = StringUtils.findFirstWord(entry);
            int end = StringUtils.findLastWord(entry);

            if (begin >= 0 && end >= 0)
                corename = CType.registerTypedef(entry.substring(end), entry.substring(0, begin));
            else if (begin >= 0)
                corename = entry.substring(0, begin);
            else if (end >= 0)
                corename = entry.substring(end);
            else
                throw new RuntimeException("struct without name: " + original);
        }
        parent.getObject(corename, false);
    }
}
