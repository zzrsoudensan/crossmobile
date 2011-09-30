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
import org.crossmobile.source.guru.Advisor;
import org.crossmobile.source.guru.Reporter;

public abstract class CSelector extends CAnyFunction {

    protected final String name;
    protected final List<CArgument> arguments;
    protected final List<String> nameParts;

    public static CSelector create(CObject parent, boolean isStatic, CType returnType, List<String> methodParts, List<CArgument> args) {
        String methodName = methodParts.get(0);
        boolean constructor = methodName.startsWith("init") && !methodName.equals("initialize");
        if (constructor)
            methodName = "";

        String signature = getSignature(parent.getName(), methodName, args);

        returnType = fixGenericsConflict(returnType, args, signature);
        fixArgumentsIDConflict(parent, args, signature);
        if (constructor)
            return new CConstructor(parent.getName(), args, methodParts, Advisor.constructorOverload(signature));
        else {
            returnType = fixReturnIDConflict(parent, isStatic, returnType, methodName, signature);
            return new CMethod(isStatic, returnType, methodName, args, methodParts);
        }
    }

    private static CType fixGenericsConflict(CType returnType, List<CArgument> arg, String signature) {

        return returnType;
    }

    private static CType fixReturnIDConflict(CObject parent, boolean isStatic, CType returnType, String methodName, String signature) {
        // Check if the return value is ID, so that it needs to be properly reported            
        if (returnType.isID()) {
            // Unknown method, first consult conflict solver
            String newtype = Advisor.returnID(signature);
            if (newtype != null)
                returnType = new CType(newtype);
            else {
                // Check if this is a convenient function
                String simplename = parent.getName().toLowerCase().substring(2);
                if (simplename.startsWith("mutable"))
                    simplename = simplename.substring(7);
                if (isStatic && methodName.toLowerCase().startsWith(simplename))    // convenient method
                    returnType = new CType(parent.getName());
                else // Use generics as a last resort 
                if (parent.getGenericsCount() > 0)
                    returnType = new CType("A");
                else {
                    returnType = new CType("Object");
                    Reporter.UNKNOWN_ID.report("return", signature);
                }
            }
        }
        return returnType;
    }

    private static void fixArgumentsIDConflict(CObject parent, List<CArgument> args, String signature) {
        int conflicts = 0;
        for (CArgument arg : args)
            if (arg.type.isID())
                conflicts++;
        if (conflicts == 0)
            return;

        List<String> newtypes = Advisor.argumentID(signature);
        if (newtypes != null) {
            if (newtypes.size() != conflicts)
                Reporter.MISMATCH_ID_RESOLVER.report("want=" + conflicts + " provided=" + newtypes.size(), signature);
            int loc = 0;
            for (CArgument arg : args)
                if (arg.type.isID())
                    arg.type = new CType(newtypes.get(loc++)); // Might throw array index out of bounds, if ID arguments and 
        } else if (parent.getGenericsCount() > 1)  // Use generics as a last resort
            for (CArgument arg : args)
                if (arg.type.isID())
                    arg.type = new CType("A");
                else {
                    arg.type = new CType("Object");
                    Reporter.UNKNOWN_ID.report("given argument", signature);
                }
    }

    public CSelector(String name, List<CArgument> arguments, List<String> nameParts) {
        this.name = name;
        this.arguments = arguments;
        this.nameParts = nameParts;
    }

    public final String getSignature(String objectname) {
        return getSignature(objectname, name, arguments);
    }

    private static String getSignature(String parent, String name, List<CArgument> arguments) {
        StringBuilder out = new StringBuilder(parent);
        out.append(":").append(name).append(":");
        for (CArgument arg : arguments)
            out.append(arg.type);
        return out.toString();
    }
}
