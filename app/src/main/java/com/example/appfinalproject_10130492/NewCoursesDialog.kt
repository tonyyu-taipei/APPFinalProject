package com.example.appfinalproject_10130492

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.appfinalproject_10130492.data.Course
import com.example.appfinalproject_10130492.databases.CoursesDB
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class NewCoursesDialog: DialogFragment(){
    private lateinit var courseNameTextField:TextInputLayout
    private lateinit var teacherTextField: TextInputLayout
    private lateinit var newCourseTitle: TextView
    lateinit var courseName: String
    private var editCourse:Course? = null
    private var myListener: DialogInterface?


    init{
        myListener = null;

    }
    fun setEditCourse(course:Course){
        editCourse = course
    }

    fun setDialogDestroyListener(dialogInterface: DialogInterface){
        myListener = dialogInterface
    }

    override fun onDestroy() {
        myListener?.onDestroyListener()
        super.onDestroy()
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {


            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;
            val coursesDB = CoursesDB(context)
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            val view = inflater.inflate(R.layout.new_courses_dialog, null)
            courseNameTextField = view.findViewById(R.id.new_course_name)
            newCourseTitle = view.findViewById(R.id.new_course_title)
                teacherTextField = view.findViewById(R.id.new_course_teacher)
            var originalCourseName = ""

            if(this::courseName.isInitialized){
                courseNameTextField.editText?.text = Editable.Factory.getInstance().newEditable(courseName)
            }
            val inputedCourse = editCourse
            if(inputedCourse != null){
                newCourseTitle.text = getString(R.string.edit_course_title)
                teacherTextField.editText?.text = Editable.Factory.getInstance().newEditable(if(inputedCourse.teacher == null)"" else inputedCourse.teacher)
                courseNameTextField.editText?.text = Editable.Factory.getInstance().newEditable(inputedCourse.courseName)
                originalCourseName =inputedCourse.courseName
            }

            builder.setView(view)
                .setPositiveButton(R.string.confirm
                ) { _, _ ->
                    if(courseNameTextField.editText!!.text.toString()==""){
                        val dialog = AlertDialog.Builder(requireActivity())
                        dialog.setTitle(R.string.alert)
                            .setMessage(R.string.alert_msg_required)
                            .setPositiveButton(R.string.confirm) { _, _ -> }
                            .create()
                            .show()
                    }
                    val course: Course = if(courseNameTextField.editText!!.text.toString() == ""){
                        if(inputedCourse == null) {
                            Course(courseNameTextField.editText!!.text.toString(), null)
                        }else{
                            inputedCourse.courseName = courseNameTextField.editText!!.text.toString()
                            inputedCourse.teacher =null
                            inputedCourse
                        }
                    }else{
                        if(inputedCourse == null) {
                            Course(
                                courseNameTextField.editText!!.text.toString(),
                                teacherTextField.editText!!.text.toString()
                            )
                        }else{
                            inputedCourse.courseName = courseNameTextField.editText!!.text.toString()
                            inputedCourse.teacher = teacherTextField.editText!!.text.toString()
                            inputedCourse
                        }
                    }
                    Snackbar.make(view,R.string.added_successfully,Snackbar.LENGTH_LONG).show()
                    if(editCourse == null) {
                        coursesDB.insert(course)
                    }else{
                        coursesDB.update(course,originalCourseName)
                    }
                }
                .setNegativeButton(R.string.cancel
                ) { _, _ ->
                    dialog?.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    interface DialogInterface{
        fun onDestroyListener()
    }


}
