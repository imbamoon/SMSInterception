package com.example.smsinterception;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zyw on 2015/11/21.
 */
public class BlackList extends SQLiteOpenHelper {
    public BlackList(Context context) {

        super(context, "BlackList", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE blackList(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "address TEXT DEFAULT NONE," +
                "date TEXT DEFAULT NONE," +
                "body TEXT DEFAULT NONE)");

        db.execSQL("CREATE TABLE wordList(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "word TEXT UNIQUE)");

        db.execSQL("CREATE TABLE numberList(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "number TEXT UNIQUE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
