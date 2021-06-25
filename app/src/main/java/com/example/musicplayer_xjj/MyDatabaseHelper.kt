package com.example.musicplayer_xjj

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class MyDatabaseHelper (val context: Context, name:String, version:Int):
    SQLiteOpenHelper(context,name,null,version){
    private val createBase = "create table fav("+
            "id integer primary key autoincrement," +
            "name text)"

    override fun onCreate(db: SQLiteDatabase?) {
        if (db != null) {
            db.execSQL(createBase)
        }
        Toast.makeText(context,"CreateSuccess", Toast.LENGTH_SHORT).show()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
}