package com.example.appfinalproject_10130492

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appfinalproject_10130492.data.Assignment
import java.util.*
import kotlin.collections.ArrayList

class RecyclerAdapter(val itemData: ArrayList<Assignment>) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val name: TextView
        val desc: TextView
        val courseTxt: TextView
        val timeTxt: TextView
        val statusIco: ImageView
        val linear: LinearLayout


        init{
            name = view.findViewById(R.id.name)
            desc = view.findViewById(R.id.description)
            courseTxt = view.findViewById(R.id.courseText)
            timeTxt = view.findViewById(R.id.timeText)
            statusIco = view.findViewById(R.id.statusIcon)
            linear = view.findViewById(R.id.linear)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_list,parent,false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: Assignment = itemData.get(position)
        holder.name.text = data.title
        holder.courseTxt.text = data.courseName
        val date = Date(data.dueDate)
        val uDate = java.text.SimpleDateFormat("yyyy-MM-dd  aa hh:mm", Locale.TAIWAN)
        holder.timeTxt.text = uDate.format(date)
        holder.desc.text = data.note


    }

    override fun getItemCount(): Int {
        return itemData.size
    }
}