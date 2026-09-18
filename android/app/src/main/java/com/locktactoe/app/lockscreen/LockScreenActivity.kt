package com.locktactoe.app.lockscreen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.locktactoe.app.game.GameActivity
import com.locktactoe.app.ui.theme.LockTacToeTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Shown the instant the screen turns on while the device is locked (see [LockListenerService]).
 * Dismissing it reveals the real system keyguard (PIN/pattern) underneath, if one is set.
 */
class LockScreenActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        setContent {
            LockTacToeTheme {
                var time by remember { mutableStateOf(currentTime()) }
                var date by remember { mutableStateOf(currentDate()) }

                DisposableEffect(Unit) {
                    val receiver = object : BroadcastReceiver() {
                        override fun onReceive(context: Context?, intent: Intent?) {
                            time = currentTime()
                            date = currentDate()
                        }
                    }
                    registerReceiver(receiver, IntentFilter(Intent.ACTION_TIME_TICK))
                    onDispose { unregisterReceiver(receiver) }
                }

                LockScreenContent(
                    time = time,
                    date = date,
                    onPlay = { startActivity(Intent(this@LockScreenActivity, GameActivity::class.java)) },
                    onDismiss = { finish() }
                )
            }
        }
    }

    private fun currentTime() = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    private fun currentDate() = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
}

@Composable
private fun LockScreenContent(
    time: String,
    date: String,
    onPlay: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))
        Text(time, color = Color.White, fontSize = 72.sp, fontWeight = FontWeight.Light)
        Spacer(Modifier.height(8.dp))
        Text(date, color = Color.Gray, fontSize = 18.sp)
        Spacer(Modifier.weight(1f))
        Button(onClick = onPlay) {
            Text("▶ Play Tic Tac Toe")
        }
        Spacer(Modifier.height(24.dp))
        Text(
            "Tap to unlock",
            color = Color.DarkGray,
            fontSize = 14.sp,
            modifier = Modifier
                .clickable(onClick = onDismiss)
                .padding(bottom = 24.dp)
        )
    }
}
