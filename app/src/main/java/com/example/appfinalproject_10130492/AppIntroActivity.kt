package com.example.appfinalproject_10130492

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment
import com.github.appintro.AppIntroPageTransformerType

/**
 * AppIntroActivity
 * An [activity] to handle the app introductions.
 */
class AppIntroActivity : AppIntro() {
    var isCourse = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val extra = intent.extras
        isCourse = extra?.getBoolean("isCourse") != null
        super.onCreate(savedInstanceState)
        addSlide(AppIntroFragment.newInstance(
            title = if(isCourse)"\n${getString(R.string.course)}" else "\n${getString(R.string.welcome)}",
            description = if(isCourse) "\n${getString(R.string.course_description)}" else getString(R.string.welcome_description),
            imageDrawable = if(isCourse) R.drawable.instruction_2 else R.drawable.ic_launcher_foreground,
            backgroundDrawable = R.drawable.background_theme2,
            backgroundColor=Color.HSVToColor(floatArrayOf(157F,59F,82F)),
            titleColor = Color.WHITE,
            descriptionColor = Color.WHITE,

        ))
        addSlide(AppIntroFragment.newInstance(
            imageDrawable = if(isCourse) R.drawable.instruction_course else R.drawable.instruction_1,
            title = if(isCourse) getString(R.string.course_instruction2) else "\n${getString(R.string.new_assign_instruction)}",
            description = if(isCourse) getString(R.string.course_instruction_description) else getString(R.string.new_assign_instruction_description),
            titleColor = Color.WHITE,
            descriptionColor = Color.WHITE,
            backgroundDrawable = R.drawable.background_theme2

            ))

        setTransformer(AppIntroPageTransformerType.Fade)
        setImmersiveMode()
    }
    private fun updatePreferenceAndLeave(){
        if(isCourse){
            PreferenceManager.getDefaultSharedPreferences(this).edit().apply {
                putBoolean(COURSE_TIP, true)
                apply()
            }
        }else {
            PreferenceManager.getDefaultSharedPreferences(this).edit().apply {
                putBoolean(ASSIGNMENT_TIP, true)
                apply()
            }
        }
        finish()
    }
    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        updatePreferenceAndLeave()

    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        updatePreferenceAndLeave()

    }
    companion object{
        val ASSIGNMENT_TIP = "assignment_tip"
        val COURSE_TIP = "course_tip"

    }
}