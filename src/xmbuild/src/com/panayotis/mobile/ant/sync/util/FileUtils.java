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

package com.panayotis.mobile.ant.sync.util;

import com.panayotis.mobile.ant.SynchronizeProject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class FileUtils {

    private final SynchronizeProject task;
    private ArrayList<File> resourcelist;

    public FileUtils(SynchronizeProject task) {
        this.task = task;
    }

    public static List<File> getFileList(Task task, String items) {
        ArrayList<File> result = new ArrayList<File>();
        StringTokenizer tk = new StringTokenizer(items, ":");
        while (tk.hasMoreTokens())
            try {
                String item = tk.nextToken();
                File file = new File(item);
                if (!file.isAbsolute())
                    file = new File(task.getProject().getBaseDir(), item).getAbsoluteFile();
                result.add(file.getCanonicalFile());
            } catch (IOException ex) {
            }
        return result;
    }

    /* Probably use getFileList instead? What about the "/" character? */
    public List<File> getResourceList() {
        if (resourcelist == null) {
            resourcelist = new ArrayList<File>();
            StringTokenizer tk = new StringTokenizer(task.getIOSResources(), ":");
            while (tk.hasMoreTokens()) {
                String item = tk.nextToken();
                File file = new File(item);
                if (!file.isAbsolute())
                    file = new File(task.getProject().getBaseDir(), item).getAbsoluteFile();
                try {
                    file = file.getCanonicalFile();
                } catch (IOException ex) {
                }
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
        checkFileIsValid(inF, "Source");
        if (outF == null)
            throw new BuildException("Destination file should not be null");
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

    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        if (in != null && out != null) {
            byte buffer[] = new byte[1024];
            int length = 0;
            while ((length = in.read(buffer)) > 0)
                out.write(buffer, 0, length);
        }
    }

    public static void copyReaders(Reader in, Writer out) throws IOException {
        if (in != null && out != null) {
            char buffer[] = new char[1024];
            int length = 0;
            while ((length = in.read(buffer)) > 0)
                out.write(buffer, 0, length);
        }
    }

    public static void closeStreams(InputStream in, OutputStream out) {
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

    public static void checkFileIsValid(File test, String type) throws BuildException {
        if (test == null)
            throw new BuildException(type + " file should not be null");
        if (!test.isFile())
            throw new BuildException(type + " '" + test.getPath() + "' is not a file");
        if (!test.canRead())
            throw new BuildException(type + " file '" + test.getPath() + "' is not readable");
    }

    public static String readFile(File input) throws BuildException {
        if (input == null)
            throw new BuildException("Input file not defined");
        StringWriter out = new StringWriter();
        Reader in = null;
        try {
            in = new InputStreamReader(new FileInputStream(input), "UTF-8");
            copyReaders(in, out);
            return out.toString();
        } catch (Exception ex) {
            throw new BuildException(ex);
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException ex) {
                }
        }
    }

    public static void writeFile(File output, String data) throws BuildException {
        if (output == null)
            throw new BuildException("Input file not defined");
        StringReader in = new StringReader(data == null ? "" : data);
        Writer out = null;
        try {
            out = new OutputStreamWriter(new FileOutputStream(output), "UTF-8");
            copyReaders(in, out);
        } catch (Exception ex) {
            throw new BuildException(ex);
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch (IOException ex) {
                }
        }
    }
}
