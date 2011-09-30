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

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import org.crossmobile.source.guru.Oracle;
import org.crossmobile.source.guru.Reporter;
import org.crossmobile.source.utils.StringUtils;
import org.crossmobile.source.utils.WritableObject;

/**
 *
 * @author teras
 */
public class CProcedural implements WritableObject {

    private List<String> name;
    protected String original;
    protected String filename;

    public static String register(CLibrary parent, String data) {
        List<String> res = StringUtils.divideBySection(data);
        StringBuilder residue = new StringBuilder();
        for (String entry : res)
            if (entry.startsWith("(")) {
                Reporter.PROCEDURAL_PROBLEM.report("starts with parenthesis", entry);
                continue;
            } else
                parse(parent, entry, residue);
        return residue.toString();
    }

    private static void parse(CLibrary parent, String entry, StringBuilder residue) {
        String header = entry;
        if (entry.endsWith("}")) {
            int begin = StringUtils.matchFromEnd(entry, '{', '}');
            header = entry.substring(0, begin).trim();
        }
        if (header.endsWith(")"))
            CFunction.create(parent, header); // Only the header - ignore the body
        else if (header.startsWith("struct"))
            CStruct.create(parent, entry);
        else if (header.startsWith("enum"))
            CEnum.create(parent, entry);
        else if (header.startsWith("typedef"))
            CType.create(parent, entry);
        else if (header.contains(" struct"))
            CStruct.create(parent, entry);
        else if (header.contains(" enum"))
            CEnum.create(parent, entry);
        else if (header.contains(" typedef"))
            CType.create(parent, entry);
        else if (header.startsWith("extern") || header.contains(" extern"))
            CExternal.create(parent, entry);
        else
            residue.append(entry).append(";");
    }

    public CProcedural(String name, String original, String filename) {
        this.name = Oracle.canonical(name);
        this.original = original;
        this.filename = filename;
    }

    @Override
    public void writeTo(Writer out) throws IOException {
        out.write(toString());
    }

    public String getName() {
        return name.get(0);
    }
}
