package com.example.appfinalproject_10130492

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.appfinalproject_10130492.databinding.FragmentFirstAddBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class AMMenuFirstFragment : Fragment() {

    private var _binding: FragmentFirstAddBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstAddBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addAssignBtn.setOnClickListener {
            AssignmentsModifyActivity.backFragmentTransition = R.id.action_Second2Fragment_to_First2Fragment
            findNavController().navigate(R.id.action_First2Fragment_to_Second2Fragment)
            AssignmentsModifyActivity.onBackBehavior = "Fragment"
        }
        binding.scanQrBtn.setOnClickListener {
            AssignmentsModifyActivity.backFragmentTransition = R.id.action_addQRFragment_to_AddFirstFragment
            findNavController().navigate(R.id.action_AddFirstFragment_to_addQRFragment)
            AssignmentsModifyActivity.onBackBehavior = "Fragment"
        }
/* default btn behav.
        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_First2Fragment_to_Second2Fragment)
        }*/
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}