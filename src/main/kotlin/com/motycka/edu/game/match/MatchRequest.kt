package com.motycka.edu.game.match

data class MatchRequest(
    val rounds: Int,
    val challengerId: Long,
    val opponentId: Long
)