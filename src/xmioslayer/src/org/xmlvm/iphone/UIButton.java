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

import org.crossmobile.ios2a.FileBridge;
import org.crossmobile.ios2a.IOSView;
import org.crossmobile.ios2a.UIRunner;
import static org.xmlvm.iphone.UIButtonType.*;
import static org.xmlvm.iphone.UIControlState.*;

public class UIButton extends UIControl {

    private static final UIFont BUTTONFONT = UIFont.boldSystemFontOfSize(UIFont.buttonFontSize());
    //
    private final int buttonType;
    private ButtonStates states = new ButtonStates();
    private UILabel title;
    private UIImageView fore;
    private boolean imagefills = true;

    public static UIButton buttonWithType(int UIButtonType) {
        UIButton result;
        if (UIButtonType != RoundedRect) {
            result = new UIButton(UIButtonType);
            UIImage img = null;
            switch (UIButtonType) {
                case DetailDisclosure:
                    img = UIImage.imageWithContentsOfFile(FileBridge.RESOURCEPREFIX + "detaildisclosure");
                    break;
                case ContactAdd:
                    img = UIImage.imageWithContentsOfFile(FileBridge.RESOURCEPREFIX + "contactadd");
                    break;
                case InfoDark:
                    img = UIImage.imageWithContentsOfFile(FileBridge.RESOURCEPREFIX + "infodark");
                    break;
                case InfoLight:
                    img = UIImage.imageWithContentsOfFile(FileBridge.RESOURCEPREFIX + "infolight");
                    break;
            }
            result.setImage(img, Normal);
        } else
            result = new UIRoundRectButton();
        return result;
    }

    UIButton(int UIButtonType) {
        super();
        this.buttonType = UIButtonType;
    }

    public int getButtonType() {
        return buttonType;
    }

    @Override
    public void setFrame(CGRect frame) {
        super.setFrame(frame);
        frame.origin.x = 0;
        frame.origin.y = 0;
        if (title != null)
            title.setFrame(frame);
        if (fore != null)
            fore.setFrame(frame);
    }

    public void setFont(UIFont font) {
        initText();
        title.setFont(font);
    }

    public UIFont getFont() {
        initText();
        return title.getFont();
    }

    public void setTitle(String title, int UIControlState) {
        initText();
        states.setTitle(UIControlState, title);
        updateText(UIControlState);
    }

    public String titleForState(int UIControlState) {
        return states.getTitle(UIControlState);
    }

    public String getCurrentTitle() {
        return titleForState(Normal);
    }

    public void setTitleColor(UIColor titleColor, int UIControlState) {
        states.setTitlecolor(UIControlState, titleColor);
        updateText(UIControlState);
    }

    public UIColor titleColorForState(int UIControlState) {
        return states.getTitlecolor(UIControlState);
    }

    public UIColor getCurrentTitleColor() {
        return titleColorForState(UIControlState.Normal);
    }

    public void setTitleShadowColor(UIColor shadowcolor, int UIControlState) {
        states.setShadowColor(UIControlState, shadowcolor);
        updateText(UIControlState);
    }

    public UIColor titleShadowColorForState(int UIControlState) {
        return states.getShadowColor(UIControlState);
    }

    public UIColor getCurrentTitleShadowColor() {
        return titleShadowColorForState(Normal);
    }

    public void setTitleShadowOffset(CGSize titleShadowOffset) {
        initText();
        title.setShadowOffset(titleShadowOffset);
    }

    public CGSize getTitleShadowOffset() {
        initText();
        return title.getShadowOffset();
    }

    public void setImage(UIImage img, int UIControlState) {
        states.setFore(UIControlState, img);
        updateImage(UIControlState);
    }

    public UIImage imageForState(int UIControlState) {
        return states.getFore(UIControlState);
    }

    public UIImage getCurrentImage() {
        return imageForState(Normal);
    }

    public void setBackgroundImage(UIImage img, int UIControlState) {
        states.setBack(UIControlState, img);
        updateImage(UIControlState);
    }

    public UIImage backgroundImageForState(int UIControlState) {
        return states.getBack(UIControlState);
    }

    public UIImage getCurrentBackgroundImage() {
        return backgroundImageForState(Normal);
    }

    @Override
    public void setHighlighted(boolean highlighted) {
        super.setHighlighted(highlighted);
        int state = highlighted ? Highlighted : isSelected() ? Selected : Normal;
        states.setState(state);
        updateText(state);
        updateImage(state);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        int state = selected ? Selected : Normal;
        states.setState(state);
        updateText(state);
        updateImage(state);
    }

    public boolean isAdjustsImageWhenDisabled() {
        return states.adjustsImageWhenDisabled;
    }

    public void setAdjustsImageWhenDisabled(boolean adjustsImageWhenDisabled) {
        states.adjustsImageWhenDisabled = adjustsImageWhenDisabled;
    }

    public boolean isAdjustsImageWhenHighlighted() {
        return states.adjustsImageWhenHighlighted;
    }

    public void setAdjustsImageWhenHighlighted(boolean adjustsImageWhenHighlighted) {
        states.adjustsImageWhenHighlighted = adjustsImageWhenHighlighted;
    }

    public boolean isShowsTouchWhenHighlighted() {
        return states.showsTouchWhenHighlighted;
    }

    public void setShowsTouchWhenHighlighted(boolean showsTouchWhenHighlighted) {
        states.showsTouchWhenHighlighted = showsTouchWhenHighlighted;
    }

    private void initText() {
        if (title == null) {
            title = new UILabel();
            title.setBackgroundColor(UIColor.clearColor);
            title.setTextAlignment(UITextAlignment.Center);
            title.setFont(BUTTONFONT);
            CGSize size = getFrame().size;
            title.setFrame(new CGRect(0, 0, size.width, size.height));

            addSubview(title);
            if (fore != null) {   // Fix Z-order
                fore.removeFromSuperview();
                addSubview(fore);
            }
        }
    }

    private void initForeground() {
        if (fore == null) {
            fore = new UIImageView();
            if (imagefills)
                fore.setContentMode(UIViewContentMode.ScaleToFill);
            else
                fore.setContentMode(UIViewContentMode.Center);
            CGSize size = getFrame().size;
            fore.setFrame(new CGRect(0, 0, size.width, size.height));
            addSubview(fore);
        }
    }

    void updateText(int state) {
        if (!states.isInState(state))
            return;
        if (title != null) {
            title.setText(states.getTitle());
            if (states.getTitlecolor() != null)
                title.setTextColor(states.getTitlecolor());
            if (states.getShadowColor() != null)
                title.setShadowColor(states.getShadowColor());
        }
    }

    void updateImage(int state) {
        if (!states.isInState(state))
            return;
        UIRunner.runSynced(new UIRunner() {

            @Override
            public void exec() {
                UIImage foreimg = states.getFore();
                if (foreimg == null && fore != null) {
                    fore.removeFromSuperview();
                    fore = null;
                } else if (foreimg != null) {
                    initForeground();
                    fore.setImage(foreimg);
                }

                UIImage backimg = states.getBack();
                if (backimg != null)
                    __model().setBackgroundDrawable(backimg.getModel());
                else
                    __model().setBackgroundDrawable(null);
            }
        });
    }

    float getPrefferedWidth() {
        __model().measure(0, 0);
        int max = __model().getMeasuredWidth();
        if (title != null) {
            title.__model().measure(0, 0);
            max = Math.max(max, title.__model().getMeasuredWidth() + 16);
        }
        if (fore != null) {
            fore.__model().measure(0, 0);
            max = Math.max(max, fore.__model().getMeasuredWidth());
        }
        return IOSView.xAndroid(max);
    }

    void setImageFillsArea(boolean imagefills) {
        this.imagefills = imagefills;
    }
}