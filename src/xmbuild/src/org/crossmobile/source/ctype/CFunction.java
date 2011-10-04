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
import org.crossmobile.source.utils.StringUtils;

public class CFunction extends CProcedural {

    private final CType result;
    private final List<CArgument> params;

    public CFunction(CType result, String name, List<CArgument> params, String original, String filename) {
        super(name, original, filename);
        this.result = result;
        this.params = params;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("\n\tpublic abstract ").append(result.toString()).append(" ").append(getName()).append("(");
        if (params.size() > 0) {
            for (CArgument arg : params)
                out.append(arg.toString()).append(", ");
            out.delete(out.length() - 2, out.length());
        }
        out.append(");\n");
        return out.toString();
    }

    public static void create(CLibrary parent, String entry) {
        String original = entry;
        entry = entry.trim();

        if (entry.endsWith("}")) {
            entry = entry.substring(0, StringUtils.matchFromEnd(entry, '{', '}')).trim();
            original = entry + ";";
        } else
            entry = entry.substring(0, entry.length() - 1).trim();

        int begin = StringUtils.matchFromEnd(entry, '(', ')');
        String prefix = entry.substring(0, begin).trim();
        String args = entry.substring(begin + 1, entry.length() - 1).trim();
        int last = StringUtils.findLastWord(prefix);
        String type = prefix.substring(0, last);
        parent.addCFunction(new CFunction(
                new CType(type),
                prefix.substring(last),
                CArgument.getFunctionArgments(args),
                original,
                parent.getCurrentFile()));
    }
}
