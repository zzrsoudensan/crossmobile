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

import static org.xmlvm.iphone.UIViewContentMode.*;
import static android.view.MotionEvent.*;
import static org.xmlvm.iphone.UITouchPhase.*;

import org.crossmobile.ios2a.transf.CoreTransf;
import java.util.Set;
import android.view.MotionEvent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import org.crossmobile.ios2a.IOSView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.crossmobile.ios2a.IOSChild;
import org.crossmobile.ios2a.ImplementationError;
import org.crossmobile.ios2a.UIRunner;

public class UIView extends UIResponder {

    // Animation variables
    static CoreTransf transf = CoreTransf.normal();
    //
    private UIColor background;
    private int tag;
    UIViewController controller;    // This variable is used in MainActivity to support hardware back button
    private int contentMode;

    public UIView() {
        this(CGRect.Zero());
    }

    @SuppressWarnings({"LeakingThisInConstructor", "OverridableMethodCallInConstructor"})
    public UIView(CGRect frame) {
        __base().setTag(this);
        setTag(0);
        setFrame(frame);
        setUserInteractionEnabled(true);
    }

    public CGRect getFrame() {
        return ((IOSView.LayoutParams) __base().getLayoutParams()).getFrame();
    }

    public void setFrame(CGRect frame) {
        if (__model() != __base())
            __model().setLayoutParams(new IOSView.LayoutParams(frame.size));
        transf.setFrame(__base(), frame);
    }

    public CGRect getBounds() {
        CGRect frame = getFrame();
        return new CGRect(0, 0, frame.size.width, frame.size.height);
    }

    public void setBounds(CGRect rect) {
        setFrame(rect);
    }

    public UIColor getBackgroundColor() {
        return background;
    }

    public void setBackgroundColor(final UIColor col) {
        UIRunner.runSynced(new UIRunner() {

            @Override
            public void exec() {
                UIView.this.background = col;
                transf.setBackgroundColor(__base(), col.getModelDrawable(), col.getModelColor());
            }
        });
    }

    public CGAffineTransform getTransform() {
        return __base().getTransform();
    }

    public void setTransform(CGAffineTransform transform) {
        transf.setTransform(__base(), transform);
    }

    public float getAlpha() {
        return __base().getAlpha();
    }

    public void setAlpha(float alpha) {
        transf.setAlpha(__base(), alpha);
    }

    public void addSubview(UIView subView) {
        insertSubview(subView, Integer.MAX_VALUE);
    }

    public void insertSubview(UIView subView, final int idx) {
        if (subView != null)
            subView.changeParent(this, idx);
    }

    public void removeFromSuperview() {
        changeParent(null, 0);
    }

    void changeParent(final UIView newParent, final int index) {
        UIRunner.runSynced(new UIRunner() {

            @Override
            public void exec() {
                UIView oldParent = getSuperview();

                if (newParent == oldParent)
                    return;

                /* Inform delegates before any change */
                if (oldParent != null)
                    oldParent.willRemoveSubview(UIView.this);
                if (newParent != null)
                    newParent.willAddSubview(UIView.this);
                UIView.this.willMoveToSuperview(newParent);

                /* Perform actual move around */
                if (oldParent != null)
                    oldParent.getContainerLayer().removeView(getContentLayer());
                if (newParent != null)
                    if (index == Integer.MAX_VALUE)
                        newParent.getContainerLayer().addView(getContentLayer());
                    else
                        newParent.getContainerLayer().addView(getContentLayer(), index);

                /* Inform delegates after change */
                UIView.this.didMoveToSuperview();
                if (newParent != null)
                    newParent.didAddSubview(UIView.this);
                if (oldParent != null)
                    oldParent.didRemoveSubview(UIView.this);
            }
        });
    }

    void willAddSubview(UIView subview) {
    }

    void didRemoveSubview(UIView subview) {
    }

    public void didAddSubview(UIView subview) {
    }

    public void willRemoveSubview(UIView subview) {
    }

    public void willMoveToSuperview(UIView newSuperview) {
    }

    public void didMoveToSuperview() {
    }

    public void willMoveToWindow(UIWindow newWindow) {
    }

    public void didMoveToWindow() {
    }

    public void sendSubviewToBack(UIView subView) {
//        if (!subviews.contains(subView)) {
//            subviews.remove(subView);
//            subviews.add(0, subView);
//        }
        throw new ImplementationError();
    }

    public void bringSubviewToFront(final UIView subView) {
        if (subView.getSuperview() == this)
            UIRunner.runSynced(new UIRunner() {

                @Override
                public void exec() {
                    subView.getContentLayer().bringToFront();
                }
            });
    }

    public List<UIView> getSubviews() {
        ArrayList<UIView> children = new ArrayList<UIView>();
        for (int i = 0; i < getContainerLayer().getChildCount(); i++)
            try {
                children.add((UIView) ((IOSChild) getContainerLayer().getChildAt(i)).getIOSView().getTag());
            } catch (Exception e) {
            }
        return children;
    }

    public UIView getSuperview() {
        try {
            return (UIView) ((IOSView) getContentLayer().getParent()).getTag();
        } catch (Exception e) {
            return null;
        }
    }

    public void layoutSubviews() {
    }

    public UIWindow getWindow() {
        UIView parent = this;
        while ((parent = parent.getSuperview()) != null)
            if (parent instanceof UIWindow)
                return (UIWindow) parent;
        return null;
    }

    public final void setTag(int tag) {
        this.tag = tag;
    }

    public final int getTag() {
        return tag;
    }

    public CGPoint getCenter() {
        CGRect frame = getFrame();
        return new CGPoint((frame.origin.x + frame.size.width) / 2, (frame.origin.y + frame.size.width) / 2);
    }

    public void setCenter(CGPoint center) {
        CGRect frame = getFrame();
        CGPoint oldcenter = getCenter();
        oldcenter.x -= center.x;
        oldcenter.y -= center.y;
        setFrame(new CGRect(frame.origin.x - oldcenter.x, frame.origin.y - oldcenter.y, frame.size.width, frame.size.height));
    }

    public void setLocation(float x, float y) {
        CGRect frame = getFrame();
        setFrame(new CGRect(x, y, frame.size.width, frame.size.height));
    }

    public void setSize(float width, float height) {
        CGRect frame = getFrame();
        setFrame(new CGRect(frame.origin.x, frame.origin.y, width, height));
    }

    public void setNeedsDisplay() {
        UIRunner.runFree(new UIRunner() {

            @Override
            public void exec() {
                __base().requestLayout();
            }
        });
    }

    public void setOpaque(boolean opaque) {
        throw new ImplementationError();
    }

    public boolean isOpaque() {
        throw new ImplementationError();
    }

    public void setClearsContextBeforeDrawing(boolean clear) {
        throw new ImplementationError();
    }

    public boolean isHidden() {
        return __base().getVisibility() == View.INVISIBLE;
    }

    public void setHidden(final boolean hidden) {
        UIRunner.runSynced(new UIRunner() {

            @Override
            public void exec() {
                __base().setVisibility(hidden ? View.INVISIBLE : View.VISIBLE);
            }
        });
    }

    public void setContentMode(int UIViewContentMode) {
        contentMode = UIViewContentMode;
        Drawable d = __model().getBackground();
        if (!(d instanceof BitmapDrawable))
            return;
        BitmapDrawable bm = (BitmapDrawable) d;

        switch (contentMode) {
            case Top:
                bm.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                break;
            case TopRight:
                bm.setGravity(Gravity.TOP | Gravity.RIGHT);
                break;
            case Right:
                bm.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
                break;
            case BottomRight:
                bm.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
                break;
            case Bottom:
                bm.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                break;
            case BottomLeft:
                bm.setGravity(Gravity.BOTTOM | Gravity.LEFT);
                break;
            case Left:
                bm.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
                break;
            case TopLeft:
                bm.setGravity(Gravity.TOP | Gravity.LEFT);
                break;
            case Center:
                bm.setGravity(Gravity.CENTER);
                break;
            case ScaleAspectFit:
            // Not directly supported under Android?
            case ScaleAspectFill:
            // Not directly supported under Android?
            case ScaleToFill:
                bm.setGravity(Gravity.FILL);
                break;
            case Redraw:
                break;
        }
    }

    public int getContentMode() {
        return contentMode;
    }

    public boolean isUserInteractionEnabled() {
        return __model().isClickable();
    }

    public final void setUserInteractionEnabled(boolean userinteaction) {
        __model().setClickable(userinteaction);
        __model().setOnTouchListener(userinteaction ? new View.OnTouchListener() {

            public boolean onTouch(View view, MotionEvent ev) {
                UIEvent uie = new UIEvent();
                switch (ev.getAction()) {
                    case ACTION_DOWN:
                        touchesBegan(getList(ev, Began), uie);
                        break;
                    case ACTION_MOVE:
                        touchesMoved(getList(ev, Moved), uie);
                        break;
                    case ACTION_UP:
                        touchesEnded(getList(ev, Ended), uie);
                        break;
                    case ACTION_CANCEL:
                        touchesCancelled(getList(ev, Cancelled), uie);
                        break;
                }
                return false;
            }

            private Set<UITouch> getList(MotionEvent ev, int phase) {
                HashSet<UITouch> touches = new HashSet<UITouch>();
                for (int p = 0; p < ev.getPointerCount(); p++)
                    touches.add(new UITouch(phase, UIView.this, System.currentTimeMillis() / 1000d,
                            IOSView.xAndroid((int) (0.5f + ev.getX(p))),
                            IOSView.yAndroid((int) (0.5f + ev.getY(p)))));
                return touches;
            }
        } : null);
    }

    public boolean clipsToBounds() {
        return true;
    }

    public void setClipsToBounds(boolean clipsToBounds) {
        throw new ImplementationError();
    }

    public CGPoint convertPointToView(CGPoint point, UIView view) {
        throw new ImplementationError();
    }

    public CGPoint convertPointFromView(CGPoint point, UIView view) {
        throw new ImplementationError();
    }

    public CGRect convertRectToView(CGRect point, UIView view) {
        throw new ImplementationError();
    }

    public CGRect convertRectFromView(CGRect point, UIView view) {
        throw new ImplementationError();
    }

    public CALayer getLayer() {
        throw new ImplementationError();
    }

    public int getAutoresizingMask() {
        throw new ImplementationError();
    }

    public void setAutoresizingMask(int UIViewAutoresizing) {
        throw new ImplementationError();
    }

    public boolean isAutoresizesSubviews() {
        throw new ImplementationError();
    }

    public void setAutoresizesSubviews(boolean autoresizesSubviews) {
        throw new ImplementationError();
    }

    /* View animations */
    public static void beginAnimations(String animationID) {
        transf = CoreTransf.create(animationID);
    }

    public static void commitAnimations() {
        transf.commit();
        transf = CoreTransf.normal();
    }

    public static void setAnimationStartDate(NSDate startTime) {
    }

    public static void setAnimationsEnabled(boolean enabled) {
        CoreTransf.setAnimationsEnabled(enabled);
    }

    public static void setAnimationDuration(double duration) {
        transf.setDuration(duration);
    }

    public static void setAnimationDelay(double delay) {
    }

    public static void setAnimationCurve(int UIViewAnimationCurve) {
    }

    public static void setAnimationRepeatCount(float repeatCount) {
    }

    public static void setAnimationRepeatAutoreverses(boolean repeatAutoreverses) {
    }

    public static void setAnimationBeginsFromCurrentState(boolean fromCurrentState) {
    }

    public static void setAnimationTransitionForView(int UIViewAnimationTransition, UIView view, boolean cache) {
    }

    public static boolean areAnimationsEnabled() {
        return CoreTransf.areAnimationsEnabled();
    }

    public static void setAnimationDelegate(UIViewAnimationDelegate delegate) {
        transf.setAnimationDelegate(delegate);
    }

    public CGSize sizeThatFits(CGSize size) {
        throw new ImplementationError();
    }

    public void sizeToFit() {
        throw new ImplementationError();
    }

    @Override
    public String toString() {
        String classname = getClass().getName();
        int point = classname.lastIndexOf('.');
        if (point >= 0)
            classname = classname.substring(point + 1);
        return "[" + classname + " " + getFrame().toString() + " tag=" + tag + "]";
    }

    public void drawRect(CGRect rect) {
        // Do nothing
    }
}
