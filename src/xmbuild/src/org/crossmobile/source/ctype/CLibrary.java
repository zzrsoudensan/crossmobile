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
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.crossmobile.source.guru.Advisor;
import org.crossmobile.source.utils.FileUtils;
import org.crossmobile.source.guru.Reporter;
import org.crossmobile.source.utils.FieldHolder;

public class CLibrary implements FieldHolder {

    private final Map<String, CObject> objects = new HashMap<String, CObject>();
    private final Set<CEnum> enums = new LinkedHashSet<CEnum>();
    private final Set<CStruct> structs = new LinkedHashSet<CStruct>();
    private final Set<CFunction> functions = new LinkedHashSet<CFunction>();
    private final Set<CArgument> externals = new LinkedHashSet<CArgument>();
    private String currentFile;
    private final String packagename;

    public CLibrary(String packagename) {
        this.packagename = packagename;
    }

    public synchronized void addFile(String filename) {
        String data = FileUtils.getFile(filename);
        if (data == null)
            return;
        this.currentFile = filename;
        Reporter.setFile(filename);
        CAny.parse(this, Advisor.convertData(data));
        this.currentFile = null;
    }

    public CObject getObject(String name) {
        return getObject(name, false, false);
    }

    public CObject getInterface(String name) {
        return getObject(name, true, false);
    }

    public CStruct getStruct(String name) {
        return (CStruct) getObject(name, false, true);
    }

    private CObject getObject(String name, boolean isProtocol, boolean isStruct) {
        CObject obj = objects.get(name);
        if (obj != null)
            return obj;

        name = new CType(name).getProcessedName();
        obj = isStruct ? new CStruct(this, name, isProtocol) : new CObject(this, name, isProtocol);
        objects.put(name, obj);
        return obj;
    }

    public Set<CEnum> getEnums() {
        return enums;
    }

    public Set<CArgument> getExternals() {
        return externals;
    }

    public Set<CFunction> getFunctions() {
        return functions;
    }

    public Set<CStruct> getStructs() {
        return structs;
    }

    public String getCurrentFile() {
        return currentFile;
    }

    public String getPackagename() {
        return packagename;
    }

    public void finalizeLibrary() {
        Reporter.setFile(null);
        getObject("CFType");
        getObject("CFURL");
        getObject("SEL");
        getObject("NSZone");
        getObject("Protocol");
        getObject("NSComparator");
        getObject("IMP");
        getObject("NSURLHandle");
        getObject("NSURLHandleClient");
        getObject("NSHost");
        getObject("NSPortMessage");
        getObject("NSConnection");
        getObject("SCNetworkInterface");
        getObject("CMFormatDescription");

        CType.finalizeTypedefs();
        for (CObject o : objects.values()) {
            Reporter.setObject(o);
            o.finalizeObject();
        }
    }

    public Iterable<CObject> getObjects() {
        return objects.values();
    }

    void addCFunction(CFunction cFunction) {
        functions.add(cFunction);
    }

    @Override
    public void addCArgument(CArgument arg) {
//        System.out.println(cExternal.getName());
        externals.add(arg);
    }
}
