package com.example.appfinalproject_10130492

import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.appfinalproject_10130492.data.Assignment
import com.example.appfinalproject_10130492.databinding.FragmentSecondBinding
import org.w3c.dom.Text
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

        name.text = assignmentBody.title
        courseName.text = assignmentBody.courseName
        note.text = assignmentBody.note
        val date = Date(assignmentBody.dueDate)
        val uDate = java.text.SimpleDateFormat("yyyy-MM-dd  aa hh:mm", Locale.TAIWAN)
        dueDate.text = uDate.format(date)
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

    companion object{
        lateinit var assignmentBody: Assignment
    }
}