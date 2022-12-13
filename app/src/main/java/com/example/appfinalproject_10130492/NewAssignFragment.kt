package com.example.appfinalproject_10130492

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.example.appfinalproject_10130492.databinding.FragmentSecondNewAssignBinding
import com.google.android.material.datepicker.MaterialDatePicker
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondNewAssignBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var calendar = Calendar.getInstance()
        binding.datepick.setOnClickListener{
                val datepicker = MaterialDatePicker.Builder.datePicker().setTitleText(R.string.date_pick).build()
                datepicker.show(parentFragmentManager,"tag")
                datepicker.addOnPositiveButtonClickListener {
                    Log.i("Date","Positive")
                    Log.i("Date",""+datepicker.selection)
                    val y = datepicker
                    val timepicker = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_12H).setTitleText(R.string.date_pick).build()
                    timepicker.show(parentFragmentManager,"tag")
                    timepicker.addOnPositiveButtonClickListener{

                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}