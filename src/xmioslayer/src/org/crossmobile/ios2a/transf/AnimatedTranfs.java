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
package org.crossmobile.ios2a.transf;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Animation;
import java.util.HashMap;
import org.xmlvm.iphone.CGAffineTransform;
import org.xmlvm.iphone.CGRect;
import org.xmlvm.iphone.UIViewAnimationDelegate;
import org.crossmobile.ios2a.IOSView;

public class AnimatedTranfs extends StaticTranfs implements Animation.AnimationListener {

    private String ID;
    private HashMap<IOSView, Parameters> map = new HashMap<IOSView, Parameters>();
    private long duration = 300;
    private UIViewAnimationDelegate delegate;

    AnimatedTranfs(String ID) {
        this.ID = ID;
    }

    @Override
    public void setFrame(IOSView view, CGRect frame) {
        if (!areAnimationsEnabled()) {
            super.setFrame(view, frame);
            return;
        }
        //    AnimationSet set = getSet(view);
        super.setFrame(view, frame);
    }

    @Override
    public void setBackgroundColor(View view, Drawable back, int color) {
        if (!areAnimationsEnabled()) {
            super.setBackgroundColor(view, back, color);
            return;
        }
        super.setBackgroundColor(view, back, color);
    }

    @Override
    public void setAlpha(IOSView view, float to) {
        if (!areAnimationsEnabled()) {
            super.setAlpha(view, to);
            return;
        }
        // Transformation parameters are stored in IOSView later, during "commit"
        getParameters(view).setAlpha(view.getAlpha(), to);
    }

    @Override
    public void setTransform(IOSView view, CGAffineTransform transform) {
        if (!areAnimationsEnabled()) {
            super.setTransform(view, transform);
            return;
        }
        super.setTransform(view, transform);
    }

    @Override
    public void setDuration(double d) {
        this.duration = (long) (d * 1000);
    }

    @Override
    public void commit() {
        Parameters params = null;
        Animation anim = null;
        for (IOSView view : map.keySet()) {
            params = map.get(view);
            view.setTransformationParameters(params);
            anim = params.createAnimation();
            anim.setDuration(duration);
            view.startAnimation(anim);
        }
        if (anim != null // There is indeed an animation
                && delegate != null)    // and there is a delegate
            anim.setAnimationListener(this);
    }

    @Override
    public void setAnimationDelegate(UIViewAnimationDelegate delegate) {
        this.delegate = delegate;
    }

    private Parameters getParameters(IOSView view) {
        Parameters transf = map.get(view);
        if (transf != null)
            return transf;
        transf = new Parameters();
        map.put(view, transf);
        return transf;
    }

    public void onAnimationStart(Animation anmtn) {
        if (delegate != null)
            delegate.animationWillStart(ID);
    }

    public void onAnimationEnd(Animation anmtn) {
        if (delegate != null)
            delegate.animationDidStop(ID, anmtn.hasEnded());
    }

    public void onAnimationRepeat(Animation anmtn) {
    }
}