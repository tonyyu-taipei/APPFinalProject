package com.example.appfinalproject_10130492

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    val item : ArrayList<AssignmentItem> = ArrayList()
    init {
        val template = AssignmentItem("統計學作業","第一題、第二題",1669809847768,1669852800000,"統計學")
        val template2 = AssignmentItem("App作業","完成SQLite",1669809847768,1669852800000,"App開發")
        item.add(template)
        item.add(template2)
        item.add(template)
        item.add(template2)
        item.add(template2)
        item.add(template2)
        item.add(template2)
        item.add(template2)
        item.add(template2)
        item.add(template2)
        item.add(template2)
        item.add(template2)
        item.add(template2)
        item.add(template2)
        item.add(template2)
        item.add(template2)
        item.add(template2)
        item.add(template2)
        item.add(template2)
    }
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

        val data: AssignmentItem = item.get(position)
        holder.name.setText(data.name)
        holder.courseTxt.setText(data.courseName)
        val date = java.util.Date(data.endTime)
        val uDate = java.text.SimpleDateFormat("yyyy-MM-dd  aa hh:mm", Locale.TAIWAN)
        holder.timeTxt.setText(uDate.format(date))
        holder.desc.setText(data.description)

    }

    override fun getItemCount(): Int {
        return item.size
    }
}