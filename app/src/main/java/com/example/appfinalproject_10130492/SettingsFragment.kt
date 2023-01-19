package com.example.appfinalproject_10130492

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
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
    private var origPercentage = 0

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

    }




override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val manager = this.preferenceManager
        sharedPref = manager.sharedPreferences!!
        val textView: TextView = view.findViewById(R.id.settingDuePercentage)
        val seekbar: SeekBar = view.findViewById(R.id.seekBar1)
        settingDB= SettingDB(context)
        setting = settingDB.read()

    seekbar.progress = setting.duePercentage
    val gotoPref:Preference = findPreference("goto_setting")!!
    gotoPref.setOnPreferenceClickListener {
        val intent = Intent()
        intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("android.provider.extra.APP_PACKAGE", context?.applicationInfo?.packageName);
        startActivity(intent)
        true
    }
    origPercentage = setting.duePercentage
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
        if(setting.duePercentage != origPercentage) {
            val dialog = context?.let { AlertDialog.Builder(it) }
            dialog?.setTitle(R.string.percentage_changed)
            dialog?.setMessage(R.string.percentage_changed_msg)
            dialog?.setPositiveButton(R.string.percentage_changed_positive) { _, _ ->
                val alarmService = context?.let { AlarmService(it) }
                alarmService?.restartAlarm()
            }
            dialog?.setNegativeButton(R.string.percentage_changed_negative) { _, _ ->


            }
            dialog?.show()
        }
        settingDB.deleteAll();
        settingDB.insert(setting)
        super.onDestroy()

    }


}