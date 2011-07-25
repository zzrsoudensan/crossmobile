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

import android.graphics.Matrix;

public class CGAffineTransform extends NSObject {

    private Matrix matrix;

    private CGAffineTransform(Matrix transform) {
        this.matrix = transform;
    }

    public static CGAffineTransform make(float a, float b, float c, float d, float tx, float ty) {
        Matrix m = new Matrix();
        m.setValues(new float[]{a, b, 0, c, d, 0, tx, ty, 1});
        return new CGAffineTransform(m);
    }

    public static CGAffineTransform makeRotation(float alpha) {
        Matrix m = new Matrix();
        m.setRotate(alpha);
        return new CGAffineTransform(m);
    }

    public static CGAffineTransform makeScale(float sx, float sy) {
        Matrix m = new Matrix();
        m.setScale(sx, sy);
        return new CGAffineTransform(m);
    }

    public static CGAffineTransform makeTranslation(float tx, float ty) {
        Matrix m = new Matrix();
        m.setTranslate(tx, ty);
        return new CGAffineTransform(m);
    }

    public static CGAffineTransform rotate(CGAffineTransform transf, float alpha) {
        Matrix old = new Matrix(transf.matrix);
        old.setRotate(alpha);
        return new CGAffineTransform(old);
    }

    public static CGAffineTransform scale(CGAffineTransform transf, float sx, float sy) {
        Matrix old = new Matrix(transf.matrix);
        old.setScale(sx, sy);
        return new CGAffineTransform(old);
    }

    public static CGAffineTransform translate(CGAffineTransform transf, float tx, float ty) {
        Matrix old = new Matrix(transf.matrix);
        old.setTranslate(tx, ty);
        return new CGAffineTransform(old);
    }

    public static CGAffineTransform concat(CGAffineTransform transf1, CGAffineTransform transf2) {
        Matrix m = new Matrix();
        m.setConcat(transf1.matrix, transf2.matrix);
        return new CGAffineTransform(m);
    }

    // since someone might change what is returned here, it is safer to always create a new object
    public static CGAffineTransform identity() {
        return new CGAffineTransform(new Matrix());
    }

    @Override
    public String toString() {
        return matrix.toShortString();
    }
}
