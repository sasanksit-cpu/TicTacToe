package com.locktactoe.app.lockscreen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.locktactoe.app.SettingsStore

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        if (SettingsStore(context).isLockScreenEnabled()) {
            ContextCompat.startForegroundService(context, Intent(context, LockListenerService::class.java))
        }
    }
}
