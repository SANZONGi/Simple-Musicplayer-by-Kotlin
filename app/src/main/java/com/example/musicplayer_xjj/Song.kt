package com.example.musicplayer_xjj

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Song(val id: Long,val name:String,val duration : Long,val path:String,val artists:String):Parcelable{
    override fun toString(): String {
        return "Song(id=$id, name='$name', duration=$duration, path='$path', artists='$artists')"
    }
}