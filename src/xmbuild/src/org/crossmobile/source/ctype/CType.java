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

    private TypeID typeid;
    private String suffix;

    public CType(String name) {
        name = name.trim();

        // Failsafe for unmatched types
        if (name.isEmpty())
            Reporter.ARGUMENT_PARSING.report(null, "empty name");

        // Handle function pointer as type
        if (isFunctionPointer(name, "parameter"))
            name = "Object";

        // Replace "Ref" with *
        if (name.endsWith("Ref"))
            name = name.replaceFirst("Ref$", "*");

        /*  Resolve pointers */
        // Triple pointer not supported
        if (name.contains("***"))
            throw new RuntimeException("Unsupported operation: ***");
        // Double pointer is back reference
        boolean doublepointer = name.contains("**");
        if (doublepointer)
            name = name.replaceAll("\\*\\*", "");
        // Single pointer
        boolean pointer = name.contains("*");
        if (pointer)
            name = name.replaceAll("\\*", "");

        boolean varargs = name.contains("...");
        if (varargs)
            name = name.replace("...", "");

        // Remove unused identifiers
        name = name.replaceAll("struct", "").
                replaceAll("const", "").
                replaceAll("unsigned", "").
                replaceAll("extern", "").
                replaceAll("inout\\s", "").
                replaceAll("out\\s", "").
                replaceAll("in\\s", "").
                replaceAll("oneway\\s", "").
                replaceAll("__strong\\s", "").
                replaceAll("\\s\\s", " ").trim();
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
            Reporter.ARGUMENT_PARSING.report("type contains spaces", name);
            name = "Object";
        }
        name = name.replaceAll(" ", "");

        // Deal with C strings
        if (pointer && name.equals("char"))
            name = "String";

        // Deal with void* pointer
        if (pointer && name.equals("void"))
            name = "byte";

        // Get type
        typeid = TypeID.get(name);

        // Fix pointer status
        boolean isNative = Advisor.isNativeType(name);
        if (doublepointer)
            suffix = isNative ? "[][]" : "Ptr";
        else if (pointer && isNative)
            suffix = "[]";
        else
            suffix = "";
        if (varargs)
            suffix += "...";
    }

    @Override
    public String toString() {
        return typeid.name + suffix;
    }

    private static class TypeID {

        private final static Map<String, TypeID> types = new HashMap<String, TypeID>();
        private String name;

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
    }

    public boolean isID() {
        return typeid.name.equals("id");
    }

    // New type will be equal to nativetype
    public static void registerTypedef(String systemtype, String newtype) {
        TypeID stype = TypeID.get(systemtype);
        TypeID ntype = TypeID.get(newtype);
        String toChange = ntype.name;
        ntype.name = stype.name;
        for (TypeID t : TypeID.types.values())
            if (t.name.equals(toChange))
                t.name = stype.name;
    }

    public static boolean isFunctionPointer(String name, String context) {
        if (name.contains("^")) {
            Reporter.FUNCTION_POINTER.report(context + " is block", name);
            return true;
        } else if (name.contains("(")) {
            Reporter.FUNCTION_POINTER.report(context + " is function pointer", name);
            return true;
        }
        return false;
    }

    public static void create(CLibrary parent, String entry) {
        //    throw new UnsupportedOperationException("Not yet implemented");
    }
}
