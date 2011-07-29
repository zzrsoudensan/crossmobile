/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xmlvm.iphone;

import android.view.MotionEvent;
import static org.xmlvm.iphone.UITouchPhase.*;

/**
 *
 * @author teras
 */
class xmEventDispatcher {

    UIView source;
    private UIView lastViewFound;

    xmEventDispatcher(UIView source) {
        this.source = source;
    }

    void send(MotionEvent ev) {
        send(new UIEvent(this, ev, false));
    }

    void send(MotionEvent ev, boolean ignoreBar) {
        send(new UIEvent(this, ev, ignoreBar));
    }

    void send(UIEvent event) {
        switch (event.firsttouch.phase) {
            /**
             * Send events to all responders or only to the first one?
             * Right now only the first touch event is taken into account.
             * Also check if every touch event points to different view
             * (and properly define UIEvent.updateToView)
             */
            case Began:
                lastViewFound = source.hitTest(new CGPoint(event.firsttouch.wloc.x, event.firsttouch.wloc.y), event);
                if (lastViewFound != null) {
                    event.updateToView(lastViewFound);
                    lastViewFound.touchesBegan(event.allTouches(), event);
                }
                break;
            case Moved:
                if (lastViewFound != null) {
                    event.updateToView(lastViewFound);
                    lastViewFound.touchesMoved(event.allTouches(), event);
                }
                break;
            case Ended:
                if (lastViewFound != null) {
                    event.updateToView(lastViewFound);
                    lastViewFound.touchesEnded(event.allTouches(), event);
                }
                break;
            case Cancelled:
            default:
                if (lastViewFound != null) {
                    event.updateToView(lastViewFound);
                    lastViewFound.touchesCancelled(event.allTouches(), event);
                }
                break;
        }
    }
}
