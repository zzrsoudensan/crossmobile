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

package org.crossmobile.source.parsers;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.crossmobile.source.ctype.CLibrary;
import org.crossmobile.source.ctype.CObject;
import org.crossmobile.source.ctype.CProcedural;
import org.crossmobile.source.guru.Reporter;
import org.crossmobile.source.utils.StringUtils;

public class ObjectParser extends Parser<CLibrary> {

    private static final Pattern pattern = Pattern.compile("@((interface)|(protocol))\\s+?(\\w+)(.*?)@end");
    //
    private static final PropertyParser propp = new PropertyParser();
    private static final SelectorParser selp = new SelectorParser();

    @Override
    protected Pattern getPattern() {
        return pattern;
    }

    @Override
    protected void match(CLibrary parent, Matcher m) {
        String name = m.group(4);
        Reporter.setObject(name);

        CObject obj = parent.getObject(name, m.group(3) != null);

        String body = m.group(5);
        if (body == null)
            return;
        body = body.trim();
        if (body.length() == 0)
            return;

        // Remove categories
        if (body.charAt(0) == '(') {
            body = body.replaceFirst("\\(.*?\\)", "").trim();
            if (body.length() == 0)
                return;
        }

        // Find superclass
        if (body.charAt(0) == ':') {
            body = body.substring(1).trim();
            int next = StringUtils.findFirstWord(body);
            if (next < 0)
                Reporter.INHERITANCE.report(null, body.substring(0, 50));
            obj.setSuperclass(body.substring(0, next));
            body = body.substring(next).trim();
            if (body.length() == 0)
                return;
        }

        // Find interfaces
        if (body.charAt(0) == '<') {
            int interf = StringUtils.matchFromStart(body, '<', '>');
            if (interf >= 0) {
                StringTokenizer tk = new StringTokenizer(body.substring(1, interf), ",");
                while (tk.hasMoreTokens())
                    obj.addInterface(tk.nextToken().trim());
                body = body.substring(interf + 1).trim();
                if (body.length() == 0)
                    return;
            }
        }

        // Find & ignoring variables
        if (body.charAt(0) == '{') {
            body = body.substring(StringUtils.matchFromStart(body, '{', '}') + 1);
            if (body.length() == 0)
                return;
        }

        body = propp.parse(obj, body);
        body = selp.parse(obj, body);

        // Might have some more procedurals
        body = CProcedural.register(parent, body);

        Reporter.addResidue("object", body);

        Reporter.setObject(null);
    }
}
