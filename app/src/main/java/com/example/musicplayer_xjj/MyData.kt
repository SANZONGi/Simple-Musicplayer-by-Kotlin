package com.example.musicplayer_xjj

import android.app.Application
import java.text.FieldPosition

class MyData : Application() {
    private var Stored_Songs = ArrayList<Song>()
    private var sing_path = "sdcard/Music/"
    public fun addSongs(s:Song)
    {
        var flag = 0
        for ( ss in  Stored_Songs)
        {
            if (ss.name . equals(s.name))
                flag = 1
        }
        if (flag == 0)
        Stored_Songs.add(s)
    }
    fun getPath() = sing_path
    fun setPath(path:String) {
        sing_path = path
    }

    public fun getSong(position:Int) = Stored_Songs[position]
    public fun getAllSongs() = Stored_Songs
    public fun removeSong(pos:Int)
    {
        Stored_Songs.removeAt(pos)
    }
}