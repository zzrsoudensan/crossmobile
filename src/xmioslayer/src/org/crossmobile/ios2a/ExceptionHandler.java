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
package org.crossmobile.ios2a;

import android.app.Activity;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final ExceptionHandler base = new ExceptionHandler();
    private Thread.UncaughtExceptionHandler system;

    @SuppressWarnings("CallToThreadDumpStack")
    public void uncaughtException(Thread thread, Throwable thrwbl) {
        final Activity act = MainActivity.current;
        if (act != null)  // Silence errors when activity is not active
            system.uncaughtException(thread, thrwbl);
        System.exit(10);
    }

    public static void setActive() {
        base.active();
    }

    private void active() {
        if (system != null)
            return;
        system = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }
}
