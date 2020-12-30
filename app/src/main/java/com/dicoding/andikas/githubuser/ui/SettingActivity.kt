package com.dicoding.andikas.githubuser.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.dicoding.andikas.githubuser.notification.NotificationReceiver
import com.dicoding.andikas.githubuser.R
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {
    companion object {
        const val SETTING_PREF = "setting_preference"
    }

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.setting)

        val alarmReceiver = NotificationReceiver()
        sharedPreferences = getSharedPreferences(SETTING_PREF, Context.MODE_PRIVATE)

        switch_reminder.isChecked = sharedPreferences.getBoolean("reminder", false)
        switch_reminder.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked){
                alarmReceiver.setDailyReminder(this, NotificationReceiver.TYPE_REPEATING, "TIme to go back")
            } else {
                alarmReceiver.setCancelDailyReminder(this)
            }
            saveState(isChecked)
        }
    }

    private fun saveState(value: Boolean){
        sharedPreferences.edit()
                .putBoolean("reminder", value)
                .apply()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }
}