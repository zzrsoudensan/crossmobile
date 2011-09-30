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

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import org.crossmobile.source.guru.Advisor;
import org.crossmobile.source.parsers.ObjectParser;
import org.crossmobile.source.utils.FileUtils;
import org.crossmobile.source.guru.Reporter;
import org.crossmobile.source.utils.FinalizableObject;
import org.crossmobile.source.utils.WritableObject;

public class CLibrary implements WritableObject, FinalizableObject {

    private final static ObjectParser parser = new ObjectParser();
    //
    private final Map<String, CObject> objects = new HashMap<String, CObject>();
    private final LinkedHashSet<CProcedural> procedurals = new LinkedHashSet<CProcedural>();
    private String currentFile;
    private final String packagename;

    public CLibrary(String packagename) {
        this.packagename = packagename;
    }

    public synchronized void parseFile(String filename) {
        String data = FileUtils.getFile(filename);
        if (data == null)
            return;
        this.currentFile = filename;
        Reporter.setFile(filename);

        data = Advisor.convertData(data);
        data = parser.parse(this, data);
        data = CProcedural.register(this, data);
        Reporter.addResidue("file", data);
        this.currentFile = null;
    }

    public CObject getObject(String name, boolean isProtocol) {
        CObject obj = objects.get(name);
        if (obj != null)
            return obj;
        obj = new CObject(this, name, isProtocol);
        objects.put(name, obj);
        return obj;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (CObject o : objects.values())
            out.append(o.toString()).append("\n");
        return out.toString();
    }

    public void addProcedural(CProcedural ext) {
        procedurals.add(ext);
    }

    public void output(String outdir) {
        File out = new File(outdir);
        FileUtils.delete(out);
        for (CObject o : objects.values())
            FileUtils.putFile(new File(out, "src" + File.separator + o.getName() + ".java"), o);

        ctype = CEnum.class;
        FileUtils.putFile(new File(out, "enumerations.java"), this);
        ctype = CExternal.class;
        FileUtils.putFile(new File(out, "externals.java"), this);
        ctype = CFunction.class;
        FileUtils.putFile(new File(out, "functions.java"), this);
        ctype = CStruct.class;
        FileUtils.putFile(new File(out, "structs.java"), this);

        for (Reporter r : Reporter.values())
            FileUtils.putFile(new File(out, "report" + File.separator + r.name().toLowerCase() + ".xml"), r);
    }
    private Class ctype;

    @Override
    public void writeTo(Writer out) throws IOException {
        for (CProcedural proc : procedurals)
            if (proc.getClass().equals(ctype))
                out.write(proc.toString());
    }

    public String getCurrentFile() {
        return currentFile;
    }

    public String getPackagename() {
        return packagename;
    }

    @Override
    public void finalizeStructures() {
        Reporter.setFile(null);
        for (CObject o : objects.values()) {
            Reporter.setObject(o.getName());
            o.finalizeStructures();
        }
    }
}
