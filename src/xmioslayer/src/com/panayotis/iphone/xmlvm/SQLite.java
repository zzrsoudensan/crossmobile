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
package com.panayotis.iphone.xmlvm;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import org.crossmobile.ios2a.MainActivity;
import org.crossmobile.ios2a.FileBridge;

public class SQLite {

    private DataBaseHelper helper;

    public SQLite(String database, boolean writable) {
        // Find various filenames
        String canonical = database.replace('/', '_');
        String inname = FileBridge.BUNDLEPREFIX + "/" + database;
        String outname = MainActivity.current.getDatabasePath(canonical).getAbsolutePath();
        File outfile = new File(outname);

        // Extract database, if not exists
        if (!outfile.exists()) {
            outfile.getParentFile().mkdirs();
            try {
                FileBridge.copyStreams(FileBridge.getInputFileStream(inname), new FileOutputStream(outname));
            } catch (FileNotFoundException ex) {
            }
        }
        helper = new DataBaseHelper(canonical, writable);
    }

    public int query(String query) {
        return helper.query(query);
    }

    public int lastInsertID() {
        return helper.lastInsertID();
    }

    public ArrayList<String> fetchRow() {
        return helper.fetchRow();
    }

    public void interrupt() {
        helper.interrupt();
    }

    private static class DataBaseHelper extends SQLiteOpenHelper {

        private Cursor cursor;
        private final boolean writable;

        public DataBaseHelper(String name, boolean writable) {
            super(MainActivity.current, name, null, 1);
            this.writable = writable;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

        private int query(String query) {
            closeCursor();
            try {
                if (query.trim().toUpperCase().startsWith("SELECT")) {
                    cursor = getReadableDatabase().rawQuery(query, null);
                    if (cursor != null && (!cursor.moveToFirst()))
                        closeCursor();
                    return cursor == null ? SQLiteResult.ERROR : SQLiteResult.OK;
                } else {
                    getBase().execSQL(query);
                    return SQLiteResult.OK;
                }
            } catch (Exception e) {
                return SQLiteResult.ERROR;
            }
        }

        private int lastInsertID() {
            int result = -1;
            Cursor lc = getWritableDatabase().rawQuery("SELECT last_insert_rowid();", null);
            if (lc != null && lc.getColumnCount() > 0) {
                lc.moveToFirst();
                result = lc.getInt(0);
            }
            lc.close();
            return result;
        }

        private ArrayList<String> fetchRow() {
            if (cursor == null)
                return null;
            int count = cursor.getColumnCount();
            ArrayList<String> res = new ArrayList<String>(count);
            for (int i = 0; i < count; i++)
                res.add(cursor.getString(i));
            if (!cursor.moveToNext())
                closeCursor();
            return res;
        }

        private void interrupt() {
            System.err.println("SQLite interrupt is not supported");
        }

        private SQLiteDatabase getBase() {
            return writable ? getWritableDatabase() : getReadableDatabase();
        }

        public void closeCursor() {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }
}
