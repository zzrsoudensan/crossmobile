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

import org.crossmobile.ios2a.MainActivity;

public class UIWindow extends UIView {

    private UIViewController rootViewController;

    public UIWindow() {
        this(CGRect.Zero());
    }

    public UIWindow(CGRect frame) {
        super(frame);
    }

    @Override
    public void addSubview(UIView subView) {
        if (subView.controller != null)
            setRootViewController(subView.controller);
        super.addSubview(subView);
    }

    public void setRootViewController(UIViewController controller) {
        for (UIView child : getSubviews())
            child.removeFromSuperview();
        this.rootViewController = controller;
    }

    public UIViewController getRootViewController() {
        return rootViewController;
    }

    public CGPoint convertPointToWindow(CGPoint point, UIWindow window) {
        // TODO : Java implementation
        return null;
    }

    public CGPoint convertPointFromWindow(CGPoint point, UIWindow window) {
        // TODO : Java implementation
        return null;
    }

    public CGRect convertRectToWindow(CGRect point, UIWindow window) {
        // TODO : Java implementation
        return null;
    }

    public CGRect convertRectFromWindow(CGRect point, UIWindow window) {
        // TODO : Java implementation
        return null;
    }

    public void sendEvent(UIEvent event) {
        // TODO : Java implementation
    }

    public void makeKeyAndVisible() {
        if (UIApplication.sharedApplication().getKeyWindow() == this)
            return;
        UIApplication.sharedApplication().setKeyWindow(this);
    }

    @Override
    public void willRemoveSubview(UIView subview) {
        if (subview == null)
            return;
        subview.willMoveToWindow(null);
        if (subview.controller != null && UIApplication.sharedApplication().getKeyWindow() == this)
            subview.controller.viewWillDisappear(true);
    }

    @Override
    void willAddSubview(UIView subview) {
        if (subview == null)
            return;
        subview.willMoveToWindow(this);
        if (subview.controller != null && UIApplication.sharedApplication().getKeyWindow() == this)
            subview.controller.viewWillAppear(true);
    }

    @Override
    public void didAddSubview(UIView subview) {
        if (subview == null)
            return;
        subview.didMoveToWindow();
        if (subview.controller != null && UIApplication.sharedApplication().getKeyWindow() == this)
            subview.controller.viewDidAppear(true);
    }

    @Override
    void didRemoveSubview(UIView subview) {
        if (subview == null)
            return;
        subview.didMoveToWindow();
        if (subview.controller != null && UIApplication.sharedApplication().getKeyWindow() == this)
            subview.controller.viewDidDisappear(true);
    }

    @Override
    void changeParent(UIView newParent, int index) {
    }

    void doLayoutWithDelegates() {
        updateStatusBarDelta();
        if (rootViewController != null) {
            if (rootViewController.getView().getSuperview() != this)
                super.addSubview(rootViewController.getView());
            rootViewController.doLayoutWithDelegates(false);
            rootViewController.viewWillAppear(false);
        }
        MainActivity.current.setContentView(__base());
        if (rootViewController != null)
            rootViewController.viewDidAppear(false);
    }

    void updateStatusBarDelta() {
        __base().updateStatusBarDelta();
    }
}
