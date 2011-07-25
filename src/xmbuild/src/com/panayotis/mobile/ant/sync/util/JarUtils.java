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
 * along with Jubler; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package com.panayotis.mobile.ant.sync.util;

import com.panayotis.mobile.ant.SynchronizeProject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.AntTypeDefinition;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.Task;

public class JarUtils {

    public static InputStream getStreamFromJar(JarFile jarpath, JarPathEntry entry) throws IOException {
        return jarpath.getInputStream(new ZipEntry(entry.home + "/" + entry.path + "/" + entry.name));
    }

    public static JarFile getSelfJar(Task t) {
        Class classtype = SynchronizeProject.class;
        String jarname = File.separator + "xmbuild.jar";

        @SuppressWarnings("UseOfObsoleteCollectionType")
        java.util.Hashtable definitions = ComponentHelper.getComponentHelper(t.getProject()).getTaskDefinitions();
        for (Object o : definitions.keySet())
            if (definitions.get(o).equals(classtype)) {
                AntTypeDefinition def = ComponentHelper.getComponentHelper(t.getProject()).getDefinition((String) o);
                AntClassLoader cl = ((AntClassLoader) def.getClassLoader());

                String classpath = cl.getClasspath();
                StringTokenizer tok = new StringTokenizer(classpath, File.pathSeparator);
                String path;
                while (tok.hasMoreTokens()) {
                    path = tok.nextToken();
                    if (path.toLowerCase().endsWith(jarname))
                        try {
                            return new JarFile(path);
                        } catch (IOException ex) {
                        }
                }
                return null;
            }
        return null;
    }

    public static ArrayList<JarPathEntry> getListOfEntries(JarFile jarpath, String dirpath) {
        String dirpathslashed;
        if (!dirpath.endsWith("/"))
            dirpathslashed = dirpath + "/";
        else
            dirpathslashed = dirpath;

        int dirpathsize = dirpathslashed.length();

        ArrayList<JarPathEntry> res = new ArrayList<JarPathEntry>();
        Enumeration<JarEntry> entries = jarpath.entries();
        JarEntry entry;
        String name;
        while (entries.hasMoreElements()) {
            entry = entries.nextElement();
            name = entry.getName();
            if (!name.endsWith("/") && name.startsWith(dirpathslashed)) {
                int lastslash = name.lastIndexOf("/");
                res.add(new JarPathEntry(dirpath, name.substring(dirpathsize, lastslash), name.substring(lastslash + 1)));
            }
        }
        return res;
    }

    public static class JarPathEntry {

        private final String home;
        public final String path;
        public final String name;

        private JarPathEntry(String home, String path, String name) {
            this.home = home;
            this.path = path;
            this.name = name;
        }
    }
}
