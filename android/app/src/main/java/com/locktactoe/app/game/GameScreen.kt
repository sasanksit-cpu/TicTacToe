package com.locktactoe.app.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/** Fullscreen, fully offline tic-tac-toe: human (X) vs. minimax CPU (O). */
@Composable
fun GameScreen(onExit: () -> Unit) {
    BackHandler(onBack = onExit)

    var board by remember { mutableStateOf(arrayOfNulls<Char>(9)) }
    var turn by remember { mutableStateOf('X') }
    var over by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Your turn") }
    var scoreX by remember { mutableIntStateOf(0) }
    var scoreO by remember { mutableIntStateOf(0) }
    var scoreD by remember { mutableIntStateOf(0) }
    var winLine by remember { mutableStateOf<IntArray?>(null) }

    fun newRound() {
        board = arrayOfNulls(9)
        winLine = null
        over = false
        turn = 'X'
        status = "Your turn"
    }

    fun finishRound(winner: WinResult?) {
        over = true
        if (winner != null) {
            winLine = winner.line
            if (winner.player == 'X') scoreX++ else scoreO++
            status = if (winner.player == 'X') "You win!" else "Computer wins!"
        } else {
            scoreD++
            status = "It's a tie!"
        }
    }

    fun place(i: Int, player: Char) {
        if (over || board[i] != null) return
        val next = board.copyOf()
        next[i] = player
        board = next
        val winner = findWinner(board)
        when {
            winner != null -> finishRound(winner)
            isBoardFull(board) -> finishRound(null)
            else -> {
                turn = if (player == 'X') 'O' else 'X'
                status = if (turn == 'X') "Your turn" else "Computer thinking…"
            }
        }
    }

    LaunchedEffect(turn, over) {
        if (!over && turn == 'O') {
            delay(450)
            val move = bestMove(board.copyOf(), 'O')
            if (move >= 0) place(move, 'O')
        }
    }

    LaunchedEffect(over) {
        if (over) {
            delay(2000)
            newRound()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ScoreChip("You (X)", scoreX)
            ScoreChip("Ties", scoreD)
            ScoreChip("CPU (O)", scoreO)
        }
        Spacer(Modifier.height(24.dp))
        Text(status, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(24.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.size(300.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(9) { i ->
                val isWin = winLine?.contains(i) == true
                Box(
                    modifier = Modifier
                        .size(94.dp)
                        .background(if (isWin) Color(0xFF2E7D32) else Color(0xFF1C1C1C))
                        .clickable(enabled = !over && turn == 'X' && board[i] == null) { place(i, 'X') },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = board[i]?.toString() ?: "",
                        color = if (board[i] == 'X') Color(0xFF4FC3F7) else Color(0xFFFF8A65),
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(Modifier.height(32.dp))
        TextButton(onClick = onExit) {
            Text("← Back", color = Color.Gray)
        }
    }
}

@Composable
private fun ScoreChip(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.Gray, fontSize = 12.sp)
        Text(value.toString(), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}
