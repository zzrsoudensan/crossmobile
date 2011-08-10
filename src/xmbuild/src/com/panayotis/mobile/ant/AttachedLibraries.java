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

package com.panayotis.mobile.ant;

import com.panayotis.mobile.ant.sync.util.FileUtils;
import com.panayotis.mobile.ant.sync.util.JarUtils;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class AttachedLibraries extends Task {

    private String list = "";
    private Set<String> blacklist = new HashSet<String>();
    private File dest;
    private String xmioslayer = "";
    private boolean skiplayerlib = false;

    public void setList(String list) {
        this.list = list;
    }

    public void setBlacklist(String blacklist) {
        StringTokenizer tk = new StringTokenizer(blacklist, ":");
        while (tk.hasMoreElements())
            this.blacklist.add(tk.nextToken().trim().toLowerCase());
    }

    public void setDest(File dest) {
        this.dest = dest;
    }

    public void setXmioslayer(String xmioslayer) {
        this.xmioslayer = xmioslayer;
    }

    public void setSkiplayerlib(boolean skiplayerlib) {
        this.skiplayerlib = skiplayerlib;
    }

    @Override
    public void execute() throws BuildException {
        if (dest == null)
            throw new NullPointerException("Should define destination directory");
        if (dest.exists())
            FileUtils.delete(dest);
        dest.mkdirs();

        for (File in : FileUtils.getFileList(this, list)) {
            String signature = in.getName().toLowerCase();
            if (!blacklist.contains(signature) && signature.endsWith(".jar")) {
                File out = new File(dest, in.getName());
                JarUtils.copyPlugin(in, out);
            }
        }
        if (!skiplayerlib) {
            File ioslayer = new File(xmioslayer);
            if (xmioslayer.endsWith("/xmioslayer.jar") && ioslayer.isFile())
                JarUtils.copyPlugin(ioslayer, new File(dest, "xmioslayer.jar"));
            else
                throw new BuildException("Unknown XMiOSLayer file " + xmioslayer);
        }
    }
}
