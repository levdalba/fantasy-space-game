package com.motycka.edu.game.match

data class Match(
    val id: Long = 0,
    val challengerId: Long,
    val opponentId: Long,
    val matchOutcome: String, // "WIN", "LOSS", "DRAW"
    val challengerXp: Int,
    val opponentXp: Int
)
