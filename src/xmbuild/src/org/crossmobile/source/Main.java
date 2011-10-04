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

package org.crossmobile.source;

import java.io.File;
import org.crossmobile.source.ctype.CLibrary;
import org.crossmobile.source.out.JavaOut;

public class Main {

    private static boolean printProgress = false;
    //
    private static final String inputpath = "/Developer/Platforms/iPhoneOS.platform/Developer/SDKs/iPhoneOS4.3.sdk/System/Library/Frameworks";
    //private static final String inputpath = System.getProperty("user.home") + File.separator + "/Works/Development/Mobile/SDK/CrossMobile/Frameworks";
    private static final String outputpath = System.getProperty("user.home") + File.separator + "output";
    private static final String searchInto = null;//"/Users/teras/Works/Development/Mobile/SDK/CrossMobile/Frameworks/SystemConfiguration.framework/Headers/SCSchemaDefinitions.h";

    public static void main(String[] args) {
        CLibrary library = new CLibrary("org.xmlvm.iphone");

        if (searchInto == null)
            addFixed(library, new File(inputpath));
            //addRecursive(library, new File(inputpath));
        else
            library.addFile(searchInto);

        if (printProgress)
            System.out.println("*** Finalize");
        library.finalizeLibrary();

        if (printProgress)
            System.out.println("*** Output");
        JavaOut out = new JavaOut(outputpath);

        out.generate(library);
        out.report();
    }

    private static void addRecursive(CLibrary lib, File file) {
        if (file.isDirectory()) {
            File[] list = file.listFiles();
            if (list != null)
                for (File item : list)
                    addRecursive(lib, item);
        } else {
            String filename = file.getPath();
            if (filename.toLowerCase().endsWith(".h")) {
                if (printProgress)
                    System.out.println("*** Parsing " + filename);
                lib.addFile(filename);
            }
        }
    }

    private static void addFixed(CLibrary lib, File file) {
        for (File frameworks : file.listFiles()) {
            if (printProgress)
                System.out.println("*** Parsing " + frameworks.getPath());
            if (new File(frameworks + File.separator + "Headers").isDirectory())
                for (File f : new File(frameworks, "Headers").listFiles())
                    if (f.getPath().toLowerCase().endsWith(".h"))
                        lib.addFile(f.getPath());
        }
    }
}
