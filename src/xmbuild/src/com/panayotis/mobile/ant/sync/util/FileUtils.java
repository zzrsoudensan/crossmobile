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
 */package com.panayotis.mobile.ant.sync.util;

import com.panayotis.mobile.ant.SynchronizeProject;
import com.panayotis.mobile.ant.sync.util.JarUtils.JarPathEntry;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.JarFile;
import org.apache.tools.ant.BuildException;

public class FileUtils {

    private final SynchronizeProject task;
    private ArrayList<File> resourcelist;

    public FileUtils(SynchronizeProject task) {
        this.task = task;
    }

    public List<File> getResourceList() {
        if (resourcelist == null) {
            resourcelist = new ArrayList<File>();
            StringTokenizer tk = new StringTokenizer(task.getIOSResources(), ":");
            while (tk.hasMoreTokens()) {
                String item = tk.nextToken();
                File file = new File(item);
                if (!file.isAbsolute())
                    file = new File(task.getProject().getBaseDir(), item).getAbsoluteFile();
                if (item.endsWith("/"))
                    resourcelist.addAll(Arrays.asList(file.listFiles()));
                else
                    resourcelist.add(file);
            }
        }
        return resourcelist;
    }

    private File findImage(String img) {
        for (File item : getResourceList())
            if (item.getName().equals(img + ".png"))
                return item;
        return null;
    }

    public boolean copyImage(String fromimg, String toimg) {
        File parent = new File(task.getResources(), "drawable");
        if ((!parent.isDirectory()) && (!parent.mkdirs()))
            return false;
        try {
            copyFile(findImage(fromimg), new File(parent, toimg + ".png"));
            return true;
        } catch (Exception ex) {
        }
        return false;
    }

    public static void createFile(File fout, String content) throws BuildException {
        File parent = fout.getParentFile();
        parent.mkdirs();
        if (!parent.isDirectory())
            throw new BuildException("Unable to create directory " + parent.getPath());
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(fout));
            out.write(content);
        } catch (IOException ex) {
            throw new BuildException(ex);
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch (IOException ex1) {
                }
        }
    }

    public static void copyFile(File inF, File outF) throws BuildException {
        if (!(inF != null && outF != null && inF.isFile() && inF.canRead()))
            throw new BuildException("Error while initializing files");
        if (outF.isFile())
            delete(outF);

        outF.getParentFile().mkdirs();
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(inF);
            out = new FileOutputStream(outF);
            copyStream(in, out);
        } catch (Exception ex) {
            throw new BuildException(ex);
        } finally {
            closeStreams(in, out);
        }
    }

    public void copySelfResources() throws BuildException {
        JarFile self = JarUtils.getSelfJar(task);
        if (self == null)
            throw new BuildException("Unable to find xmbuild.jar.");
        ArrayList<JarPathEntry> entries = JarUtils.getListOfEntries(self, "res");
        if (entries == null || entries.isEmpty())
            throw new BuildException("Unable to find 'res' directory inside xmbuild.jar");

        File resdir = task.getResources();
        InputStream in = null;
        FileOutputStream out = null;
        try {
            for (JarPathEntry entry : entries) {
                in = JarUtils.getStreamFromJar(self, entry);
                File parent = new File(resdir, entry.path);
                parent.mkdirs();
                out = new FileOutputStream(new File(parent, entry.name));
                copyStream(in, out);

                // Required, since we re in a loop
                closeStreams(in, out);
                in = null;
                out = null;
            }
        } catch (Exception ex) {
            closeStreams(in, out);
            throw new BuildException(ex);
        }
    }

    private static void copyStream(InputStream in, OutputStream out) throws IOException {
        if (in != null && out != null) {
            byte buffer[] = new byte[1024];
            int length = 0;
            while ((length = in.read(buffer)) > 0)
                out.write(buffer, 0, length);
        }
    }

    private static void closeStreams(InputStream in, OutputStream out) {
        if (in != null)
            try {
                in.close();
            } catch (IOException ex) {
            }
        if (out != null)
            try {
                out.close();
            } catch (IOException ex) {
            }
    }

    public static void delete(File current) throws BuildException {
        if (current.isDirectory())
            for (File sub : current.listFiles())
                delete(sub);
        if (current.exists() && (!current.delete()))
            throw new BuildException("Unable to remove " + current.getPath());
    }

    public static void copyInSync(File source, File target) throws BuildException {
        if (target.exists() && source.isDirectory() != target.isDirectory())
            FileUtils.delete(target);

        if (source.isDirectory()) {
            if ((!target.exists()) && (!target.mkdirs()))
                throw new BuildException("Unable to create directory " + target.getPath());
            for (File item : source.listFiles())
                copyInSync(item, new File(target, item.getName()));
        } else if (source.length() != target.length())
            copyFile(source, target);
    }
}
