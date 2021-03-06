package com.example.musicplayer_xjj

import android.Manifest
import android.R.attr.path
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_list_view.*


class ListView_XJJ : AppCompatActivity() {
    var songlist = ArrayList<Song>()
    var fav_list = ArrayList<Song>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_view)
        val dbHelper  =MyDatabaseHelper(this,"fav.db",1)
        dbHelper.writableDatabase
        getpermission()
        Toast.makeText(this, "长按编辑或下载在线歌曲", Toast.LENGTH_LONG).show()
        val fav_song = application as MyData
        val layoutManager = LinearLayoutManager(this)
        if (this.intent.extras == null) {
            getSongs()
        } else {
            songlist = this.intent.getParcelableArrayListExtra<Song>("song_ser") as ArrayList<Song>
//            Log.d("songlist", songlist.size.toString())
        }
        rec_song.layoutManager = layoutManager
        rec_song.adapter = SongAdapter_Xjj(songlist, fav_song.getPath())
        //设置在线和本地切换
        online_music.setOnClickListener {
            val intent = Intent(this, OnlineMusic_XJJ::class.java)
            songlist.clear()
            startActivity(intent)
        }
        local_music.setOnClickListener {
            getSongs()
            rec_song.adapter = SongAdapter_Xjj(songlist, fav_song.getPath())
        }
        favourite_music.setOnClickListener {
            fav_list.clear()
            val dbHelper = MyDatabaseHelper(this,"fav.db",1)
            val db = dbHelper.writableDatabase
            val cursor =db.query("fav",null,null,null,null,null,null)
            if (cursor.moveToFirst()){
                do {
                    val name = cursor.getString(cursor.getColumnIndex("name"))
                    for (s in songlist)
                    {
                        if (s.name .equals(name))
                            fav_list.add(s)
                    }
                }while (cursor.moveToNext())
            }
            cursor.close()
            rec_song.adapter = SongAdapter_Xjj(fav_song.getAllSongs(), fav_song.getPath())
        }
    }

    private fun getSongs() {
        songlist.clear()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection =
            MediaStore.Audio.Media.IS_MUSIC + "!= 0 AND " + MediaStore.Audio.Media.DURATION +" >= 1000"
        val sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        val cursor = contentResolver.query(
            uri,
            null,
            selection,
            null,
            sortOrder
        )
        if (cursor != null && cursor.moveToNext()) {
            val id = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val name = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val duration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            val path = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            do {
                val song = Song(
                    cursor.getLong(id),
                    cursor.getString(name),
                    cursor.getLong(duration),
                    cursor.getString(
                        path
                    ),
                    cursor.getString(artist)
                )
                songlist.add(song)
//                val path_song:MyData = application as MyData
//                path_song.setPath(song.path.substring(0,song.path.lastIndexOf('/'))+'/')
//                Log.d("song", path_song.getPath())
            } while (cursor.moveToNext())
            cursor.close()
        }
    }

    private fun getpermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 1
            )

        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1
            )
        }
    }


}