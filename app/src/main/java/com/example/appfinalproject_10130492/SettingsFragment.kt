package com.example.appfinalproject_10130492

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceGroupAdapter
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceScreen
import androidx.preference.PreferenceViewHolder

import androidx.recyclerview.widget.RecyclerView
import com.example.appfinalproject_10130492.data.Setting
import com.example.appfinalproject_10130492.databases.SettingDB


class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var settingDB: SettingDB
    private lateinit var setting: Setting;
    private lateinit var sharedPref: SharedPreferences
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

    }




override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val manager = this.preferenceManager
        sharedPref = manager.sharedPreferences!!
    val edit = sharedPref.edit()
        val textView: TextView = view.findViewById(R.id.settingDuePercentage)
        val seekbar: SeekBar = view.findViewById(R.id.seekBar1)
        settingDB= SettingDB(context)
        setting = settingDB.read()
        edit.putBoolean("due_notification", setting.toggleDue == 1)
        edit.putBoolean("late_notification", setting.toggleDue == 1)
        edit.commit()

    seekbar.progress = setting.duePercentage
        textView.text = setting.duePercentage.toString()
        seekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(progress in 1..99){
                    textView.text=progress.toString()
                    setting.duePercentage = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })



    }

    override fun onDestroy() {
        super.onDestroy()

        val dueBoolean = sharedPref.getBoolean("due_notification",true)
        val lateBoolean = sharedPref.getBoolean("late_notification", true)
        Log.i("PREF",sharedPref.toString())
        setting.toggleDue = if(dueBoolean) 1 else 0
        setting.toggleLate = if(lateBoolean) 1 else 0
        settingDB.deleteAll()
        settingDB.insert(setting)

    }


}