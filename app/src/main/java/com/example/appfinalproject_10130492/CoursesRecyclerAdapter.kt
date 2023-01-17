package com.example.appfinalproject_10130492

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.appfinalproject_10130492.data.AssignmentsWithStatus
import com.example.appfinalproject_10130492.data.Course
import com.example.appfinalproject_10130492.databases.AssignmentsDB
import com.example.appfinalproject_10130492.databases.CoursesDB
import java.util.*
import kotlin.collections.ArrayList


class CoursesRecyclerAdapter(val courses:ArrayList<Course>, val coursesDB: CoursesDB, val assignDB: AssignmentsDB, val viewParent: View, val context: Context?, val fragmentManager: FragmentManager) : RecyclerView.Adapter<CoursesRecyclerAdapter.ViewHolder>(), ItemTouchHelperAdapter {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val courseTitle: TextView
        val teacher: TextView
        val lateText: TextView
        val queuedText: TextView
        val finishedText: TextView
        val linear: LinearLayout


        init{
            courseTitle = view.findViewById(R.id.courses_sec_titie)
            teacher = view.findViewById(R.id.courses_sec_teacher)
            lateText = view.findViewById(R.id.courses_sec_late)
            queuedText = view.findViewById(R.id.courses_sec_queued)
            finishedText = view.findViewById(R.id.courses_sec_finished)
            linear = view.findViewById(R.id.linear)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.courses_recyclerview_list,parent,false)

        return ViewHolder(view)
    }


    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val courseData = courses[position]
        holder.courseTitle.text = courseData.courseName
        holder.teacher.text = courseData.teacher ?: ""

        val res = assignDB.readAllByStatus(courseData.courseName)

        Log.i("SQL", res.toString())
        holder.finishedText.text = res.finished.size.toString()
        holder.queuedText.text = res.queued.size.toString()
        holder.lateText.text = res.late.size.toString()
        //TODO: Convert assignment sec to courses sec
        holder.linear.setOnClickListener{
            MainActivity.scrollFab(true)
            FirstFragment.modeOn = true
            FirstFragment.courseSpecific = courseData.courseName
            viewParent.findNavController().navigate(R.id.action_coursesFirstFragment_to_firstFragment)
        }
        holder.linear.setOnLongClickListener {
            val newCoursesDialog = NewCoursesDialog()
            newCoursesDialog.setEditCourse(courseData)
            newCoursesDialog.show(fragmentManager,null)
            fun reloadSpecificItem(){
                notifyItemChanged(position)
            }
            newCoursesDialog.setDialogDestroyListener(
                object:NewCoursesDialog.DialogInterface{
                    override fun onDestroyListener() {

                        reloadSpecificItem()
                    }

                }
            )
            true
        }


    }

    override fun getItemCount(): Int {
        return courses.size
    }

    override fun onItemMove(fromPos: Int, toPos: Int) {
        // No need to move the item
    }

    override fun onItemDismiss(pos: Int) {

        if(courses.size != 0) {
            val coursesData = courses[pos]
            val alertDialog = AlertDialog.Builder(context)
            alertDialog.setTitle(R.string.alert)
                .setMessage(R.string.confirm_del_courses)
                .setPositiveButton(R.string.confirm,){_,_->
                    assignDB.deleteByCourse(coursesData.courseName)
                    coursesData.courseName?.let { coursesDB.deleteOne(it) }
                    courses.removeAt(pos)
                    notifyItemRemoved(pos)
                }.setNegativeButton(R.string.cancel){i,_->
                    i.dismiss()
                    this.notifyDataSetChanged()
                }.create().show()

            //notifyItemRangeChanged(pos, itemData.size);
        }

    }


}