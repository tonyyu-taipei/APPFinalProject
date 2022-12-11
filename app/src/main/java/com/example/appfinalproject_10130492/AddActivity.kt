package com.example.appfinalproject_10130492

import android.os.Bundle
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.appfinalproject_10130492.databinding.ActivityAddBinding

class AddActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityAddBinding
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.toolbarAdd)


        navController = findNavController(R.id.nav_host_fragment_content_class)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        setupActionBarWithNavController(navController, appBarConfiguration)
        /*binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAnchorView(R.id.fab)
                .setAction("Action", null).show()
        }*/
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(onBackBehavior == "Activity")
        overridePendingTransition(R.anim.no_anim,R.anim.up_bottom)
        else{
            overridePendingTransition(R.anim.no_anim,R.anim.left_right)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return when(onBackBehavior){
            "Activity" -> {
                finish()
                overridePendingTransition(R.anim.no_anim,R.anim.up_bottom)
                true
            }
            "Fragment" ->{
                if(backFragmentTransition == R.id.action_Second2Fragment_to_First2Fragment){
                    onBackBehavior = "Activity"
                }
                navController.navigate(backFragmentTransition)
                supportActionBar?.setDisplayHomeAsUpEnabled(true);
                supportActionBar?.setDisplayShowHomeEnabled(true);
                true
            }
            else ->{
                val navController = findNavController(R.id.nav_host_fragment_content_class)
                navController.navigateUp() || super.onSupportNavigateUp()
            }
        }

    }
    companion object{
        var onBackBehavior = "Activity"
        var backFragmentTransition = 0

    }
}