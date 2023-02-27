package com.example.appfinalproject_10130492

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.appfinalproject_10130492.data.Assignment
import com.example.appfinalproject_10130492.databinding.ActivityAddBinding

/**
 * AssignmentsModifyActivity
 * Everything related the Assignment. Including create and modify assignments,
 * and also create assignments via QR code.
 *
 * intent:
 * Sharing Intent (EXTRA_STREAM) requires an IME type that's image
 */
class AssignmentsModifyActivity : AppCompatActivity() {

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
        if (inputedAssignment != null) {
            AMDetailFragment.assignmentInput = inputedAssignment
            AMDetailFragment.setEditModeToggle(true)
            navController.navigate(R.id.AddSecondFragment)
            onBackBehavior = "No"
            supportActionBar?.title = getString(R.string.editAssign)


        }
        if (intent?.action == Intent.ACTION_SEND && intent?.type?.startsWith("image/") == true) {
            handleImageInput(intent, this)
            navController.navigate(R.id.addQRFragment)
        }


    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBackPressed() {
        if (onBackBehavior == "No") {
            finish()
            return
        }
        super.onBackPressed()

        AMDetailFragment.Companion.EditMode.canItEdit = false

        if (onBackBehavior == "Activity")
            overridePendingTransition(R.anim.no_anim, R.anim.up_bottom)
    }

    override fun onSupportNavigateUp(): Boolean {
        return when (onBackBehavior) {
            "No" -> {
                finish()
                false
            }
            "Activity" -> {
                finish()
                true
            }
            else -> {
                val navController = findNavController(R.id.nav_host_fragment_content_class)
                overridePendingTransition(R.anim.no_anim, R.anim.left_right)
                navController.navigateUp() || super.onSupportNavigateUp()
                true
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.close -> {
                finish()
                AMDetailFragment.Companion.EditMode.canItEdit = false
                overridePendingTransition(R.anim.no_anim, R.anim.up_bottom)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }

    private fun handleImageInput(intent: Intent, context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            (intent.getParcelableExtra(Intent.EXTRA_STREAM, Parcelable::class.java) as? Uri)?.let {
                // Update UI to reflect image being shared
                handleImage(it, context)
            }
        } else {
            (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
                handleImage(it, context)
            }
        }
    }

    private fun handleImage(uri: Uri, context: Context) {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
        AMQrResolveFragment.bitmap = bitmap
        AMQrResolveFragment.importMode = true
    }

    companion object {
        var onBackBehavior = "Activity"
        var backFragmentTransition = 0
    }
}