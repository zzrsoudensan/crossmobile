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
package org.xmlvm.iphone;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.crossmobile.ios2a.FileBridge;
import org.crossmobile.ios2a.MainActivity;

public class Foundation {

    private static String TEMP;
    private static String USERLOCATION;
    private static String TEMPSUFF = "/temp/" + UIApplication.sharedApplication().getDelegate().getClass().getName();
    private static final String[] PATHS = {
        null,
        "/Applications",
        "/Applications/Demos",
        "/Developer/Applications",
        "/Applications/Utilities",
        "/Library",
        "/Developer",
        null,
        "/Library/Documentation",
        "/Documents",
        null,
        "/Library/Autosave Information",
        "/Desktop",
        "/Library/Caches",
        "/Library/Application Support",
        "/Downloads",
        "/Library/Input Methods",
        "/Movies",
        "/Music",
        "/Pictures",
        null,
        "/Public",
        "/Library/PreferencePanes"};

    public static ArrayList<String> NSSearchPathForDirectoriesInDomains(int NSSearchPathDirectory, int NSSearchPathDomainMask, boolean expandTilde) {
        initUserLocation();
        ArrayList<String> res = new ArrayList<String>();
        if (NSSearchPathDirectory >= 1 && NSSearchPathDirectory <= 22 && PATHS[NSSearchPathDirectory] != null)
            res.add(USERLOCATION + PATHS[NSSearchPathDirectory]);
        return res;
    }

    public static String NSTemporaryDirectory() {
        if (TEMP == null) {
            initUserLocation();
            FileBridge.deleteRecursive(new File(USERLOCATION + TEMPSUFF)); // Clean up internal memory, just in case
            try {
                File test = File.createTempFile("xmlvm.temporary", ".file");
                test.delete();
                TEMP = test.getParent();
            } catch (IOException ex) {
            }
            if (TEMP == null)
                TEMP = USERLOCATION;
            TEMP += TEMPSUFF;
            FileBridge.deleteRecursive(new File(TEMP + TEMPSUFF)); // Delete old temporary files
            new File(TEMP).mkdirs();
        }
        return TEMP;
    }

    public static String NSHomeDirectory() {
        return USERLOCATION;
    }

    private static void initUserLocation() {
        if (USERLOCATION == null) {
            USERLOCATION = MainActivity.current.getFilesDir().getPath();
            new File(USERLOCATION + PATHS[org.xmlvm.iphone.NSSearchPathDirectory.Document]).mkdirs();
            new File(USERLOCATION + PATHS[org.xmlvm.iphone.NSSearchPathDirectory.Caches]).mkdirs();
        }
    }
}
