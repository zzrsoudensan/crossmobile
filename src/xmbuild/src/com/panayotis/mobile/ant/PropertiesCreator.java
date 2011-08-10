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
import com.panayotis.mobile.ant.sync.util.InfoPlist;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class PropertiesCreator extends Task {

    private Properties prop;
    private File outdir;
    private static final String TEMPLATE =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n"
            + "<plist version=\"1.0\">\n"
            + "<dict>\n"
            + "\t<key>UIStatusBarHidden</key>\n"
            + "\t<PROPERTY_STATUSBARHIDDEN/>\n"
            + "\t<key>UIPrerenderedIcon</key>\n"
            + "\t<PROPERTY_PRERENDEREDICON/>\n"
            + "\t<key>UIApplicationExitsOnSuspend</key>\n"
            + "\t<PROPERTY_APPLICATIONEXITS/>\n"
            + "\t<key>CFBundleDevelopmentRegion</key>\n"
            + "\t<string>English</string>\n"
            + "\t<key>CFBundleDisplayName</key>\n"
            + "\t<string>PROPERTY_BUNDLEDISPLAYNAME</string>\n"
            + "\t<key>CFBundleExecutable</key>\n"
            + "\t<string>XMLVM_APP</string>\n"
            + "\t<key>CFBundleIdentifier</key>\n"
            + "\t<string>PROPERTY_BUNDLEIDENTIFIER</string>\n"
            + "\t<key>CFBundleInfoDictionaryVersion</key>\n"
            + "\t<string>6.0</string>\n"
            + "\t<key>CFBundleName</key>\n"
            + "\t<string>XMLVM_APP</string>\n"
            + "\t<key>CFBundlePackageType</key>\n"
            + "\t<string>APPL</string>\n"
            + "\t<key>CFBundleSignature</key>\n"
            + "\t<string>????</string>\n"
            + "\t<key>CFBundleVersion</key>\n"
            + "\t<string>PROPERTY_BUNDLEVERSION</string>\n"
            + "\t<key>LSRequiresIPhoneOS</key>\n"
            + "\t<true/>\n"
            + "\t<key>UIInterfaceOrientation</key>\n"
            + "\t<string>PROPERTY_INTERFACE_ORIENTATION</string>\n"
            + "PROPERTY_SUPPORTED_INTERFACE_ORIENTATIONS\n"
            + "PROPERTY_FONTS\n"
            + "</dict>\n"
            + "</plist>\n";

    public void setInput(File input) {
        prop = new Properties();
        try {
            prop.load(new InputStreamReader(new FileInputStream(input), "UTF-8"));
        } catch (Exception ex) {
            throw new BuildException(ex);
        }
    }

    public void setOutdir(File output) {
        this.outdir = output;
    }

    @Override
    public void execute() throws BuildException {
        if (prop == null)
            throw new BuildException("Input file is missing");
        if (outdir == null)
            throw new BuildException("Output directory is missing");
        System.out.println("Writing CrossMobile properties files");
        outdir.mkdirs();

        // Write Info.plist file
        InfoPlist info = new InfoPlist(TEMPLATE);
        info.setApplication(prop("bundle.displayname", "CrossMobile"));
        info.setIdentifier(prop("bundle.identifier", "com.mycompany"));
        info.setVersion(prop("bundle.version", "1.0"));
        info.setDisplayName(prop("bundle.displayname", "CrossMobile"));
        info.setStatusBarHidden(prop("statusbarhidden", "false"));
        info.setPrerenderIcon(prop("prerenderedicon", "false"));
        info.setApplicationExits(prop("applicationexits", "true"));
        info.setDefaultOrientation(prop("orientations.initial", "UIInterfaceOrientationPortrait"));
        info.setSupportedOrientations(prop("orientations.supported", "UIInterfaceOrientationPortrait"));
        info.setFonts(prop("appfonts", ""));
        FileUtils.writeFile(new File(outdir, "Info.plist"), info.toString());

        // Write crossmobile.properties
        Properties crossmobile = new Properties();
        crossmobile.setProperty("xm.splash.delay", prop("xm.splash.delay", "1"));
        crossmobile.setProperty("xm.table.cells", prop("xm.table.cells", "20"));
        crossmobile.setProperty("xm.map.apikey", prop("xm.map.apikey", ""));
        crossmobile.setProperty("xm.device", prop("xmlvm.project", "iphone"));
        try {
            crossmobile.store(new FileWriter(outdir + File.separator + "crossmobile.properties"), null);
        } catch (IOException ex) {
            throw new BuildException(ex);
        }
    }

    private String prop(String property, String defval) {
        String val = prop.getProperty(property);
        return val == null || val.trim().isEmpty() ? defval : val.trim();
    }
}
