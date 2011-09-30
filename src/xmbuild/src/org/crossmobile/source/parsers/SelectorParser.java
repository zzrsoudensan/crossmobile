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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.crossmobile.source.ctype.CArgument;
import org.crossmobile.source.ctype.CObject;
import org.crossmobile.source.ctype.CSelector;
import org.crossmobile.source.ctype.CType;
import org.crossmobile.source.utils.StringUtils;

public class SelectorParser extends Parser<CObject> {

    private static final Pattern pattern = Pattern.compile("([-+])(.*?);");

    @Override
    protected Pattern getPattern() {
        return pattern;
    }

    @Override
    protected void match(CObject parent, Matcher m) {
        boolean isStatic = m.group(1).equals("+");
        String body = m.group(2).trim();

        String rtrn = "id";
        if (body.startsWith("(")) {
            int param = StringUtils.matchFromStart(body, '(', ')');
            rtrn = body.substring(1, param);
            body = body.substring(param + 1);
        }
        CType returnType = new CType(rtrn);

        List<CArgument> args = new ArrayList<CArgument>();
        List<String> methodParts = new ArrayList<String>();
        if (!body.contains(":"))
            // No arguments, only method name
            methodParts.add(body);
        else {
            // At least one argument: parsing
            StringTokenizer tk = new StringTokenizer(body, ":");
            methodParts.add(tk.nextToken().trim());  // Add first token as method name
            while (tk.hasMoreTokens()) {
                ArgumentResult res = CArgument.getSelectorArgument(tk.nextToken().trim());
                args.add(res.argument);
                methodParts.add(res.selectorPart);
            }
            methodParts.remove(methodParts.size() - 1); // Selectors end with argument definition, not argument name
        }
        CSelector sel = CSelector.create(parent, isStatic, returnType, methodParts, args);
        sel.addDefinition(m.group());
        parent.addSelector(sel);
    }

    public static class ArgumentResult {

        private String selectorPart;
        private CArgument argument;

        public ArgumentResult(CArgument argument, String selectorPart) {
            this.selectorPart = selectorPart;
            this.argument = argument;
        }
    }
}
