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
import org.crossmobile.ios2a.transf.RichTransformation;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import org.xmlvm.iphone.CGAffineTransform;
import org.xmlvm.iphone.CGRect;
import org.xmlvm.iphone.CGSize;
import org.xmlvm.iphone.UIApplication;
import org.xmlvm.iphone.UIScreen;
import org.crossmobile.ios2a.transf.Parameters;

public class IOSView extends ViewGroup implements IOSChild {

    public static final float STATUSBAR_HEIGHT = 20;
//
    private static int androidBar;
    private static float xratio;
    private static float yratio;

    public static float getScale() {
        return (xratio + yratio) / 2;
    }
    //
    private RichTransformation transformation = null;
    private int deltaY = 0; // This is useful to reserve space for views hidden by status bar

    public static void updateBarMetrics(Activity activity) {
        Rect rectgle = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rectgle);
        androidBar = rectgle.top;
        System.out.println("Bar is " + androidBar);
    }

    public static void updateRatios() {
        // TODO : take care of orientations...
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) MainActivity.current.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        CGRect appframe = UIScreen.mainScreen().getApplicationFrame();
        xratio = (float) metrics.widthPixels / appframe.size.width;
        yratio = (float) (metrics.heightPixels
                - (UIApplication.sharedApplication().isStatusBarHidden() ? 0 : androidBar))
                / appframe.size.height;
    }

    public IOSView(Context ctx) {
        super(ctx);
        setStaticTransformationsEnabled(true);
    }

    public float getAlpha() {
        return transformation == null ? 1 : transformation.getAlpha();
    }

    public void setAlpha(float alpha) {
        transformation = RichTransformation.setAlpha(transformation, alpha);
        invalidate();
    }

    public CGAffineTransform getTransform() {
        return transformation == null ? CGAffineTransform.identity() : transformation.getTransformation();
    }

    public void setTransform(CGAffineTransform transform) {
        transformation = RichTransformation.setTransform(transformation, transform);
        invalidate();
    }

    /** 
     * This method is a call back for animations, so that they can set the parameters,
     * to be ready to redraw when the animation finishes.
     * This method is called just when commiting animations, before any animation is performed.
     * No invalidate() is nessesary, since an animation is pending.
     * @param params 
     */
    public void setTransformationParameters(Parameters params) {
        transformation = RichTransformation.setParameters(params);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int howmany = getChildCount();
        for (int i = 0; i < howmany; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                IOSView.LayoutParams ipar = (IOSView.LayoutParams) child.getLayoutParams();
                child.layout(ipar.x, ipar.y - deltaY, ipar.x + ipar.width, ipar.y - deltaY + ipar.height);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();

        int maxHeight = 0;
        int maxWidth = 0;

        // Find out how big everyone wants to be
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        // Find rightmost and bottom-most child
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                int childRight;
                int childBottom;

                IOSView.LayoutParams lp = (IOSView.LayoutParams) child.getLayoutParams();

                childRight = lp.x + child.getMeasuredWidth();
                childBottom = lp.y + child.getMeasuredHeight();

                maxWidth = Math.max(Math.max(maxWidth, childRight), lp.width);
                maxHeight = Math.max(Math.max(maxHeight, childBottom), lp.height);
            }
        }

        // Check against minimum height and width
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        setMeasuredDimension(resolveSize(maxWidth, widthMeasureSpec),
                resolveSize(maxHeight, heightMeasureSpec));
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new IOSView.LayoutParams(CGRect.Zero());
    }

    public IOSView getIOSView() {
        return this;
    }

    @Override
    protected boolean getChildStaticTransformation(View child, Transformation t) {
        if (!(child instanceof IOSView))
            return super.getChildStaticTransformation(child, t);

        IOSView view = (IOSView) child;
        if (view.transformation == null)
            return false;
        else {
            t.set(view.transformation);
            return true;
        }
    }

    public void updateStatusBarDelta() {
        // Use iphone metrics and not android (i.e. androidBar),
        // so that half-visible items would display correctly
        deltaY = UIApplication.sharedApplication().isStatusBarHidden() ? 0 : xIOS(STATUSBAR_HEIGHT);
    }

    public static class LayoutParams extends AbsListView.LayoutParams {

        public int x;
        public int y;

        public LayoutParams(CGRect frame) {
            this(xIOS(frame.origin.x), yIOS(frame.origin.y), xIOS(frame.size.width), yIOS(frame.size.height));
        }

        public LayoutParams(CGSize size) {
            this(0, 0, xIOS(size.width), yIOS(size.height));
        }

        private LayoutParams(int x, int y, int width, int height) {
            super(width, height);
            this.x = x;
            this.y = y;
        }

        public void updateParams(Rect rect) {
            this.x = rect.left;
            this.y = rect.top;
            this.width = rect.right - rect.left;
            this.height = rect.bottom - rect.top;
        }

        public CGRect getFrame() {
            return new CGRect(xAndroid(x), yAndroid(y), xAndroid(width), yAndroid(height));
        }

        @Override
        public String toString() {
            return "[x=" + x + " y=" + y + " width=" + width + " height=" + height + "]";
        }
    }

    public static int xIOS(float iosmetric) {
        return (int) (iosmetric * xratio + 0.5);

    }

    public static int yIOS(float iosmetric) {
        return (int) (iosmetric * yratio + 0.5);
    }

    public static float xAndroid(int ametric) {
        return ametric / xratio;

    }

    public static float yAndroid(int ametric) {
        return ametric / yratio;
    }
}
