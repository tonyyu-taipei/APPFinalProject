package com.example.appfinalproject_10130492

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.appfinalproject_10130492.data.Assignment
import com.example.appfinalproject_10130492.databinding.FragmentSecondBinding
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
    private lateinit var name: TextView
    private lateinit var courseName: TextView
    private lateinit var progress: ProgressBar
    private lateinit var note: TextView
    private lateinit var dueDate: TextView
    private lateinit var day: TextView
    private lateinit var hour: TextView
    private lateinit var sec: TextView
    private lateinit var min: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        name = view.findViewById(R.id.sec_name)
        courseName = view.findViewById(R.id.sec_course_name)
        progress = view.findViewById(R.id.progressBar)
        note = view.findViewById(R.id.sec_notes)
        dueDate = view.findViewById(R.id.duedate_sec)
        hour = view.findViewById(R.id.sec_hour)
        day = view.findViewById(R.id.sec_day)
        sec = view.findViewById(R.id.sec_second)
        min = view.findViewById(R.id.sec_min)

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


        Timer().scheduleAtFixedRate(object: TimerTask(){
            override fun run() {

            progress.progress = percentageCalc(assignedCal,dueCal)
            Log.i("Menu","目前百分比：${percentageCalc(assignedCal,dueCal)}")

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
            if(progress.progress > 90){
                progress.progressTintList = ColorStateList.valueOf(Color.rgb(181, 5, 5))
            }
            }

        },0,1000)

        /*

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }*/
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_assign_page,menu)
        super.onCreateOptionsMenu(menu, inflater)

    }
    override fun onDestroyView() {
        super.onDestroyView()
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
        lateinit var assignmentBody: Assignment
    }
}