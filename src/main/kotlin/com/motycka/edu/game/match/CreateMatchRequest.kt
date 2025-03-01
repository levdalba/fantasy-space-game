package com.motycka.edu.game.match

data class CreateMatchRequest(
    val rounds: Int,
    val challengerId: Long,
    val opponentId: Long
)