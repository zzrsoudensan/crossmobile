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

import org.crossmobile.source.utils.FieldHolder;
import org.crossmobile.source.parser.Stream;
import org.crossmobile.source.utils.StringUtils;

public class CStruct extends CObject implements FieldHolder {

    public CStruct(CLibrary library, String name, boolean isProtocol) {
        super(library, name, false);
    }

    public static void create(CLibrary parent, boolean isTypedef, String entry, boolean isInternal) {
        entry = entry.trim();
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
                CStruct cs = parent.getStruct(corename);
            } else if (StringUtils.findFirstWord(entry) == entry.length())
                return;
            else
                throw new RuntimeException("Unknown struct: " + original);
        else {
            int begin = StringUtils.findFirstWord(entry);
            int end = entry.lastIndexOf('}');
            if (end < 0)
                throw new RuntimeException("Defined struct does not end with the '}' character");

            String lastpart = entry.substring(end + 1).trim();
            entry = entry.substring(0, end);
            if (lastpart.isEmpty())
                end = -1;

            if (begin >= 0 && end >= 0)
                corename = CType.registerTypedef(lastpart, entry.substring(0, begin));
            else if (begin >= 0)
                corename = entry.substring(0, begin);
            else if (end >= 0)
                corename = lastpart;
            else
                throw new RuntimeException("struct without name: " + original);

            if (begin < 0)
                begin = 0;
            if (end < 0)
                end = entry.length();
            entry = entry.substring(begin, end).trim();
            if (entry.charAt(0) == '{')
                entry = entry.substring(1);
            if (entry.charAt(entry.length() - 1) == '}')
                entry = entry.substring(0, entry.length() - 1);
            entry = entry.trim();

            CStruct cs = parent.getStruct(corename);
            CAny.parse(parent, new Stream(entry), cs);
        }
    }

    public void foundEnum(CLibrary lib, boolean typedef, String block) {
        throw new RuntimeException("Not supported yet");
    }

    public void foundStruct(CLibrary lib, boolean typedef, String block) {
        System.out.println("?? " + block);
    }

    public void foundArg(CLibrary lib, boolean typedef, String block) {
        CArgument.create(lib, this, typedef, block);
    }

    @Override
    public void addCArgument(CArgument arg) {
        addVariable(arg, false);
    }
}
