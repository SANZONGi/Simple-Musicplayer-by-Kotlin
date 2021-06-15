package com.example.musicplayer_xjj

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import okhttp3.*
import java.io.*
import java.util.concurrent.TimeUnit


class DownLoadMusic_XJJ {
    @SuppressLint("SdCardPath")
    fun downLoad(url: String, name: String,path:String,context: Context){
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
            val file = File("${path}${name}.mp3")

            try {
                fos = FileOutputStream(file)
                fos.write(inputStream?.readBytes())          //这里写成read错了无数次
                fos.flush();
                fos.close()
                val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                val uri = Uri.fromFile(File("${path}${name}.mp3"));
                intent.data = uri
                context.sendBroadcast(intent);
                Log.d("pathname", "${path}${name}.mp3")
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }.start()
    }
}