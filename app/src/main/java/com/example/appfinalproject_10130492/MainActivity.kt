package com.example.appfinalproject_10130492

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.MenuItem
import androidx.core.widget.NestedScrollView
import com.example.appfinalproject_10130492.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
private lateinit var fab: FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var scroll: NestedScrollView
    private lateinit var botnav: BottomNavigationView
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarAdd)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        fab = findViewById(R.id.fab1)

        // Bottom Navigation Listener
        botnav = findViewById(R.id.botnav)
        botnav?.setOnItemSelectedListener{
            Log.i("Menu", ""+it.itemId+" Title"+it.title)
            when(it.itemId){
                R.id.assignments->{

                }
                R.id.classes -> {

                }
            }
            return@setOnItemSelectedListener true
        }
        setupActionBarWithNavController(navController, appBarConfiguration)
        fab.setOnClickListener{l->
            var intent = Intent(this, AddActivity::class.java)
            intent.putExtra("selected",botnav.selectedItemId)
            startActivity(intent)
            overridePendingTransition(R.anim.bottom_up,R.anim.no_anim)
        }


    }

    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.i("Menu","Menu Clicked")
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("Menu","Activity Finished")
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
    // This is for hiding the fab button when user's navigating to the bottom.
    companion object{
        fun scrollFab(show: Boolean){
            if(show){
                fab.show()
            }else{
                fab.hide()
            }
    }
    }

}