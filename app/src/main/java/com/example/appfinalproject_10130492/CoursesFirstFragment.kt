package com.example.appfinalproject_10130492

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appfinalproject_10130492.data.AssignmentsWithStatus
import com.example.appfinalproject_10130492.databases.AssignmentsDB
import com.example.appfinalproject_10130492.databases.CoursesDB
import com.example.appfinalproject_10130492.databinding.FragmentCoursesFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class CoursesFirstFragment : Fragment() {
    private lateinit var assStatus:AssignmentsWithStatus
    private lateinit var scroll: NestedScrollView
    private var _binding: FragmentCoursesFirstBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var adapter: CoursesRecyclerAdapter
    private lateinit var courseDB: CoursesDB
    private lateinit var assignDB:AssignmentsDB
    private lateinit var appBarConfiguration:AppBarConfiguration
    private lateinit var noCourseText: TextView
    private lateinit var noCourseTextDescription: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCoursesFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onResume() {
        super.onResume()
        Log.i("Courses","Resumed")

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController(requireActivity(),R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        val navHostFragment = NavHostFragment.findNavController(this)
        val toolbar:androidx.appcompat.widget.Toolbar = requireActivity().findViewById(R.id.toolbarAdd)
        NavigationUI.setupWithNavController(toolbar,navHostFragment,appBarConfiguration)
        recyclerView = view.findViewById(R.id.courses_rec)
        courseDB = CoursesDB(this.context)
        assignDB = AssignmentsDB(this.context)
        noCourseText = view.findViewById(R.id.no_course_prompt)
        noCourseTextDescription = view.findViewById(R.id.no_course_prompt_description)


        val courses = courseDB.readAll()
        assStatus = assignDB.readAllByStatus()
        adapter = CoursesRecyclerAdapter(courses,courseDB,assignDB,view,this.context,parentFragmentManager)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        setVisibility(courses.isNotEmpty())

        newCoursesDialog.setDialogDestroyListener(object: NewCoursesDialog.DialogInterface{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDestroyListener() {
                courses.clear()
                courses.addAll(courseDB.readAll())
                adapter.notifyDataSetChanged()
                setVisibility(courses.isNotEmpty())

            }

        })
        scroll = view.findViewById(R.id.nest_course)
        scroll.setOnScrollChangeListener(View.OnScrollChangeListener(
            fun(v: View, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int){
                val scrollView = scroll.getChildAt(scroll.childCount - 1) as View
                val diff = scrollView.bottom - (scroll.height + scroll
                    .scrollY)
                if(diff==0){
                    MainActivity.scrollFab(false)
                }else{
                    MainActivity.scrollFab(true)
                }
            }

        ))
        val itemCallback = this.context?.let { ItemTouchHelperCallback(adapter, it) }
        val itemHelper = itemCallback?.let { ItemTouchHelper(it) }
        itemHelper?.attachToRecyclerView(recyclerView)



    }
    private fun setVisibility(showRecycler: Boolean){
        if(!showRecycler) {
            noCourseText.visibility = View.VISIBLE
            noCourseTextDescription.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            (activity as MainActivity).fabAnimation(false)
        }else{
            noCourseText.visibility = View.GONE
            noCourseTextDescription.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            (activity as MainActivity).fabAnimation(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object{

        private lateinit var recyclerView:RecyclerView
         var newCoursesDialog: NewCoursesDialog = NewCoursesDialog()
    }



}