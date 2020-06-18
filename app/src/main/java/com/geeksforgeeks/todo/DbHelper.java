package com.geeksforgeeks.todo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    public DbHelper(Context context) {
        super(context, TodoContract.DB_NAME, null, TodoContract.DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TodoContract.TaskEntry.TABLE + " ( " +
                TodoContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TodoContract.TaskEntry.COL_TASK_TITLE + " TEXT NOT NULL , done TEXT );";
        sqLiteDatabase.execSQL(query);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TodoContract.TaskEntry.TABLE);
        onCreate(sqLiteDatabase);
    }
}
