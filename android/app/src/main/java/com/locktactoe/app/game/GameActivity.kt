package com.locktactoe.app.game

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.locktactoe.app.ui.theme.LockTacToeTheme

/** Fullscreen game screen. Can be launched from the lock screen or from the app icon. */
class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showOverLockScreenIfNeeded()
        hideSystemBars()
        val mode = if (intent.getStringExtra(EXTRA_MODE) == MODE_TWO_PLAYER) {
            GameMode.TWO_PLAYER
        } else {
            GameMode.ONE_PLAYER
        }
        setContent {
            LockTacToeTheme {
                GameScreen(mode = mode, onExit = { finish() })
            }
        }
    }

    private fun showOverLockScreenIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }
    }

    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    companion object {
        const val EXTRA_MODE = "mode"
        const val MODE_ONE_PLAYER = "one_player"
        const val MODE_TWO_PLAYER = "two_player"
    }
}
