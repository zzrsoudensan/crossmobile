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
import org.crossmobile.source.parser.BlockType;
import org.crossmobile.source.parser.Stream;

public class CLibrary {

    private final Map<String, CObject> objects = new HashMap<String, CObject>();
    private final Set<CEnum> enums = new LinkedHashSet<CEnum>();
    private final Set<CStruct> structs = new LinkedHashSet<CStruct>();
    private final Set<CFunction> functions = new LinkedHashSet<CFunction>();
    private final Set<CExternal> externals = new LinkedHashSet<CExternal>();
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
        data = Advisor.convertData(data);

        CObject lastObject = null;

        Stream s = new Stream(data);
        s.consumeSpaces();
        BlockType type;
        boolean istypedef;
        boolean isRequired;
        boolean isProtocol;
        while ((type = s.peekBlockType()) != BlockType.EOF) {
            String z = s.peekBlock();
            istypedef = false;
            switch (type) {
                case OPTIONAL:
                case REQUIRED:
                    isRequired = type == BlockType.REQUIRED;
                    s.consumeBlock();
                    break;
                case PROTOCOLSTART:
                case OBJECTSTART:
                    isProtocol = type == BlockType.PROTOCOLSTART;
                    s.consumeBlock();
                    lastObject = CObject.parse(this, isProtocol, s);
                    isRequired = isProtocol;    // by default, protocol selectors are required. If it is a class, (not a protocol) it is not required (it has default implementation)
                    break;
                case PROPERTY:
                    if (lastObject == null)
                        throw new NullPointerException("Enclosing object not found!");
                    s.consumeBlock();
                    CProperty.parse(lastObject, s);
                    break;
                case SELECTOR:
                    if (lastObject == null)
                        throw new NullPointerException("Enclosing object not found!");
                    CSelector.parse(lastObject, s);
                    break;
                case OBJECTEND:
                    Reporter.setObject(null);
                    lastObject = null;
                    s.consumeBlock();
                    break;
                case TYPEDEFFUNCTION:
                    CType.isFunctionPointer(s.consumeBlock(), "typedef");
                    break;
                case FUNCTION:
                    CFunction.create(this, s.consumeBlock());
                    break;
                case TYPEDEFENUM:
                    istypedef = true;
                case ENUM:
                    CEnum.create(this, istypedef, s.consumeBlock());
                    break;
                case TYPEDEFSTRUCT:
                    istypedef = true;
                case STRUCT:
                    CStruct.create(this, istypedef, s.consumeBlock());
                    break;
                case TYPEDEFEXTERNAL:
                    istypedef = true;
                case EXTERNAL:
                    CExternal.create(this, istypedef, s.consumeBlock());
                    break;
                default:
                    Reporter.addUnknownItem(lastObject, s.consumeBlock());
            }
        }

        this.currentFile = null;
    }

    public CObject getObject(String name, boolean isProtocol) {
        CObject obj = objects.get(name);
        if (obj != null)
            return obj;

        name = new CType(name).getProcessedName();
        obj = new CObject(this, name, isProtocol);
        objects.put(name, obj);
        return obj;
    }

    public Set<CEnum> getEnums() {
        return enums;
    }

    public Set<CExternal> getExternals() {
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
        getObject("CFType", false);
        getObject("SEL", false);
        getObject("NSZone", false);
        getObject("Protocol", false);
        getObject("NSComparator", false);
        getObject("IMP", false);
        getObject("NSURLHandle", false);
        getObject("NSURLHandleClient", false);
        getObject("NSHost", false);
        getObject("NSPortMessage", false);
        getObject("NSConnection", false);
        getObject("SCNetworkInterface", false);
        getObject("CMFormatDescription", false);

        Advisor.addDefaultTypedefs();
        CType.finalizeTypedefs();
        for (CObject o : objects.values()) {
            Reporter.setObject(o.getName());
            o.finalizeObject();
        }
    }

    public Iterable<CObject> getObjects() {
        return objects.values();
    }

    void addCFunction(CFunction cFunction) {
        functions.add(cFunction);
    }

    void addCExternal(CExternal cExternal) {
        externals.add(cExternal);
    }
}
