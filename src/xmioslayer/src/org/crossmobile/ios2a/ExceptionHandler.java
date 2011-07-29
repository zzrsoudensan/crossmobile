/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.crossmobile.ios2a;

import android.app.Activity;

/**
 *
 * @author teras
 */
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
