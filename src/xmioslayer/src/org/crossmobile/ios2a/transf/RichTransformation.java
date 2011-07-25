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

import android.graphics.Matrix;
import android.view.animation.Transformation;
import java.lang.reflect.Field;
import org.xmlvm.iphone.CGAffineTransform;

public class RichTransformation extends Transformation {

    private static final Field matrixF;

    static {
        Field fm = null;
        try {
            fm = CGAffineTransform.class.getDeclaredField("matrix");
            fm.setAccessible(true);
        } catch (Exception ex) {
        }
        matrixF = fm;
    }

    public static RichTransformation setAlpha(RichTransformation old, float alpha) {
        if (alpha < 0)
            alpha = 0;
        if (alpha >= 1)
            if (old == null)
                return null;
            else if (old.mMatrix.isIdentity())
                return null;
            else {
                old.mAlpha = 1;
                return old;
            }
        else {
            if (old == null)
                old = new RichTransformation();
            old.mAlpha = alpha;
            return old;
        }
    }

    public static RichTransformation setTransform(RichTransformation old, CGAffineTransform cgtrans) {
        Matrix newmatrix;
        if (cgtrans == null)
            newmatrix = new Matrix();
        else
            newmatrix = new Matrix(getMatrix(cgtrans));
        if (newmatrix.isIdentity())
            if (old == null)
                return null;
            else if (old.mAlpha >= 1)
                return null;
            else {
                old.mMatrix = newmatrix;
                return old;
            }
        else {
            if (old == null)
                old = new RichTransformation();
            old.mMatrix = newmatrix;
            return old;
        }
    }

    public static RichTransformation setParameters(Parameters params) {
        return setAlpha(null, params.getAlpha());
    }

    public CGAffineTransform getTransformation() {
        CGAffineTransform transf = CGAffineTransform.identity();
        setMatrix(transf, mMatrix);
        return transf;
    }

    private static void setMatrix(CGAffineTransform transf, Matrix m) {
        try {
            matrixF.set(transf, m);
        } catch (Exception e) {
        }
    }

    private static Matrix getMatrix(CGAffineTransform transf) {
        try {
            return (Matrix) matrixF.get(transf);
        } catch (Exception e) {
            return null;
        }
    }
}