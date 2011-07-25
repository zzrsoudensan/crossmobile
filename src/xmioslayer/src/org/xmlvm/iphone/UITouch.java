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

    private final int phase;
    private final UIView view;
    private final double timestamp;
    private final float viewX;
    private final float viewY;

    UITouch(int UITouchPhase, UIView view, double timestamp, float x, float y) {
        this.phase = UITouchPhase;
        this.view = view;
        this.timestamp = timestamp;
        this.viewX = x;
        this.viewY = y;
    }

    public CGPoint locationInView(UIView request) {
        if (request == view)
            return new CGPoint(viewX, viewY);
        else
            throw new ImplementationError();
//        if (request == null)
//            request = UIApplication.sharedApplication().getKeyWindow();
//        float dx = 0;
//        float dy = 0;
//        CGRect frame;
//        while (request != null) {
//            frame = request.getFrame();
//            dx += frame.origin.x;
//            dy += frame.origin.y;
//            request = request.getSuperview();
//        }
//        return new CGPoint(viewX - dx, viewY - dy);
    }

    public UIView getView() {
        return view;
    }

    public UIWindow getWindow() {
        return view.getWindow();
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
