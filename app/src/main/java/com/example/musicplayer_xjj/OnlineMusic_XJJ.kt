package com.example.musicplayer_xjj

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Parcelable
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_online_music.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit


class OnlineMusic_XJJ : AppCompatActivity() {
    private var mainHanlder: Handler = Handler()
    val songlist = ArrayList<Song>()
    private var key_word = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_music)
        mainHanlder = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    60 -> {
                        //更新一首歌曲
                        val music: Song = msg.obj as Song
                        songlist.add(music)
                    }
                }
            }
        }
        var flag = 0
        search_name.setOnClickListener(){
            flag = 0
            start_search.text = "查询"
        }
        start_search.setOnClickListener {
            key_word = search_name.text.toString().trim()
            getOlineMusic()
            if (flag == 0) {
                flag++
                start_search.text = "跳转"
                Toast.makeText(this,"查询成功",Toast.LENGTH_SHORT).show()
            }
            else if (flag == 1) {
                val intent = Intent(this, ListView_XJJ::class.java)
                val bundle = Bundle()
                bundle.putParcelableArrayList("song_ser", songlist as ArrayList<out Parcelable>)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
    }

    private fun getOlineMusic() {
        val client = OkHttpClient.Builder()
            .connectTimeout(300, TimeUnit.SECONDS) //设置连接超时时间
            .readTimeout(300, TimeUnit.SECONDS) //设置读取超时时间
            .build()
        val request = Request.Builder()
            .url("http://pd.musicapp.migu.cn/MIGUM3.0/v1.0/content/search_all.do?ua=Android_migu&version=5.0.1&text=${key_word}&pageNo=1&pageSize=3&searchSwitch={\"song\":1,\"album\":0,\"singer\":0,\"songlist\":0,\"bestShow\":0}")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val result = response.body()?.string()
                try {
                    val obj = JSONObject(result)
                    val s = JSONObject(obj.optString("songResultData")).optString("result")
                    val songs = JSONArray(s)
                    for (i in 0 until songs.length()) {
                        val song = songs.getJSONObject(i)
                        val id = song.getString("contentId")
                        val songurl =
                            "http://app.pd.nf.migu.cn/MIGUM2.0/v1.0/content/sub/listenSong.do?toneFlag=HQ&netType=00&userId=15548614588710179085069&ua=Android_migu&version=5.1&copyrightId=0&contentId=${id}&resourceType=2&channel=0"
                        val name = song.getString("name")
                        val singer =
                            JSONObject((JSONArray(song.optString("singers")).get(0)).toString()).optString(
                                "name"
                            )

                        //实例化一首音乐并发送到主线程更新
                        val music = Song(id.toLong(), name, 0, songurl, singer)
                        val message: Message = mainHanlder.obtainMessage()
                        message.what = 60
                        message.obj = music
                        mainHanlder.sendMessage(message)
                        Thread.sleep(20)
                    }
                } catch (e: Exception) {
                }
            }
        })
    }

    // 返回按钮
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}