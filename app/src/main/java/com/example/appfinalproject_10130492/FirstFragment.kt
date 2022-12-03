package com.example.appfinalproject_10130492

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appfinalproject_10130492.databinding.FragmentFirstBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {
    private lateinit var scroll: NestedScrollView
    private var _binding: FragmentFirstBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.adapter = RecyclerAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        scroll = view.findViewById(R.id.nest)
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

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}