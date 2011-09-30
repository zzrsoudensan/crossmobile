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
import org.crossmobile.source.ctype.CObject;
import org.crossmobile.source.ctype.CProperty;
import org.crossmobile.source.ctype.CType;
import org.crossmobile.source.guru.Reporter;
import org.crossmobile.source.utils.StringUtils;

public class PropertyParser extends Parser<CObject> {

    private static final Pattern pattern = Pattern.compile("@property(.+?);");

    @Override
    protected Pattern getPattern() {
        return pattern;
    }

    @Override
    protected void match(CObject parent, Matcher m) {
        String property = m.group(1).trim();
        String mods = null;
        String defs = property;
        if (property.charAt(0) == '(') { // Has modifiers
            int end = StringUtils.matchFromStart(property, '(', ')');
            mods = property.substring(1, end).trim();
            defs = property.substring(end + 1).trim();
        }

        if (CType.isFunctionPointer(defs, "property"))
            return;

        // Find all names
        List<String> names = new ArrayList<String>();
        List<String> getterL = new ArrayList<String>();
        List<String> setterL = new ArrayList<String>();
        defs += ",";
        boolean hasStar = false;
        while (defs.endsWith(",")) {
            defs = defs.substring(0, defs.length() - 1).trim();
            int lastWord = StringUtils.findLastWord(defs);
            names.add(defs.substring(lastWord));
            defs = defs.substring(0, lastWord).trim();
            if (defs.endsWith("*")) {
                hasStar = true;
                defs = defs.substring(0, defs.length() - 1).trim();
            } else
                hasStar = false;    // Take care ONLY for the last one - copy type of first object to all others. So we need to keep the star if it is deleted by the last entry
        }

        // Calculate getters/setters
        CType ptype = new CType(defs + (hasStar ? "*" : ""));
        for (String bname : names) {
            String camelName = bname.substring(0, 1).toUpperCase() + bname.substring(1);
            getterL.add("get" + camelName);
            setterL.add("set" + camelName);
        }

        // Find modifiers (name only for first property)
        boolean writable = true;
        if (mods != null) {
            StringTokenizer tk = new StringTokenizer(mods, ",");
            while (tk.hasMoreElements()) {
                String cmod = tk.nextToken().trim();
                if (cmod.equals("readonly"))
                    writable = false;
                else if (cmod.startsWith("getter"))
                    getterL.set(0, getParameterDefinition(cmod.substring(6)));
                else if (cmod.startsWith("setter"))
                    setterL.set(0, getParameterDefinition(cmod.substring(6)));
            }
        }

        // Add properties
        String definition = m.group();
        for (int i = 0; i < names.size(); i++) {
            CProperty prop = new CProperty(names.get(i), ptype, getterL.get(i), writable ? setterL.get(i) : null);
            prop.addDefinition(definition);
            parent.addProperty(prop);
        }
    }

    private String getParameterDefinition(String cmod) {
        cmod = cmod.trim();
        if (cmod.charAt(0) != '=')
            Reporter.PROPERTY_ERROR.report("missing = sign", cmod);
        cmod = cmod.substring(1).trim();
        return cmod;
    }
}
