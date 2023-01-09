package com.example.appfinalproject_10130492

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appfinalproject_10130492.data.Assignment
import com.example.appfinalproject_10130492.data.AssignmentsWithStatus
import com.example.appfinalproject_10130492.databases.AssignmentsDB
import com.example.appfinalproject_10130492.databases.CoursesDB
import com.example.appfinalproject_10130492.databinding.FragmentCoursesFirstBinding
import com.example.appfinalproject_10130492.databinding.FragmentFirstBinding

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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCoursesFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onResume() {
        super.onResume()
        Log.i("Courses","Resumed")

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController(requireActivity(),R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        val navHostFragment = NavHostFragment.findNavController(this)
        val toolbar:androidx.appcompat.widget.Toolbar = requireActivity().findViewById(R.id.toolbarAdd)
        NavigationUI.setupWithNavController(toolbar,navHostFragment,appBarConfiguration)
        recyclerView = view.findViewById(R.id.courses_rec)
        courseDB = CoursesDB(this.context)
        assignDB = AssignmentsDB(this.context)


        val courses = courseDB.readAll()
        assStatus = assignDB.readAllByStatus()
        adapter = CoursesRecyclerAdapter(courses,courseDB,assignDB,view,this.context)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)

        newCoursesDialog.setDialogDestroyListener(object: NewCoursesDialog.DialogInterface{
            override fun onDestroyListener() {
                courses.clear()
                courses.addAll(courseDB.readAll())
                adapter.notifyDataSetChanged()
                Log.i("Courses","Destroyed")
            }

        })
        scroll = view.findViewById(R.id.nest_course)
        scroll.setOnScrollChangeListener(View.OnScrollChangeListener(
            fun(v: View, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int){
                val view = scroll.getChildAt(scroll.childCount - 1) as View
                val diff = view.bottom - (scroll.height + scroll
                    .scrollY)
                if(diff==0){
                    Log.i("Scroll","BOTTOM")
                    MainActivity.scrollFab(false);
                }else{
                    MainActivity.scrollFab(true);
                }
            }

        ))
        val itemCallback = this.context?.let { ItemTouchHelperCallback(adapter, it) }
        val itemHelper = itemCallback?.let { ItemTouchHelper(it) }
        itemHelper?.attachToRecyclerView(recyclerView)



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