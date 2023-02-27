package com.example.appfinalproject_10130492

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.appfinalproject_10130492.data.Assignment
import com.example.appfinalproject_10130492.databases.AssignmentsDB
import com.example.appfinalproject_10130492.databases.SettingDB
import com.example.appfinalproject_10130492.databinding.FragmentSecondBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.joda.time.*
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 * MainSecondFragment
 * A fragment to view the detailed information for the assignment.
 */
class MainSecondFragment : Fragment() {

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
    private  lateinit var linDay: LinearLayout
    private lateinit var hour: TextView
    private  lateinit var linHour: LinearLayout
    private lateinit var sec: TextView
    private  lateinit var linSec: LinearLayout
    private lateinit var min: TextView
    private  lateinit var linMin: LinearLayout
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
        if(isInited()) {
            assignmentBody = assignmentBody.id?.let { assignmentDB.read(it) }!!
            assignmentBody.dueDate * 1000
            assignmentBody.assignedDate * 1000
        }
        updateUI()


    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        statusTxt = view.findViewById(R.id.sec_status_text)
        statusIco = view.findViewById(R.id.sec_status_ico)
        name = view.findViewById(R.id.sec_name)
        courseName = view.findViewById(R.id.share_course_name)
        progress = view.findViewById(R.id.progressBar)
        note = view.findViewById(R.id.sec_notes)
        dueDate = view.findViewById(R.id.share_date)
        hour = view.findViewById(R.id.sec_hour)
        day = view.findViewById(R.id.sec_day)
        sec = view.findViewById(R.id.sec_second)
        min = view.findViewById(R.id.sec_min)
        linMin = view.findViewById(R.id.sec_lin_min)
        linSec = view.findViewById(R.id.sec_lin_sec)
        linHour = view.findViewById(R.id.sec_lin_hour)
        linDay = view.findViewById(R.id.sec_lin_day)
        finishedButt = view.findViewById(R.id.sec_finish_butt)
        if(!isInited()){
            name.text = "錯誤：資料未正確載入"
            return
        }
        val showNotificationPrompt: CardView = view.findViewById(R.id.notification_prompt)
        if(this.context?.let { ActivityCompat.checkSelfPermission(it,Manifest.permission.POST_NOTIFICATIONS) } != PackageManager.PERMISSION_GRANTED
            && activity?.let { ActivityCompat.shouldShowRequestPermissionRationale(it,Manifest.permission.POST_NOTIFICATIONS) } == true
        ){
            showNotificationPrompt.visibility = View.VISIBLE
            val notificationBtn:Button = view.findViewById(R.id.open_notification_button)
            notificationBtn.setOnClickListener {
                activity?.let { it1 -> ActivityCompat.requestPermissions(it1,arrayOf(Manifest.permission.POST_NOTIFICATIONS),0) }
                showNotificationPrompt.visibility = View.INVISIBLE

            }
        }

        /*

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }*/
    }

    fun updateUI(){
        //reset the texts to 0
        day.text = "00"
        hour.text = "00"
        min.text = "00"
        sec.text = "00"

        // set fab to editMode
        fab = activity?.findViewById(R.id.fab1)!!
        fab.setImageResource(R.drawable.edit_48px)
        // set MainActivity to accept the Assignment provided
        // it'll make AssignmentsModifyActivity turn into edit mode
        if(isInited()) {
            MainActivity.assignment = assignmentBody
            MainActivity.editModeToggle(true)

            name.text = assignmentBody.title
            courseName.text = assignmentBody.courseName
            note.text = assignmentBody.note
        }



        val date = Date(assignmentBody.dueDate)
        val uDate = java.text.SimpleDateFormat("yyyy-MM-dd  aa hh:mm", Locale.TAIWAN)
        dueDate.text = uDate.format(date)

        val assignedCal = Calendar.getInstance()
        assignedCal.time = Date(assignmentBody.assignedDate)

        val dueCal = Calendar.getInstance()
        dueCal.time = Date(assignmentBody.dueDate)


        progress.max = 100

        val settingDB = SettingDB(context);
        val setting = settingDB.read()
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
                        day.text = String.format("%02d",days)
                        hour.text = String.format("%02d",hours)
                        min.text = String.format("%02d",mins)
                        sec.text = String.format("%02d",secs)
                        if(days > 0) {
                            linDay.visibility = View.VISIBLE
                            linHour.visibility = View.VISIBLE
                            linMin.visibility = View.VISIBLE
                            linSec.visibility = View.VISIBLE
                        }
                        if(hours > 0 && days == 0) {
                            linDay.visibility = View.GONE
                            linHour.visibility = View.VISIBLE
                            linMin.visibility = View.VISIBLE
                            linSec.visibility = View.VISIBLE
                        }
                        if(mins > 0 && hours == 0 && days == 0 ){
                            linDay.visibility = View.GONE
                            linHour.visibility = View.GONE
                            linMin.visibility = View.VISIBLE
                            linSec.visibility = View.VISIBLE
                        }
                        if(secs > 0 && mins == 0 && hours == 0 && days == 0 ){
                            linDay.visibility = View.GONE
                            linHour.visibility = View.GONE
                            linMin.visibility = View.GONE
                            linSec.visibility = View.VISIBLE
                        }


                    }


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

            val alarmService = AlarmService(requireContext())
            assignmentBody.finished = if(assignmentBody.finished == 1 ) 0 else 1
            finishedButt.text = if(assignmentBody.finished == 1){
                alarmService.cancelSpecificAlarm(assignmentBody)
                getString(R.string.unfinish)
            } else {
                alarmService.setAlarm(assignmentBody)
                getString(R.string.finished)
            }

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
        Log.i("Fragment","MainSecondFragment Destroyed")
        timer.cancel()
        forceFabAdd?.onChange()
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
        var forceFabAdd: ForceChangeFabToAddListener? = null
    }
}