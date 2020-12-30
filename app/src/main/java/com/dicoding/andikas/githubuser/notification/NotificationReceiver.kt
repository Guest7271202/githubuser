package com.dicoding.andikas.githubuser.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.dicoding.andikas.githubuser.R
import com.dicoding.andikas.githubuser.ui.MainActivity
import java.util.*

class NotificationReceiver: BroadcastReceiver() {
    companion object {
        const val TYPE_REPEATING = "repeating notification"
        const val EXTRA_MESSAGE = "message"
        const val EXTRA_TYPE = "type"
        const val CHANNEL_ID = "channel_1"
        const val CHANNEL_NAME = "notification channel"

        private const val ID_REPEATING = 100
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val message = intent?.getStringExtra(EXTRA_MESSAGE)

        sendNotification(context, message.toString())
    }

    private fun sendNotification(context: Context, message: String){
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent = PendingIntent.getActivity(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationManagerCompat = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_alarm_24)
                .setContentTitle("Github Daily Reminder")
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            builder.setChannelId(CHANNEL_ID)
            notificationManagerCompat.createNotificationChannel(channel)
        }

        notificationManagerCompat.notify(100, builder.build())
    }

    fun setDailyReminder(context: Context, type: String, message: String){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra(EXTRA_MESSAGE, message)
        intent.putExtra(EXTRA_TYPE, type)

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 9)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        val pendingIntent = PendingIntent.getBroadcast(context, ID_REPEATING, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
        Toast.makeText(context, "Github daily reminder activated", Toast.LENGTH_SHORT).show()
    }

    fun setCancelDailyReminder(context: Context){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, ID_REPEATING, intent, 0)
        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)
        Toast.makeText(context, "Github daily reminder cancelled", Toast.LENGTH_SHORT).show()
    }

}

