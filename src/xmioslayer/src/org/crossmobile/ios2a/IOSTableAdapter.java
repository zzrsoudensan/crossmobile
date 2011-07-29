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

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import org.xmlvm.iphone.CGRect;
import org.xmlvm.iphone.NSIndexPath;
import org.xmlvm.iphone.UITableView;
import org.xmlvm.iphone.UITableViewCell;
import org.xmlvm.iphone.UITableViewDataSource;

public class IOSTableAdapter implements ListAdapter {

    private static final int CELLTYPES;

    static {
        int types = 20;
        try {
            types = Integer.parseInt(System.getProperty("ios.tablecells"));
        } catch (Exception e) {
        }
        CELLTYPES = types;
    }
    private final DataSetObservable mDataSetObservable = new DataSetObservable();
    private final UITableView tv;
    private final HashMap<Integer, String> registry = new HashMap<Integer, String>();
    private final ArrayList<String> reuselist = new ArrayList<String>();
    private int lastusedid = -1;
    public View offeredCellView;

    public IOSTableAdapter(UITableView tv) {
        this.tv = tv;
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);

    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    public int getCount() {
        UITableViewDataSource ds = tv.getDataSource();
        return ds == null ? 0 : ds.numberOfRowsInSection(tv, 1);
    }

    public Object getItem(int i) {
        return null;
    }

    public long getItemId(int i) {
        return i;
    }

    public boolean hasStableIds() {
        return false;
    }

    public View getView(int i, final View view, ViewGroup vg) {
        UITableViewDataSource ds = tv.getDataSource();
        if (ds == null)
            return null;
        UITableViewCell iview = null;
        NSIndexPath path = new NSIndexPath();
        path.setRow(i);

        synchronized (tv) {
            offeredCellView = view;
            iview = ds.cellForRowAtIndexPath(tv, path);
            offeredCellView = null;
        }
        if (iview == null)
            return null;

        /* Find cell height */
        float height = tv.getRowHeight();
        try {
            height = tv.getTableViewDelegate().heightForRowAtIndexPath(tv, path);
        } catch (OptionalSelectorException ex) {
        }
        iview.setFrame(new CGRect(0, 0, 320, height));

        registry.remove(i);
        String idreuse = iview.getReuseIdentifier();
        if (idreuse != null) {
            registry.put(i, idreuse);
            if (!reuselist.contains(idreuse))
                reuselist.add(idreuse);
        }
        return iview.xm_base();
    }

    public int getItemViewType(int i) {
        int newid = reuselist.indexOf(registry.get(i));
        if (newid < 0)
            newid = lastusedid;
        lastusedid = newid;
        return newid;
    }

    public int getViewTypeCount() {
        return CELLTYPES;
    }

    public boolean isEmpty() {
        return getCount() < 1;
    }

    public boolean areAllItemsEnabled() {
        return true;
    }

    public boolean isEnabled(int i) {
        return true;
    }
}
