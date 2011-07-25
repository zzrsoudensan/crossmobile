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

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class Parameters {

    private float fromalpha = 1;
    private float toalpha = 1;

    public Animation createAnimation() {
        return new AlphaAnimation(fromalpha, toalpha);
    }

    public void setAlpha(float from, float to) {
        if (to >= 1 || to < 0)
            to = 1;
        this.fromalpha = from;
        this.toalpha = to;
    }

    public float getAlpha() {
        return toalpha;
    }
}
