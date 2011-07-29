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
package com.panayotis.mobile.ant;

import com.panayotis.mobile.ant.sync.util.AndroidUtils;
import com.panayotis.mobile.ant.sync.util.FileUtils;
import com.panayotis.mobile.ant.sync.util.Templates;
import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class SynchronizeProject extends Task {

    private String mainclass;
    private String displayname;
    private File generated;
    private File resources;
    private File asset;
    private String iosresources;
    private boolean ignore = false;
    private boolean debuggable = false;

    public void setMainClass(String mainclass) {
        this.mainclass = mainclass;
    }

    public String getMainClass() {
        return mainclass;
    }

    public void setDisplayName(String displayname) {
        this.displayname = displayname;
    }

    public String getDisplayName() {
        return displayname;
    }

    public void setGenerated(File generated) {
        this.generated = generated;
    }

    public File getGenerated() {
        return generated;
    }

    public void setResources(File resources) {
        this.resources = resources;
    }

    public File getResources() {
        return resources;
    }

    public void setIOSResources(String iosresources) {
        this.iosresources = iosresources;
    }

    public String getIOSResources() {
        return iosresources;
    }

    public void setAsset(File asset) {
        this.asset = asset;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public void setDebuggable(boolean debuggable) {
        this.debuggable = debuggable;
    }

    @Override
    public void execute() throws BuildException {
        if (ignore)
            return;

        AndroidUtils.checkValidClass("mainclass", mainclass);
        int lastdot = mainclass.lastIndexOf('.');
        String packname = mainclass.substring(0, lastdot);
        String classname = mainclass.substring(lastdot + 1);
        System.out.println("Updating project with main class " + mainclass);

        if (displayname == null)
            throw new BuildException("Property displayname should be set");

        if (generated == null)
            generated = new File(getProject().getBaseDir(), "gen");

        if (resources == null)
            resources = new File(getProject().getBaseDir(), "res");

        if (asset == null)
            asset = new File(getProject().getBaseDir(), "asset");

        FileUtils futils = new FileUtils(this);
        AndroidUtils autils = new AndroidUtils(this);

        autils.createActivities(packname, classname);
        autils.updateAndroidManifest(packname, classname, debuggable);
        autils.createAssets(futils.getResourceList(), asset);
        futils.copySelfResources();
        futils.copyImage("Icon", Templates.ICONDRAWABLE);
        futils.copyImage("Default", Templates.SPLASHDRAWABLE);
    }
}
