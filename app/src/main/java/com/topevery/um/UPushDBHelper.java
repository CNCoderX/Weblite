package com.topevery.um;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author wujie
 */
public class UPushDBHelper extends SQLiteOpenHelper {
    private static UPushDBHelper instance = null;

    public static UPushDBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new UPushDBHelper(context);
        }
        return instance;
    }

    public UPushDBHelper(Context context) {
        super(context, "upush.db", null, BuildConfig.VERSION_CODE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE UMENG_MSG(" +
                "ID TEXT PRIMARY KEY NOT NULL, " +
                "MESSAGE TEXT NOT NULL, " +
                "TIME TEXT NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
