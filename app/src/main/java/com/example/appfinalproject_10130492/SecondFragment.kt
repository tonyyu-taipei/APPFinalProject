package com.example.appfinalproject_10130492

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.appfinalproject_10130492.data.Assignment
import com.example.appfinalproject_10130492.databases.AssignmentsDB
import com.example.appfinalproject_10130492.databases.SettingDB
import com.example.appfinalproject_10130492.databinding.FragmentSecondBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.joda.time.*
import org.w3c.dom.Text
import java.time.LocalDateTime
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var statusIco: ImageView
    private lateinit var statusTxt: TextView
    private lateinit var name: TextView
    private lateinit var courseName: TextView
    private lateinit var progress: ProgressBar
    private lateinit var note: TextView
    private lateinit var dueDate: TextView
    private lateinit var day: TextView
    private lateinit var hour: TextView
    private lateinit var sec: TextView
    private lateinit var min: TextView
    private lateinit var fab: FloatingActionButton
    private lateinit var finishedButt: Button
    private lateinit var assignmentDB: AssignmentsDB
    var timer = Timer()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    @SuppressLint("ResourceType")
    override fun onResume() {
        super.onResume()
        timer.cancel()
        timer = Timer()
        Log.i("Time","Resumed")
        MainActivity.editModeToggle(true)
       assignmentDB = AssignmentsDB(this.context)
       assignmentBody = assignmentBody.id?.let { assignmentDB.read(it) }!!
        assignmentBody.dueDate*1000
        assignmentBody.assignedDate *1000
        updateUI()


    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        statusTxt = view.findViewById(R.id.sec_status_text)
        statusIco = view.findViewById(R.id.sec_status_ico)
        name = view.findViewById(R.id.sec_name)
        courseName = view.findViewById(R.id.sec_course_name)
        progress = view.findViewById(R.id.progressBar)
        note = view.findViewById(R.id.sec_notes)
        dueDate = view.findViewById(R.id.duedate_sec)
        hour = view.findViewById(R.id.sec_hour)
        day = view.findViewById(R.id.sec_day)
        sec = view.findViewById(R.id.sec_second)
        min = view.findViewById(R.id.sec_min)
        finishedButt = view.findViewById(R.id.sec_finish_butt)



        /*

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }*/
    }

    fun updateUI(){
        //reset the texts to 0
        day.text = "0"
        hour.text = "0"
        min.text = "0"
        sec.text = "0"

        // set fab to editMode
        fab = activity?.findViewById(R.id.fab1)!!
        fab.setImageResource(R.drawable.edit_48px)
        // set MainActivity to accept the Assignment provided
        // it'll make AddActivity turn into edit mode
        MainActivity.assigment = assignmentBody
        MainActivity.editModeToggle(true)

        name.text = assignmentBody.title
        courseName.text = assignmentBody.courseName
        note.text = assignmentBody.note



        val date = Date(assignmentBody.dueDate)
        val uDate = java.text.SimpleDateFormat("yyyy-MM-dd  aa hh:mm", Locale.TAIWAN)
        dueDate.text = uDate.format(date)

        val assignedCal = Calendar.getInstance()
        assignedCal.time = Date(assignmentBody.assignedDate)

        val dueCal = Calendar.getInstance()
        dueCal.time = Date(assignmentBody.dueDate)


        progress.max = 100


        timer.scheduleAtFixedRate(object: TimerTask(){
            override fun run() {
                activity!!.runOnUiThread {

                    progress.progress = percentageCalc(assignedCal,dueCal)
                    if(progress.progress < 100) {
                        val days: Int = Days.daysBetween(
                            DateTime(),
                            DateTime(assignmentBody.dueDate)
                        ).days
                        val hours: Int = Hours.hoursBetween(
                            DateTime(), DateTime(
                                assignmentBody.dueDate
                            )
                        ).hours - days * 24
                        val mins: Int = Minutes.minutesBetween(DateTime(), DateTime(
                            assignmentBody.dueDate)).minutes - days*24*60 -hours * 60
                        val secs: Int = Seconds.secondsBetween(
                            DateTime(), DateTime(
                                assignmentBody.dueDate
                            )
                        ).seconds - days * 24*60*60 - hours * 60*60 - mins * 60
                        day.text = "$days"
                        hour.text = "$hours"
                        min.text = "$mins"
                        sec.text = "$secs"
                    }
                    val settingDB = SettingDB(requireContext());
                    val setting = settingDB.read()
                    if(progress.progress > setting.duePercentage){
                        progress.progressTintList = ColorStateList.valueOf(Color.parseColor(getString(R.color.late)))
                    }else{
                        progress.progressTintList = ColorStateList.valueOf(Color.parseColor(getString(R.color.blue)))
                    }
                }
                }

        },0,1000)

        fun statusHelper() {
            val assignedCal = Calendar.getInstance()
            assignedCal.time = Date(assignmentBody.assignedDate)

            val dueCal = Calendar.getInstance()
            dueCal.time = Date(assignmentBody.dueDate)


            if (percentageCalc(assignedCal, dueCal) >= 100) {
                statusTxt.text = getString(R.string.late)
                statusIco.imageTintList = ColorStateList.valueOf(Color.rgb(181, 5, 5))
                statusIco.setImageResource(R.drawable.error_48px)
            } else {
                statusTxt.text = getString(R.string.queued)
                statusIco.setImageResource(R.drawable.alarm_on_48px)
                statusIco.imageTintList =
                    ColorStateList.valueOf(Color.parseColor(context?.getString(R.color.blue)))
            }
            if (assignmentBody.finished == 1) {
                statusTxt.text = getString(R.string.done)
                statusIco.setImageResource(R.drawable.check_circle_48px)
                finishedButt.text = getString(R.string.unfinish)
                statusIco.imageTintList = ColorStateList.valueOf(Color.parseColor(getString(R.color.blue)))
            }
        }
        statusHelper()

        finishedButt.setOnClickListener {
            assignmentDB = AssignmentsDB(this.context)


            assignmentBody.finished = if(assignmentBody.finished == 1 ) 0 else 1
            finishedButt.text = if(assignmentBody.finished == 1)getString(R.string.unfinish) else getString(R.string.finished)

            assignmentDB.update(assignmentBody)
            statusHelper()

        }
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_assign_page,menu)
        super.onCreateOptionsMenu(menu, inflater)

    }
    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("Fragment","SecondFragment Destroyed")
        timer.cancel()
        _binding = null
    }

    fun percentageCalc(calendarFrom: Calendar, calendarDue: Calendar): Int{
        val from = calendarFrom.timeInMillis
        val to = calendarDue.timeInMillis
        val now = Date().time
        val res:Int =  (((now-from).toFloat() / (to-from).toFloat())* 100).toInt()
        return res
    }

    companion object{
        fun isInited(): Boolean{
            return this::assignmentBody.isInitialized
        }
        lateinit var assignmentBody: Assignment
    }
}