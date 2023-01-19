package com.example.appfinalproject_10130492

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.appfinalproject_10130492.data.Assignment
import com.example.appfinalproject_10130492.databases.AssignmentsDB
import com.example.appfinalproject_10130492.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton


private lateinit var fab: FloatingActionButton


class MainActivity : AppCompatActivity() {
    private lateinit var botnav: BottomNavigationView
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    var dialog: NewCoursesDialog = NewCoursesDialog()
    private lateinit var navController:NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        val input = intent.extras
        val inputedAssignmentID = input?.getInt("assignment")


        AlarmService.alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        CoursesFirstFragment.newCoursesDialog = dialog
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarAdd)
        navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        fab = findViewById(R.id.fab1)


        // Bottom Navigation Listener
        botnav = findViewById(R.id.botnav)


        botnav.setOnItemSelectedListener{

            fab.setImageResource(android.R.drawable.ic_input_add)
            // Somehow once the user pressed course from botnav,
            // the onSupportNavUp() will no longer work, so I have to implement
            // this custom listener. It starts once botnav was clicked.
            SecondFragment.forceFabAdd = object: ForceChangeFabToAddListener{
                override fun onChange() {
                    fab.setImageResource(android.R.drawable.ic_input_add)
                    editModeToggle(false)
                }

            }

            editModeToggle(false)
            Log.i("Menu", ""+it.itemId+" Title"+it.title)
            when(it.itemId){
                R.id.assignments->{
                    FirstFragment.modeOn = false
                    navController.setGraph(R.navigation.nav_graph)
                    appBarConfiguration = AppBarConfiguration(navController.graph)
                    scrollFab(true)



                }
                R.id.courses -> {
                    navController.setGraph(R.navigation.nav_graph3)
                    scrollFab(true)
                    appBarConfiguration = AppBarConfiguration(navController.graph)
                }
                R.id.action_settings ->{
                    navController.setGraph(R.navigation.nav_settings)
                    appBarConfiguration = AppBarConfiguration(navController.graph)
                    scrollFab(false)
                }
            }
            setupActionBarWithNavController(navController, appBarConfiguration)
            return@setOnItemSelectedListener true
        }
        setupActionBarWithNavController(navController, appBarConfiguration)
        if(inputedAssignmentID != null){
            val assignmentsDB = AssignmentsDB(this)
            SecondFragment.assignmentBody = assignmentsDB.read(inputedAssignmentID)!!
            navController.navigate(R.id.SecondFragment)
        }
        fab.setOnClickListener{
            val intent = Intent(this, AddActivity::class.java)
            if(botnav.selectedItemId == R.id.courses){
                dialog.show(supportFragmentManager,null)

                return@setOnClickListener
            }
            if(EditMode.canItEdit){
                intent.putExtra("assignment", assignment)
            }
            startActivity(intent)

            overridePendingTransition(R.anim.bottom_up,R.anim.no_anim)

        }


    }

    override fun onResume() {
        super.onResume()
        AlarmService.alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

    }
    private fun backPressedFunc(){
        onBackPressedDispatcher.onBackPressed()
        fab.setImageResource(android.R.drawable.ic_input_add)
        editModeToggle(false)
        scrollFab(true)
    }
    @Deprecated("Deprecated in Java", ReplaceWith("backPressedFunc()"))
    override fun onBackPressed() {
        backPressedFunc()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.i("Menu","Menu Clicked")

        return when (item.itemId) {
            R.id.delete_assign ->{
                Log.i("Menu","delete button clicked")
                val alertBuilder = AlertDialog.Builder(this)
                alertBuilder.setMessage(R.string.confirm_del)
                    .setPositiveButton(R.string.confirm) { _, _ ->
                        val assignDB = AssignmentsDB(this)
                        val alarmService =  AlarmService(this)
                        alarmService.cancelSpecificAlarm(assignment)
                        assignment.id?.let { assignDB.deleteOne(it) }
                        backPressedFunc()

                    }
                    .setNegativeButton(R.string.cancel){ _, _ ->

                    }
                alertBuilder.create().show()

               false
            }
            R.id.share_assign ->{
                Log.i("Menu","Share button clicked")
                QRShareFragment.findId = assignment.id!!
                when(navController.graph.id) {
                    R.id.nav_graph->
                        navController.navigate(R.id.action_SecondFragment_to_QRShareFragment)
                    R.id.nav_graph3->
                        navController.navigate(R.id.action_SecondFragment_to_QRShareFragment2)
                }
                false
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        editModeToggle(false)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        fab.setImageResource(android.R.drawable.ic_input_add)
        editModeToggle(false)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
    // This is for hiding the fab button when user's navigating to the bottom.
    companion object{

        lateinit var assignment:Assignment
        fun editModeToggle(toggle: Boolean){
            EditMode.canItEdit = toggle
        }
        class EditMode {
            companion object{
                var canItEdit = false
            }
        }
        fun scrollFab(show: Boolean){
            if(show){
                fab.show()
            }else{
                fab.hide()
            }
    }
    }
    //ForceChangeFabToAddListener


}
interface ForceChangeFabToAddListener{
    fun onChange()
}
