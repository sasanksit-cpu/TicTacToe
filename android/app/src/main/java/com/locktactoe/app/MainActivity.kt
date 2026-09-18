package com.locktactoe.app

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.locktactoe.app.game.GameActivity
import com.locktactoe.app.lockscreen.LockListenerService
import com.locktactoe.app.ui.theme.LockTacToeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settings = SettingsStore(this)

        setContent {
            LockTacToeTheme {
                var enabled by remember { mutableStateOf(settings.isLockScreenEnabled()) }

                val notifPermission = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { /* no-op: FGS notification still shows if denied on Android <13 */ }

                Surface {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Lock Tac Toe", style = MaterialTheme.typography.headlineMedium)
                        Text("Shows a clock + tic-tac-toe screen when your phone wakes up. 100% offline.")

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Enable lock screen")
                            Switch(checked = enabled, onCheckedChange = { checked ->
                                enabled = checked
                                settings.setLockScreenEnabled(checked)
                                if (checked) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        notifPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                    ContextCompat.startForegroundService(
                                        this@MainActivity,
                                        Intent(this@MainActivity, LockListenerService::class.java)
                                    )
                                } else {
                                    stopService(Intent(this@MainActivity, LockListenerService::class.java))
                                }
                            })
                        }

                        Button(onClick = { requestIgnoreBatteryOptimizations() }) {
                            Text("Exempt from battery optimization")
                        }

                        OutlinedButton(onClick = {
                            startActivity(
                                Intent(this@MainActivity, GameActivity::class.java)
                                    .putExtra(GameActivity.EXTRA_MODE, GameActivity.MODE_ONE_PLAYER)
                            )
                        }) {
                            Text("Play vs Computer")
                        }

                        OutlinedButton(onClick = {
                            startActivity(
                                Intent(this@MainActivity, GameActivity::class.java)
                                    .putExtra(GameActivity.EXTRA_MODE, GameActivity.MODE_TWO_PLAYER)
                            )
                        }) {
                            Text("Play 2 Players")
                        }
                    }
                }
            }
        }
    }

    private fun requestIgnoreBatteryOptimizations() {
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            startActivity(
                Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:$packageName")
                }
            )
        }
    }
}
