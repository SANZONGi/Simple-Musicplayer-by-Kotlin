package com.example.musicplayer_xjj

import android.app.AlertDialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.contentValuesOf
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.ArrayList

class SongAdapter_Xjj(val SongList: ArrayList<Song>,val path:String) :
    RecyclerView.Adapter<SongAdapter_Xjj.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.song_name)
        val artists: TextView = view.findViewById(R.id.song_artists)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            val intent = Intent()
            val bundle = Bundle()
            bundle.putParcelableArrayList("song_ser", SongList as ArrayList<out Parcelable>)
            bundle.putInt("pos", position)
            intent.putExtras(bundle)
            intent.setClass(parent.context, MainActivity::class.java)
            parent.context.startActivity(intent)
        }

        //长按事件
        viewHolder.itemView.setOnLongClickListener {
            val position = viewHolder.adapterPosition
//            SongList.removeAt(position)
//            notifyItemRemoved(position)
//            notifyItemRangeChanged(position,itemCount)
            AlertDialog.Builder(parent.context)
                .setMessage("请选择")
                .setNegativeButton("从表中删除",
                    DialogInterface.OnClickListener { dialog, which ->
                        SongList.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, itemCount)
                    })
                .setPositiveButton("下载",
                    DialogInterface.OnClickListener { dialog, whichButton ->
                        Log.d("pre",SongList[position].path.substring(0,4))
                        if (SongList[position].path.substring(0,4) != "http")
                        {
                            Toast.makeText(parent.context,"本地音乐无法下载",Toast.LENGTH_SHORT).show()

                        }else {
                            val download = DownLoadMusic_XJJ()
                            download.downLoad(SongList[position].path, SongList[position].name,path,parent.context)

//                            Thread.sleep(6000)
                            Toast.makeText(parent.context,"下载成功",Toast.LENGTH_SHORT).show()
                        }
                    }).show()
            true
        }
        return viewHolder
    }

    override fun getItemCount(): Int = SongList.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = SongList[position]
        holder.name.text = item.name
        holder.artists.text = item.artists
    }
}