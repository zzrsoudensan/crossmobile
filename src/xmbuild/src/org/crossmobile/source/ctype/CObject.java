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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.crossmobile.source.guru.Advisor;
import org.crossmobile.source.guru.Oracle;
import org.crossmobile.source.guru.Reporter;
import org.crossmobile.source.utils.FinalizableObject;
import org.crossmobile.source.utils.WritableObject;

public class CObject implements WritableObject, FinalizableObject {

    private final CLibrary library;
    private final String name;
    private final boolean isProtocol;
    private boolean hasOptionalMethod = false;
    private List<CConstructor> constructors = new ArrayList<CConstructor>();
    private List<CMethod> methods = new ArrayList<CMethod>();
    private List<CProperty> properties = new ArrayList<CProperty>();
    private final int genericsCount;
    private boolean hasConstructorEnums = false;
    private boolean hasStaticMethods = false;
    private boolean hasInstanceMethods = false;
    private String superclass = null;
    private Set<String> interfaces = new HashSet<String>();

    public CObject(CLibrary library, String name, boolean isProtocol) {
        this.library = library;
        this.name = name;
        this.isProtocol = isProtocol;
        genericsCount = Advisor.genericsSupport(name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CObject))
            return false;
        return ((CObject) obj).name.equals(name);
    }

    public void addSelector(CSelector sel) {
        String signature = sel.getSignature(name);
        if (sel instanceof CConstructor) {
            CConstructor con = (CConstructor) sel;
            // Check for overloaded constructors
            for (CConstructor other : constructors)
                if (other.getSignature(name).equals(signature))
                    if (con.isOverloaded()) {
                        hasConstructorEnums = true;
                        other.appendDefinitions(con);
                        return;
                    } else {
                        Reporter.UNKNOWN_OVERRIDE.report("constructor", signature);
                        return;
                    }
            constructors.add(con);
        } else {
            CMethod meth = (CMethod) sel;
            if (meth.isStatic()) {
                hasStaticMethods = true;
                if (isProtocol)
                    Reporter.STATIC_INTERFACE.report(null, signature);
            } else
                hasInstanceMethods = true;
            methods.add(meth);
        }
    }

    public void addProperty(CProperty pro) {
        properties.add(pro);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();

        // Add package description
        out.append("package ").append(library.getPackagename()).append(";\n\n");

        String type = isProtocol ? (hasOptionalMethod ? "abstract class" : "interface") : "class";
        out.append("public ").append(type).append(" ");

        out.append(name);
        if (genericsCount > 0) {
            out.append("<");
            for (int i = 0; i < genericsCount; i++)
                out.append((char) ('A' + i)).append(",");
            out.delete(out.length() - 1, out.length());
            out.append(">");
        }
        if (superclass != null)
            out.append(" extends ").append(superclass);
        if (interfaces.size() > 0) {
            out.append(" implements ");
            for (String interf : interfaces)
                out.append(interf).append(", ");
            out.delete(out.length() - 2, out.length());
        }
        out.append(" {\n");

        if (hasConstructorEnums)
            out.append("\n\t/*\n\t * Initialization enumerations \n\t */\n");
        for (CConstructor c : constructors)
            out.append(c.getEnumAsString());

        if (hasStaticMethods)
            out.append("\n\t/*\n\t * Static methods\n\t */\n");
        for (CMethod m : methods)
            if (m.isStatic())
                out.append(m.toString());

        if (constructors.size() > 0) {
            out.append("\n\t/*\n\t * Constructors\n\t */\n");
            for (CConstructor c : constructors)
                out.append(c);
        }

        if (properties.size() > 0)
            out.append("\n\t/*\n\t * Properties\n\t */\n");
        for (CProperty p : properties)
            out.append(p.toString(isProtocol));

        if (hasInstanceMethods)
            out.append("\n\t/*\n\t * Instance methods\n\t */\n");
        for (CMethod m : methods)
            if (!m.isStatic())
                out.append(m.toString(isProtocol));

        out.append("}\n");
        return out.toString();
    }

    public int getGenericsCount() {
        return genericsCount;
    }

    public CLibrary getLibrary() {
        return library;
    }

    public void setSuperclass(String superclass) {
        this.superclass = superclass;
    }

    public void addInterface(String interf) {
        interfaces.add(interf);
    }

    @Override
    public void writeTo(Writer out) throws IOException {
        out.write(toString());
    }

    @Override
    public void finalizeStructures() {
        // Search if this is a delegate, so that the selector name aggregator will be more aggressive
        boolean isDelegate = false;
        for (String pattern : Advisor.getDelegatePatterns())
            isDelegate |= name.matches(pattern);

        Map<String, List<CMethod>> maps = new HashMap<String, List<CMethod>>();

        // Put methods in order
        for (CMethod m : methods) {
            String key = isDelegate ? m.nameParts.get(0) : m.getSignature("");  // if it is a delegate, group by first type, to minimize namespace pollution
            List<CMethod> list = maps.get(key);
            if (list == null) {
                list = new ArrayList<CMethod>();
                maps.put(key, list);
            }
            list.add(m);
        }

        // find conflicting methods
        for (List<CMethod> conflict : maps.values())    // get a list of all methods in categorized format
            if (conflict.size() > 1) {  // only affect methods with conflict

                List<List<String>> parts = new ArrayList<List<String>>();   // aggregate parameter names
                List<Boolean> statics = new ArrayList<Boolean>();
                for (CMethod meth : conflict) {
                    parts.add(meth.nameParts);
                    statics.add(meth.isStatic());
                }

                List<String> newnames = Oracle.findUniqueNames(parts, statics, isDelegate); // find new names
                String cname;
                for (int i = 0; i < conflict.size(); i++) {
                    cname = newnames.get(i);
                    if (cname == null)
                        methods.remove(conflict.get(i));
                    else
                        conflict.get(i).setCanonicalName(newnames.get(i));
                }
            }
    }
}
