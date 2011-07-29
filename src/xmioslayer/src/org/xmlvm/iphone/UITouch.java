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

import org.crossmobile.ios2a.ImplementationError;

public class UITouch extends NSObject {

    private final double timestamp;
    final int phase;
    final UIView parent;
    UIView view;
    CGPoint wloc;

    UITouch(UIView parent, int UITouchPhase, double timestamp, CGPoint wloc) {
        this.parent = parent;
        this.phase = UITouchPhase;
        this.timestamp = timestamp;
        this.wloc = wloc;
    }

    public CGPoint locationInView(UIView request) {
        if (request == null)
            request = parent;
        return request.convertPointFromView(wloc, parent);
    }

    public UIView getView() {
        return view;
    }

    public UIWindow getWindow() {
        return parent.getWindow();
    }

    public int getTapCount() {
        throw new ImplementationError();
    }

    public double getTimestamp() {
        return timestamp;
    }

    public int getPhase() {
        return phase;
    }
}
