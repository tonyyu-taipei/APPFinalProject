package com.example.appfinalproject_10130492

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.appfinalproject_10130492.data.Assignment
import com.example.appfinalproject_10130492.databases.AssignmentsDB
import com.example.appfinalproject_10130492.databases.CoursesDB
import com.example.appfinalproject_10130492.databinding.FragmentSecondNewAssignBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

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
    private val uDate = java.text.SimpleDateFormat("yyyy-MM-dd  aa hh:mm", Locale.TAIWAN)
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
        name.markRequired()
        note = view.findViewById(R.id.add_assign_notes)
        courseName = view.findViewById(R.id.course_sel_menu)
        var fromCalendarDate = Calendar.getInstance()
        var toCalendarDate = Calendar.getInstance()
        val course = CoursesDB(this.context)
        val assignDB = AssignmentsDB(this.context)



        //get courses data
        var coursesArr= readCourses(course)



        if(coursesArr.isNotEmpty() && coursesArr[0]!=null) {
            val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_list_item, coursesArr)
            (binding.courseSelTextField.editText as? MaterialAutoCompleteTextView)?.setAdapter(
                adapter
            )
        }




        //init text
        dateToTextHelper(fromCalendarDate,"from")
        dateToTextHelper(toCalendarDate,"to")

        Log.i("Time","Time Now: ${Date().time}")


        fromCalendarDate.timeZone = TimeZone.getDefault()
        toCalendarDate.timeZone = TimeZone.getDefault()

        //See if there's assignment attached to companion obj.
        if(isAssignmentInitialized()){
            super.setMenuVisibility(false)
            Log.d("QR","inited")

            name.editText!!.text = SpannableStringBuilder(assignmentInput.title)
            note.editText!!.text = SpannableStringBuilder(assignmentInput.note)
            courseName.text = if(assignmentInput.courseName == null) courseName.text else Editable.Factory.getInstance().newEditable(assignmentInput.courseName)
            Log.i("QR","QR Title: ${assignmentInput.title}")

            fromCalendarDate.time =
                if(assignmentInput.id == -1){
                    Date(assignmentInput.assignedDate)
                }
                else{
                    Date(assignmentInput.assignedDate)
                }
            toCalendarDate.time = Date(assignmentInput.dueDate)
            dateToTextHelper(fromCalendarDate,"from")
            dateToTextHelper(toCalendarDate,"to")


        }
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
             datePickHelper(fromCalendarDate,"from")
            dateToTextHelper(fromCalendarDate,"from")
        }
        binding.datepick2.setOnClickListener{
            datePickHelper(toCalendarDate,"to")
            dateToTextHelper(toCalendarDate,"to")

        }

        binding.save.setOnClickListener{
            val alert = AlertDialog.Builder(requireContext())
            val nameString = name.editText!!.text.toString()
            if(nameString.isEmpty()){
                alert.setTitle(R.string.alert)
                alert.setMessage(R.string.alert_msg_required)
                alert.setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
                alert.create().show()
                return@setOnClickListener
            }
            if(fromCalendarDate.timeInMillis > toCalendarDate.timeInMillis){
                alert.setTitle(R.string.alert)
                alert.setMessage(R.string.alert_msg_date_error)
                alert.setPositiveButton(R.string.alert_msg_date_confirm) { dialog, _ ->
                    dialog.dismiss();
                    val tmp = fromCalendarDate
                    fromCalendarDate = toCalendarDate
                    toCalendarDate = tmp
                    dateToTextHelper(fromCalendarDate,"from")
                    dateToTextHelper(toCalendarDate,"to")
                    Snackbar.make(view,R.string.alert_msg_date_success,Snackbar.LENGTH_SHORT).show()

                }
                alert.setNegativeButton(R.string.cancel_course){ dialog, _->dialog.dismiss()}

                alert.create().show()
                return@setOnClickListener
            }


            if(!(coursesArr.contains(courseName.text.toString()) )&& courseName.text.toString().isNotEmpty()
            ){
                Log.i("Course","${coursesArr.contains(courseName.text.toString())}嗎？${courseName.text.toString()} ")
                val dialog = AlertDialog.Builder(requireActivity())
                dialog.setTitle(R.string.alert)
                    .setMessage(R.string.new_courses_from_qr_hint)

                dialog.setPositiveButton(R.string.ok) { _, _ ->
                    val newCoursesDialog = NewCoursesDialog();
                    newCoursesDialog.courseName = courseName.text.toString()
                    newCoursesDialog.show(parentFragmentManager, null)
                    newCoursesDialog.setDialogDestroyListener(object: NewCoursesDialog.DialogInterface{
                        override fun onDestroyListener() {
                            coursesArr = readCourses(course)
                        }

                    })
                }
                dialog.setNegativeButton(R.string.empty){_,_->
                    courseName.text = Editable.Factory.getInstance().newEditable("");
                }
                dialog.create()
                dialog.show()
                coursesArr = readCourses(course)

                return@setOnClickListener
            }
            if(isAssignmentInitialized() && assignmentInput.id != -1){
                val assignment = Assignment(
                    assignmentInput.id,
                    nameString,
                    fromCalendarDate.timeInMillis,
                    toCalendarDate.timeInMillis,
                    courseName.text.toString(),
                    note.editText!!.text.toString(),
                    0 )
                assignDB.update(assignment)
            }else {
                val assignment = Assignment(
                    null,
                    nameString,
                    fromCalendarDate.timeInMillis,
                    toCalendarDate.timeInMillis,
                    courseName.text.toString(),
                    note.editText!!.text.toString(),
                    0
                )

                assignDB.insert(assignment)
            }
            activity?.finish()



        }
    }

    private fun datePickHelper(calendarDate: Calendar, type: String): Calendar{
        val origCalendar:Calendar = calendarDate.clone() as Calendar
        val datepicker = MaterialDatePicker.Builder.datePicker().setTitleText(R.string.date_pick).setSelection(Date(calendarDate.timeInMillis).time).build()
        datepicker.show(parentFragmentManager,"tag")
        datepicker.addOnPositiveButtonClickListener {
            Log.i("Date","Positive")
            Log.i("Date",""+datepicker.selection)
            // DatePicker Selection Result
            var dateSel:Long = datepicker.selection!!

            //Set Calendar Time
            calendarDate.time = Date(dateSel)
            Log.wtf("time" ,""+calendarDate.get(Calendar.HOUR_OF_DAY))
            val timepicker = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_12H).setHour(origCalendar.get(Calendar.HOUR_OF_DAY)).setMinute(origCalendar.get(Calendar.MINUTE)).setTitleText(R.string.date_pick).build()
            timepicker.show(parentFragmentManager,"tag")
            timepicker.addOnPositiveButtonClickListener{
                calendarDate.set(Calendar.HOUR_OF_DAY,timepicker.hour)
                calendarDate.set(Calendar.MINUTE,timepicker.minute)
                Log.i("Date",""+timepicker.hour)
                dateToTextHelper(calendarDate,type)

            }
        }
        return calendarDate
    }
    private fun dateToTextHelper(calendarDate: Calendar, type: String){
        when(type){
            "to"->toDateTextView.text = uDate.format(Date(calendarDate.timeInMillis))
            "from"->fromDateTextView.text = uDate.format(Date(calendarDate.timeInMillis))
        }
    }
    private fun readCourses(db:CoursesDB):Array<String?>{
        val coursesList = db.readAll()
        var courseArr = arrayOfNulls<String>(coursesList.size)
        for(i in 0 until coursesList.size) {
            courseArr[i] = coursesList[i].courseName
            courseArr[i]?.let { Log.i("Courses", it) }
        }
        return courseArr
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun TextInputLayout.markRequired(){
        hint = "$hint *"
    }
    companion object{
        lateinit var assignmentInput: Assignment
        fun setEditModeToggle(toggle: Boolean){
            EditMode.canItEdit = toggle
        }

        class EditMode{
            companion object{
                var canItEdit = false
            }
        }

        fun isAssignmentInitialized()= :: assignmentInput.isInitialized && EditMode.canItEdit
    }
}