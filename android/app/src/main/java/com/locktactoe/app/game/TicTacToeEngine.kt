package com.locktactoe.app.game

private val WIN_LINES = listOf(
    intArrayOf(0, 1, 2), intArrayOf(3, 4, 5), intArrayOf(6, 7, 8),
    intArrayOf(0, 3, 6), intArrayOf(1, 4, 7), intArrayOf(2, 5, 8),
    intArrayOf(0, 4, 8), intArrayOf(2, 4, 6)
)

data class WinResult(val player: Char, val line: IntArray)

fun findWinner(board: Array<Char?>): WinResult? {
    for (line in WIN_LINES) {
        val (a, b, c) = line
        val v = board[a]
        if (v != null && v == board[b] && v == board[c]) return WinResult(v, line)
    }
    return null
}

fun isBoardFull(board: Array<Char?>): Boolean = board.all { it != null }

/** Unbeatable minimax search for the CPU player. */
fun bestMove(board: Array<Char?>, aiPlayer: Char): Int {
    var bestScore = Int.MIN_VALUE
    var move = -1
    for (i in board.indices) {
        if (board[i] != null) continue
        board[i] = aiPlayer
        val score = minimax(board, false, 0, aiPlayer)
        board[i] = null
        if (score > bestScore) {
            bestScore = score
            move = i
        }
    }
    return move
}

private fun minimax(board: Array<Char?>, isMax: Boolean, depth: Int, aiPlayer: Char): Int {
    val opponent = if (aiPlayer == 'O') 'X' else 'O'
    val winner = findWinner(board)
    if (winner != null) return if (winner.player == aiPlayer) 10 - depth else depth - 10
    if (isBoardFull(board)) return 0

    val current = if (isMax) aiPlayer else opponent
    var best = if (isMax) Int.MIN_VALUE else Int.MAX_VALUE
    for (i in board.indices) {
        if (board[i] != null) continue
        board[i] = current
        val score = minimax(board, !isMax, depth + 1, aiPlayer)
        board[i] = null
        best = if (isMax) maxOf(best, score) else minOf(best, score)
    }
    return best
}
