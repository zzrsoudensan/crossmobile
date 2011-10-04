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

import java.util.HashMap;
import java.util.Map;
import org.crossmobile.source.guru.Advisor;
import org.crossmobile.source.guru.Reporter;
import org.crossmobile.source.utils.StringUtils;

public class CType {

    private static Map<String, String> typedefs = new HashMap<String, String>();
    //
    private final TypeID typeid;
    private final String processed;
    private boolean varargs = false;
    private int reference = 0;

    public CType(String name) {
        // Remove unused identifiers
        String original = name;
        name = name.replaceAll("\\*\\s*const", "").
                replaceAll("const\\s", "").
                replaceAll("struct\\s", "").
                replaceAll("inline\\s", "").
                replaceAll("extern\\s", "").
                replaceAll("inout\\s", "").
                replaceAll("out\\s", "").
                replaceAll("in\\s", "").
                replaceAll("oneway\\s", "").
                replaceAll("typedef\\s", "").
                replaceAll("__strong\\s", "").trim();

        // Failsafe for unmatched types
        if (name.isEmpty())
            Reporter.ARGUMENT_PARSING.report(null, "empty name");

        // Handle function pointer as type
        if (isFunctionPointer(name, "parameter"))
            name = "Object";

        // Beautify Ref pointers
        if (name.endsWith("Ref"))
            name = name.substring(0, name.length() - 3);

        /*  Resolve pointers */
        reference = StringUtils.count(name, '*');
        if (reference > 0)
            name = name.replaceAll("\\*", "");
        int openBracket = name.indexOf('[');
        if (openBracket >= 0) {
            int closeBracket = name.lastIndexOf(']');
            reference += StringUtils.count(name, '[');
            name = name.substring(0, openBracket) + name.substring(closeBracket + 1, name.length());
        }

        varargs = name.contains("...");
        if (varargs)
            name = name.replace("...", "");

        // Fix naming conventions
        if (name.contains("signed")) {
            name = name.replaceAll("unsigned", "").replaceAll("signed", "").replaceAll("\\s\\s", " ").trim();
            if (name.isEmpty())
                name = "int";
        } else
            name = name.replaceAll("\\s\\s", " ").trim();
        if (name.equals("short int"))
            name = "short";
        if (name.equals("long int"))
            name = "long";
        if (name.equals("long long"))
            name = "long";
        if (name.equals("long long int"))
            name = "long";
        if (name.equals("long double"))
            name = "double";

        while (name.startsWith("_")) {
            String shorter = name.substring(1);
            registerTypedef(shorter, name);
            name = shorter;
        }
        if (name.toLowerCase().startsWith("opaque")) {
            String shorter = name.substring(6);
            registerTypedef(shorter, name);
            name = shorter;
        }

        // Replace protocol with Object name
        if (name.endsWith(">"))
            if (name.startsWith("id")) {
                int prot = StringUtils.matchFromEnd(name, '<', '>');
                name = name.substring(prot + 1, name.length() - 1).trim();
                if (name.startsWith(","))
                    throw new RuntimeException("Protocol type starts with comma: " + name);
                if (name.contains(","))
                    name = name.substring(0, name.indexOf(','));
            } else {
                int where = StringUtils.findFirstWord(name);
                name = name.substring(0, where);
            }

        if (name.contains(" ")) {
            Reporter.ARGUMENT_PARSING.report("type \'" + name + "\' contains spaces", original);
            name = "Object";
            name = name.replaceAll(" ", "");
        }

        // Deal with void* pointer
        if (reference > 0 && name.equals("void"))
            name = "byte";

        // Get type
        typeid = TypeID.get(name);
        processed = name;
    }

    @Override
    public String toString() {
        int level = reference + typeid.reference;
        if (level > 0 && !Advisor.isNativeType(typeid.name))
            level--;
        StringBuilder b = new StringBuilder(typeid.name);
        for (; level > 0; level--)
            b.append("[]");
        if (varargs)
            b.append("...");
        return b.toString();
    }

    public String getProcessedName() {
        return processed;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CType))
            return false;
        return processed.equals(((CType) obj).processed);
    }

    @Override
    public int hashCode() {
        return processed.hashCode();
    }

    private static class TypeID {

        private final static Map<String, TypeID> types = new HashMap<String, TypeID>();
        //
        private String name;
        private int reference = 0;

        private TypeID(String name) {
            this.name = name;
        }

        private static TypeID get(String name) {
            TypeID res = types.get(name);
            if (res == null) {
                res = new TypeID(name);
                types.put(name, res);
            }
            return res;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public boolean isID() {
        return typeid.name.equals("id");
    }

    // New type will be equal to nativetype
    public static String registerTypedef(String systemtype, String newtype) {
        if ((systemtype.toLowerCase().startsWith("opaque") && !newtype.toLowerCase().startsWith("opaque"))) {
            String swap = systemtype;
            systemtype = newtype;
            newtype = swap;
        }
        if (!isFinalizing)
            typedefs.put(newtype, systemtype);
        return systemtype;
    }
    private static boolean isFinalizing = false;

    public static void finalizeTypedefs() {
        isFinalizing = true;
        for (String key : typedefs.keySet()) {
            CType stype = new CType(typedefs.get(key));
            String systemname = stype.typeid.name;
            int addedRef = stype.reference + stype.typeid.reference;
            TypeID ntypeid = new CType(key).typeid;
            String toChange = ntypeid.name;
            for (TypeID t : TypeID.types.values())
                if (t.name.equals(toChange)) {
                    t.name = systemname;
                    t.reference += addedRef;
                }
        }
        isFinalizing = true;
    }

    public static boolean isFunctionPointer(String name, String context) {
        int from = name.indexOf('^');
        if (from >= 0) {
            Reporter.FUNCTION_POINTER.report(context + " is block", name);
            name = name.substring(from + 1);
            int to = StringUtils.findFirstWord(name);
            if (to > 0)
                registerTypedef("Object", name.substring(0, to));
            return true;
        }

        from = name.indexOf('(');
        if (from >= 0) {
            name = name.substring(from);
            int to = StringUtils.matchFromStart(name, '(', ')');
            name = name.substring(to + 1).trim();
            if (name.charAt(0) == '(') {
                Reporter.FUNCTION_POINTER.report(context + " is function pointer", name);
                return true;
            }
        }

        return false;
    }
}
