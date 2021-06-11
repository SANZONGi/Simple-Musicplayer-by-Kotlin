package com.example.musicplayer_xjj

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.util.Log
import okhttp3.*
import java.io.*
import java.util.concurrent.TimeUnit

class DownLoadMusic_XJJ {
    @SuppressLint("SdCardPath")
    fun downLoad(url: String, name: String) {
        Thread {
            val client = OkHttpClient.Builder()
                .connectTimeout(3000, TimeUnit.SECONDS) //设置连接超时时间
                .readTimeout(3000, TimeUnit.SECONDS) //设置读取超时时间
                .build()
            val request = Request.Builder()
                .url(url)
                .build()
            val response = client.newCall(request).execute()
            val inputStream = response.body()?.byteStream()
            val fos: FileOutputStream
            val file = File("/sdcard/Music/${name}.mp3")
            Log.d("pathname", "/sdcard/Music/${name}.mp3")
            try {
                fos = FileOutputStream(file)
                fos.write(inputStream?.readBytes())          //这里写成read错了无数次
                fos.flush();
                fos.close()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }.start()
    }
}