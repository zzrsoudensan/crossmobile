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

import java.util.ArrayList;
import java.util.List;

public abstract class CAnyFunction {

    protected final static String DUMMYBODY = "{\n\t\tthrow new RuntimeException(\"Stub\");\n\t}\n";
    protected final static String ABSTRACTBODY = ";\n";
    //
    private List<String> definition = new ArrayList<String>(2);

    public String getJavadoc() {
        StringBuilder b = new StringBuilder("\t/**\n");
        for (String def : definition)
            b.append("\t * ").append(def).append("\n");
        b.append("\t */\n");
        return b.toString();
    }

    public void addDefinition(String definition) {
        this.definition.add(definition);
    }

    public void appendDefinitions(CAnyFunction other) {
        definition.addAll(other.definition);
    }
}
