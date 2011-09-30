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

public class Main {

    private static final String inputpath = System.getProperty("user.home") + File.separator + "/Works/Development/Mobile/SDK/CrossMobile/Frameworks";
    private static final String outputpath = System.getProperty("user.home") + File.separator + "output";
    private static final String searchInto = null;//"/Users/teras/Works/Development/Mobile/SDK/CrossMobile/Frameworks/SystemConfiguration.framework/Headers/SCSchemaDefinitions.h";

    public static void main(String[] args) {
        CLibrary library = new CLibrary("org.xmlvm.iphone");

        if (searchInto == null)
            for (File frameworks : new File(inputpath).listFiles()) {
                System.out.println("*** Parsing " + frameworks.getPath());
                if (new File(frameworks + File.separator + "Headers").isDirectory())
                    for (File f : new File(frameworks, "Headers").listFiles())
                        if (f.getPath().toLowerCase().endsWith(".h"))
                            library.parseFile(f.getPath());
            }
        else
            library.parseFile(searchInto);

        System.out.println("*** Finalize");
        library.finalizeStructures();

        System.out.println("*** Output");
        library.output(outputpath);
    }
}
