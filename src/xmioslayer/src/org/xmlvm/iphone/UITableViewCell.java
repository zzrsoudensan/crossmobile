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

public class UITableViewCell extends UIView {

    private final String reuseIdentifier;
    //
    boolean selected = false;
    private UIView accessoryView;
    private UILabel textlabel;

    public UITableViewCell() {
        this(UITableViewCellStyle.Default, null);
    }

    public UITableViewCell(int UITableViewCellStyle, String reuseIdentifier) {
        super(CGRect.Zero());
        setBackgroundColor(UIColor.clearColor);
        accessoryView = null;
        this.reuseIdentifier = reuseIdentifier;
    }

    @Override
    public void setFrame(CGRect frame) {
        super.setFrame(frame);
        updateInnerChilds();
    }

    public void setSelected(boolean sel) {
        selected = sel;
    }

    public boolean isSelected() {
        return selected;
    }

    public int getEditingStyle() {
        return UITableViewCellEditingStyle.None;
    }

    public UIView getContentView() {
        return this;
    }

    public void setBackgroundView(UIView backgroundView) {
        throw new ImplementationError();
    }

    public UIView getBackgroundView() {
        throw new ImplementationError();
    }

    public void setSelectedBackgroundView(UIView selectedBackgroundView) {
        throw new ImplementationError();
    }

    public UIView getSelectedBackgroundView() {
        throw new ImplementationError();
    }

    public UILabel getTextLabel() {
        if (textlabel == null) {
            textlabel = new UILabel();
            addSubview(textlabel);
            updateInnerChilds();
        }
        return textlabel;
    }

    public UILabel getDetailTextLabel() {
        throw new ImplementationError();
    }

    public UIImageView getImageView() {
        throw new ImplementationError();
    }

    public UIView getAccessoryView() {
        return accessoryView;
    }

    public void setAccessoryView(UIView accessoryView) {
        this.accessoryView = accessoryView;
    }

    public String getReuseIdentifier() {
        return reuseIdentifier;
    }

    private void updateInnerChilds() {
        CGRect frame = getFrame();
        frame.origin.x = 0;
        frame.origin.y = 0;
        if (textlabel != null)
            textlabel.setFrame(frame);
    }
}
