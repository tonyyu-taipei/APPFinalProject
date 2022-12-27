package com.example.appfinalproject_10130492

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.appfinalproject_10130492.data.Assignment
import com.example.appfinalproject_10130492.databinding.ActivityAddBinding

class AddActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityAddBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        //get bundled data
        val inputed = intent.extras
        val inputedAssignment: Assignment? = inputed?.get("assignment") as Assignment?

        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.toolbarAdd)


        val navController = findNavController(R.id.nav_host_fragment_content_class)

        appBarConfiguration = AppBarConfiguration(navController.graph)

        setupActionBarWithNavController(navController, appBarConfiguration)
        if(inputedAssignment != null){
            NewAssignFragment.assignmentInput = inputedAssignment
            NewAssignFragment.setEditModeToggle(true)
            navController.navigate(R.id.AddSecondFragment)
            onBackBehavior = "No"
            supportActionBar?.title = getString(R.string.editAssign)


        }



    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBackPressed() {
        if(onBackBehavior == "No"){
            finish()
            return
        }
        super.onBackPressed()

        NewAssignFragment.Companion.EditMode.canItEdit = false

        if(onBackBehavior == "Activity")
        overridePendingTransition(R.anim.no_anim,R.anim.up_bottom)
    }
    override fun onSupportNavigateUp(): Boolean {
        return when(onBackBehavior){
            "No"->{
                finish()
                false
            }
            "Activity" -> {
                finish()
                true
            }
            else ->{
                val navController = findNavController(R.id.nav_host_fragment_content_class)
                overridePendingTransition(R.anim.no_anim,R.anim.left_right)
                navController.navigateUp()||super.onSupportNavigateUp()
                true
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.close->{
                finish()
                NewAssignFragment.Companion.EditMode.canItEdit = false
                overridePendingTransition(R.anim.no_anim,R.anim.up_bottom)
            }
        }

        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }
    companion object{
        var onBackBehavior = "Activity"
        var backFragmentTransition = 0
    }
}