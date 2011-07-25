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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import org.crossmobile.ios2a.FileBridge;

public class UIImage extends NSObject {

    private static HashMap<String, WeakReference<Bitmap>> cache = new HashMap<String, WeakReference<Bitmap>>();
    //
    private BitmapDrawable image;

    private UIImage(byte[] data) {
        this(data == null ? (Bitmap) null : BitmapFactory.decodeByteArray(data, 0, data.length));
    }

    private UIImage(Bitmap bitmap) {
        this(bitmap == null ? (BitmapDrawable) null : new BitmapDrawable(bitmap));
    }

    UIImage(BitmapDrawable image) {
        this.image = image;
    }

    public static UIImage imageWithContentsOfFile(String filename) {
        // Check in cache
        WeakReference<Bitmap> wimg = cache.get(filename);
        if (wimg != null && wimg.get() != null)
            return new UIImage(wimg.get());

        // Load image
        Bitmap bm = FileBridge.loadBitmap(filename);

        // Bitmap not found
        if (bm == null)
            return null;

        // store image in cache
        cache.put(filename, new WeakReference<Bitmap>(bm));
        return new UIImage(bm);
    }

    public static UIImage imageNamed(String filename) {
        return imageWithContentsOfFile(FileBridge.BUNDLEPREFIX + "/" + filename);
    }

    public static UIImage imageWithData(NSData data) {
        UIImage img = new UIImage(data.getBytes());
        return img.image != null ? img : null;
    }

    public UIImage stretchableImage(int leftCapWidth, int topCapHeight) {
        // TODO REQ: stretchableImage
        return this;
    }

    public CGImage getCGImage() {
        return new CGImage(image.getBitmap());
    }

    public void drawInRect(CGRect rect) {
        throw new ImplementationError();
    }

    public void drawAtPoint(CGPoint point) {
        throw new ImplementationError();
    }

    public CGSize getSize() {
        if (image == null)
            return new CGSize(0, 0);
        Bitmap bm = image.getBitmap();
        return new CGSize(bm.getWidth(), bm.getHeight());
    }

    public UIImage cropImage(int x, int y, int width, int height) {
        throw new ImplementationError();
    }

    public NSData PNGRepresentation() {
        throw new ImplementationError();
    }

    public NSData JPEGRepresentation(float compressionQuality) {
        throw new ImplementationError();
    }

    BitmapDrawable getModel() {
        return image;
    }
}
