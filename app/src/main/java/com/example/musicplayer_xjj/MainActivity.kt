package com.example.musicplayer_xjj

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private var songlist = ArrayList<Song>()
    private val mPlayer: MediaPlayer = MediaPlayer()
//    private var fav_songlist = ArrayList<Song>()
    private var posAtfav = -1
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val favourite_song:MyData = application as MyData
        songlist = this.intent.getParcelableArrayListExtra<Song>("song_ser") as ArrayList<Song>
        val bundle = this.intent.extras
        var position = -1
        if (bundle != null) {
            position = bundle.getInt("pos")
        }
        setSeekBar()
        var modeflag = 0
        initPlayer(position)
        mPlayer.setOnCompletionListener {
            if (modeflag != 2) {
                position = (position - 1 + songlist.size)%songlist.size

            }
            initPlayer(position)
        }

        previous.setOnClickListener {
            if (modeflag != 2) {
                position = (position - 1 + songlist.size)%songlist.size

            }
            initPlayer(position)
        }
        next.setOnClickListener {
            if (modeflag != 2) {
                position = (position + 1)%songlist.size

            }
            initPlayer(position)
        }
        mode.setOnClickListener {
            modeflag = (modeflag + 1) % 3
            when(modeflag)
            {
                0 -> { mode.text = "顺序播放"
                    mPlayer.isLooping = false}
                1 -> { mode.text = "随机播放"
                    songlist.shuffle()
                    mPlayer.isLooping = false
                }
                2 -> { mode.text = "单曲循环"
                mPlayer.isLooping = true
                }
            }
        }
        favourite.setOnClickListener{
            if ("加入收藏".equals(favourite.text))
                favourite_song.addSongs(songlist[position])
            else if(posAtfav != -1)
                favourite_song.removeSong(posAtfav)
        }

        //后台播放
        quit_with_music.setOnClickListener {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
    }


    private fun FormmatDur(dur: Long): String {
        return String.format(Locale.ENGLISH, "%02d:%02d", dur / 1000 / 60, dur / 1000 % 60)
    }


    @Synchronized fun initProcess() {
        Thread {
            try {
                while (!Thread.interrupted()) {
                    Thread.sleep(250)
                    runOnUiThread {
                        seekBar.progress = mPlayer.currentPosition
                        song_process.text = FormmatDur(mPlayer.currentPosition.toLong())
                    }
                }
            } catch (e: InterruptedException) {
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "processUpdater has been interrupt, the position of playing may update no longer.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.start()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initOneSong(song: Song,pos:Int) {
        val favourite_song:MyData = application as MyData
        for (i in (0 until favourite_song.getAllSongs().size))
        {
            if (songlist[pos].name == favourite_song.getAllSongs()[i].name && songlist[pos].duration == favourite_song.getAllSongs()[i].duration)
            {
                favourite.text = "移除收藏夹"
                posAtfav = i
            }
        }
        song_title.text = song.name
        song_artist.text = song.artists
        song_duration.text = FormmatDur(song.duration)
        song_process.text = FormmatDur(0)
        seekBar.min = 0
        seekBar.max = song.duration.toInt()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initPlayer(position: Int) {
        val song: Song = songlist[position]
        play.text = "暂停"
        Log.d("song_path",song.path)
        initOneSong(song,position)
        initProcess()
        try {
            mPlayer.reset()
            mPlayer.setDataSource(song.path)
            mPlayer.prepare()
            mPlayer.start()
            song_duration.text = FormmatDur(mPlayer.duration.toLong())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    private fun setPlayerProgress(process: Int) {
        mPlayer.seekTo(process)
        song_process.text = FormmatDur(process.toLong())
    }

    private fun setSeekBar() {
        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {                 //否则将导致卡顿
                    setPlayerProgress(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    fun PlayOrPause(view: View) {
        if (mPlayer.isPlaying) {
            mPlayer.pause()
            play.text = "播放"
        } else {
            mPlayer.start()
            play.text = "暂停"
        }
    }

    // 第一种
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder(this)
                .setMessage("确定退出系统吗？")
                .setNegativeButton("回到列表",
                    DialogInterface.OnClickListener { dialog, which ->
                        mPlayer.stop()
                        val intent = Intent()
                        val bundle = Bundle()
                        bundle.putParcelableArrayList("song_ser", songlist as ArrayList<out Parcelable>)
                        intent.putExtras(bundle)
                        intent.setClass(this, ListView_XJJ::class.java)
                        startActivity(intent)
                    })
                .setPositiveButton("确定",
                    DialogInterface.OnClickListener { dialog, whichButton ->
                        mPlayer.stop()
                        mPlayer.release()
                        finish()
                    }).show()
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }
}