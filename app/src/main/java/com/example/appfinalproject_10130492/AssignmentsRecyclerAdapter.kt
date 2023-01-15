package com.example.appfinalproject_10130492

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.NavGraph
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.appfinalproject_10130492.data.Assignment
import com.example.appfinalproject_10130492.databases.AssignmentsDB
import com.example.appfinalproject_10130492.databases.NotificationsDB
import java.util.*


class AssignmentsRecyclerAdapter(val itemData: ArrayList<Assignment>, val dbHelper: AssignmentsDB, val viewParent: View, val context: Context?) : RecyclerView.Adapter<AssignmentsRecyclerAdapter.ViewHolder>(), ItemTouchHelperAdapter {
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.assignments_recyclerview_list,parent,false)

        return ViewHolder(view)
    }


    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: Assignment = itemData[position]
        holder.name.text = data.title
        holder.courseTxt.text = data.courseName
        val date = Date(data.dueDate)
        val uDate = java.text.SimpleDateFormat("yyyy-MM-dd  aa hh:mm", Locale.TAIWAN)

        val dateCalendar = Calendar.getInstance()
        dateCalendar.time = Date(data.dueDate)

        if(dateIsLate(dateCalendar) && data.finished ==0){
            holder.statusIco.setImageResource(R.drawable.error_48px)
            holder.statusIco.imageTintList = ColorStateList.valueOf(Color.parseColor(context?.getString(R.color.late)))

        }
        else if(data.finished == 1){
            holder.statusIco.setImageResource(R.drawable.check_circle_48px)
        }
        else{
            holder.statusIco.setImageResource(R.drawable.alarm_on_48px)
            holder.statusIco.imageTintList = ColorStateList.valueOf(Color.parseColor(context?.getString(R.color.blue)))
        }


        holder.timeTxt.text = uDate.format(date)
        holder.desc.text = data.note
        holder.linear.setOnClickListener{
            MainActivity.scrollFab(true)
            SecondFragment.assignmentBody = data
            val graph = viewParent.findNavController().graph
            when(graph.id){
                R.id.nav_graph->
                    viewParent.findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                R.id.nav_graph3->
                    viewParent.findNavController().navigate(R.id.action_firstFragment_to_SecondFragment_courses)
            }


        }
        Log.i("Menu","Adapter Data: $itemData")


    }

    override fun getItemCount(): Int {
        return itemData.size
    }

    override fun onItemMove(fromPos: Int, toPos: Int) {
        // No need to move the item
    }

    override fun onItemDismiss(pos: Int) {
        Log.i("Menu", "Position is at $pos")
        Log.i("Menu","The adapter data is \r\n $itemData")
        if(itemData.size != 0) {
            val assignment = itemData[pos]
            assignment.id?.let { dbHelper.deleteOne(it) }
            val notificationsDB = context?.let { NotificationsDB(it) }
            assignment.id?.let { notificationsDB?.deleteOne(it) }
            val alarmService = context?.let { AlarmService(it) }
            alarmService?.cancelSpecificAlarm(assignment)
            itemData.removeAt(pos)
            notifyItemRemoved(pos)
            //notifyItemRangeChanged(pos, itemData.size);
        }

    }
    private fun dateIsLate(calendar: Calendar): Boolean{
        val date = calendar.timeInMillis
        val dateNow = Date().time

        return date <= dateNow
    }
}