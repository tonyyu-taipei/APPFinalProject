package com.example.appfinalproject_10130492

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appfinalproject_10130492.data.Assignment
import com.example.appfinalproject_10130492.databases.AssignmentsDB
import com.example.appfinalproject_10130492.databases.AssignmentsDeleteQueue
import com.example.appfinalproject_10130492.databases.CoursesDB
import com.example.appfinalproject_10130492.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {
    var assList = ArrayList<Assignment>()
    private lateinit var scroll: NestedScrollView
    private var _binding: FragmentFirstBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var adapter: AssignmentsRecyclerAdapter
    private lateinit var courseDB: CoursesDB
    private lateinit var assignDB:AssignmentsDB
    private lateinit var deleteQueue: AssignmentsDeleteQueue
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        assList.clear()
        assList.addAll(if(isSpecific()){
            assignDB.readAll(courseSpecific)
        }else {
            assignDB.readAll()
        })

        adapter.notifyDataSetChanged()

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerview)
        courseDB = CoursesDB(this.context)
        assignDB = AssignmentsDB(this.context)


        if(isSpecific()){
            MainActivity.scrollFab(false)
            assList = assignDB.readAll(courseSpecific)
        }else {
            assList = assignDB.readAll()
        }
        deleteQueue = AssignmentsDeleteQueue(requireContext())
        adapter = AssignmentsRecyclerAdapter(assList, view,this.context,deleteQueue)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)



        scroll = view.findViewById(R.id.nest)
        scroll.setOnScrollChangeListener(View.OnScrollChangeListener(
            fun(v: View, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int){
                val scrollView = scroll.getChildAt(scroll.childCount - 1) as View
                val diff = scrollView.bottom - (scroll.height + scroll
                    .scrollY)
                if(diff==0){
                    Log.i("Scroll","BOTTOM")
                    MainActivity.scrollFab(false)
                }else{
                    MainActivity.scrollFab(true)
                }
            }

        ))

        val itemCallback = this.parentFragment?.context?.let { ItemTouchHelperCallback(adapter, it) }
        val itemHelper = itemCallback?.let { ItemTouchHelper(it) }
        itemHelper?.attachToRecyclerView(recyclerView)



    }

    override fun onPause() {
        super.onPause()
        deleteQueue.hardDelete()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object{
        lateinit var courseSpecific: String
        var modeOn = false
        fun isSpecific(): Boolean{
            return this::courseSpecific.isInitialized && modeOn
        }
        private lateinit var recyclerView:RecyclerView
    }



}