package com.example.weatherapplication.ui.alerts.view

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.example.weatherapplication.MainActivity
import com.example.weatherapplication.R
import com.example.weatherapplication.databinding.AlarmDialogBinding

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var binding: AlarmDialogBinding

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        binding = AlarmDialogBinding.inflate(LayoutInflater.from(this))

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP


        // insert the view into the window and then display it
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.addView(binding.root, params)

        // open the media player and start looping
        val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        mediaPlayer = MediaPlayer.create(this, notificationUri)
        mediaPlayer.isLooping = true
        mediaPlayer.start()

        binding.stopButton.setOnClickListener {
            stopSelf()
        }

        binding.snoozeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            pendingIntent.send()
            stopSelf()
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        if (::binding.isInitialized) {
            windowManager.removeView(binding.root)
        }

        if (::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }
}