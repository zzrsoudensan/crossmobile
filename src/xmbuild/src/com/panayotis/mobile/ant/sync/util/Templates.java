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

public class Templates {

    public static final String PACKAGENAME_ANCHOR = "__PACKAGENAME__";
    public static final String MAINACTIVITY_ANCHOR = "__MAINACTIVITY__";
    public static final String MAINCLASS_ANCHOR = "__MAINCLASS__";
    public static final String SPLASHACTIVITY_ANCHOR = "__SPLASHACTIVITY__";
    //
    public static final String SPLASHLAYOUT = "splash";
    public static final String SPLASHVIEW = "splashview";
    public static final String SPLASHDRAWABLE = "appsplash";
    public static final String ICONDRAWABLE = "appicon";
    //
    public static final String LAUNCHTAG = ".MAIN";
    public static final String MAINTAG = ".XMMAIN";
    public static final String MAPTAG = ".XMMAP";
    //
    public static final String MAINACTIVITY_TEMPLATE =
            "/* AUTO-GENERATED FILE. DO NOT MODIFY.\n"
            + " *\n"
            + " * This file was created automatically by the CrossMobile tools.\n"
            + " * It should not be modified by hand.\n"
            + " */\n"
            + "\n"
            + "package " + PACKAGENAME_ANCHOR + ";\n"
            + "public class " + MAINACTIVITY_ANCHOR + " extends org.crossmobile.ios2a.MainActivity {\n"
            + "    protected String getMainClass() {\n"
            + "        return \"" + MAINCLASS_ANCHOR + "\";\n"
            + "    }\n"
            + "}\n";
    public static final String SPLASHACTIVITY_TEMPLATE =
            "/* AUTO-GENERATED FILE. DO NOT MODIFY.\n"
            + " *\n"
            + " * This file was created automatically by the CrossMobile tools.\n"
            + " * It should not be modified by hand.\n"
            + " */\n"
            + "\n"
            + "package " + PACKAGENAME_ANCHOR + ";\n"
            + "public class " + SPLASHACTIVITY_ANCHOR + " extends org.crossmobile.ios2a.SplashActivity {\n"
            + "    protected String getMainActivity() {\n"
            + "        return \"" + PACKAGENAME_ANCHOR + MAINTAG + "\";\n"
            + "    }\n"
            + "\n"
            + "    protected int getSplashResource() {\n"
            + "        return R.layout." + SPLASHLAYOUT + ";\n"
            + "    }\n"
            + "\n"
            + "    protected int getSplashViewID() {\n"
            + "        return R.id." + SPLASHVIEW + ";\n"
            + "    }\n"
            + "}\n";
    public static final String SPLASHLAYOUT_TEMPLATE = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
            + "<ImageView android:id=\"@+id/" + SPLASHVIEW + "\" xmlns:android=\"http://schemas.android.com/apk/res/android\" android:src=\"@drawable/" + SPLASHDRAWABLE + "\" android:layout_width=\"fill_parent\" android:layout_height=\"fill_parent\" android:scaleType=\"fitXY\"/>\n";
}
