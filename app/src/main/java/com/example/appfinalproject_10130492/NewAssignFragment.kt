package com.example.appfinalproject_10130492

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import com.example.appfinalproject_10130492.data.Assignment
import com.example.appfinalproject_10130492.databases.AssignmentsDB
import com.example.appfinalproject_10130492.databases.CoursesDB
import com.example.appfinalproject_10130492.databinding.FragmentSecondNewAssignBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class NewAssignFragment : Fragment() {

    private var _binding: FragmentSecondNewAssignBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var fromDateTextView: TextView
    private lateinit var toDateTextView: TextView
    private lateinit var name: TextInputLayout
    private lateinit var note: TextInputLayout
    private lateinit var courseName: AutoCompleteTextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondNewAssignBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fromDateTextView = view.findViewById(R.id.fromDateText)
        toDateTextView = view.findViewById(R.id.toDateText)
        name = view.findViewById(R.id.add_assign_name)
        note = view.findViewById(R.id.add_assign_notes)
        courseName = view.findViewById(R.id.course_sel_menu)
        var fromCalendarDate = Calendar.getInstance()
        var toCalendarDate = Calendar.getInstance()
        val course = CoursesDB(this.context)
        val assignDB = AssignmentsDB(this.context)

        //get courses data
        var coursesArr= readCourses(course)



        if(coursesArr[0]!=null) {
            val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_list_item, coursesArr)
            (binding.courseSelTextField.editText as? MaterialAutoCompleteTextView)?.setAdapter(
                adapter
            )
        }




        //init text
        fromDateTextView.text = "${fromCalendarDate.get(Calendar.YEAR)}年${fromCalendarDate.get(Calendar.MONTH)+1}月${fromCalendarDate.get(Calendar.DATE)}日  ${fromCalendarDate.get(Calendar.HOUR_OF_DAY)}時${fromCalendarDate.get(Calendar.MINUTE)}分"
        toDateTextView.text = "${toCalendarDate.get(Calendar.YEAR)}年${toCalendarDate.get(Calendar.MONTH)+1}月${toCalendarDate.get(Calendar.DATE)}日  ${toCalendarDate.get(Calendar.HOUR_OF_DAY)}時${toCalendarDate.get(Calendar.MINUTE)}分"


        fromCalendarDate.timeZone = TimeZone.getDefault()
        binding.courseSelMenu.setOnClickListener {
            if(coursesArr[0] == null) {
                val alert = AlertDialog.Builder(requireContext())
                alert.setTitle(R.string.alert)
                alert.setMessage(R.string.alert_msg)
                alert.setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
                alert.create().show()

            }

        }
        binding.datepick.setOnClickListener{
            fromCalendarDate = datePickHelper(fromCalendarDate,"from")
            fromDateTextView.text = "${fromCalendarDate.get(Calendar.YEAR)}年${fromCalendarDate.get(Calendar.MONTH)+1}月${fromCalendarDate.get(Calendar.DATE)}日  ${fromCalendarDate.get(Calendar.HOUR_OF_DAY)}時${fromCalendarDate.get(Calendar.MINUTE)}分"
        }
        binding.datepick2.setOnClickListener{
            toCalendarDate = datePickHelper(toCalendarDate,"to")
            toDateTextView.text = "${toCalendarDate.get(Calendar.YEAR)}年${toCalendarDate.get(Calendar.MONTH)+1}月${toCalendarDate.get(Calendar.DATE)}日  ${toCalendarDate.get(Calendar.HOUR_OF_DAY)}時${toCalendarDate.get(Calendar.MINUTE)}分"

        }

        binding.save.setOnClickListener{
            val nameString = name.editText!!.text.toString()
            if(nameString.isEmpty()){
                val alert = AlertDialog.Builder(requireContext())
                alert.setTitle(R.string.alert)
                alert.setMessage(R.string.alert_msg_required)
                alert.setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
                alert.create().show()
                return@setOnClickListener
            }
            val assignment = Assignment(null, nameString,fromCalendarDate.timeInMillis,toCalendarDate.timeInMillis,courseName.text.toString(),
                note.editText!!.text.toString())
            assignDB.insert(assignment)
            val intent = Intent()
            activity?.setResult(RESULT_OK)
            // APP p74
            activity?.finish()



        }
    }

    fun datePickHelper(calendarDate: Calendar,type: String): Calendar{
        val datepicker = MaterialDatePicker.Builder.datePicker().setTitleText(R.string.date_pick).setSelection(Date(calendarDate.timeInMillis).time).build()
        datepicker.show(parentFragmentManager,"tag")
        datepicker.addOnPositiveButtonClickListener {
            Log.i("Date","Positive")
            Log.i("Date",""+datepicker.selection)
            // DatePicker Selection Result
            var dateSel:Long = datepicker.selection!!

            //Set Calendar Time
            calendarDate.time = Date(dateSel)
            val timepicker = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_12H).setHour(calendarDate.get(Calendar.HOUR_OF_DAY)).setMinute(calendarDate.get(Calendar.MINUTE)).setTitleText(R.string.date_pick).build()
            timepicker.show(parentFragmentManager,"tag")
            timepicker.addOnPositiveButtonClickListener{
                calendarDate.set(Calendar.HOUR_OF_DAY,timepicker.hour)
                calendarDate.set(Calendar.MINUTE,timepicker.minute)
                Log.i("Date",""+timepicker.hour)
                if(type == "from"){
                    fromDateTextView.text = "${calendarDate.get(Calendar.YEAR)}年${calendarDate.get(Calendar.MONTH)+1}月${calendarDate.get(Calendar.DATE)}日  ${calendarDate.get(Calendar.HOUR_OF_DAY)}時${calendarDate.get(Calendar.MINUTE)}分"
                }else if(type == "to"){
                    toDateTextView.text = "${calendarDate.get(Calendar.YEAR)}年${calendarDate.get(Calendar.MONTH)+1}月${calendarDate.get(Calendar.DATE)}日  ${calendarDate.get(Calendar.HOUR_OF_DAY)}時${calendarDate.get(Calendar.MINUTE)}分"

                }

            }
        }
        return calendarDate
    }
    private fun readCourses(db:CoursesDB):Array<String?>{
        val cursor = db.readAllCursor()
        val courseArr = arrayOfNulls<String>(cursor.columnCount)
        if (cursor!=null && cursor.moveToFirst())
            for(i in 0 until cursor.columnCount){
                // _id note title assignedDate dueDate courseName
                val courseName = cursor.getString(0)
                courseArr[i] = courseName
            }

        return courseArr
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}