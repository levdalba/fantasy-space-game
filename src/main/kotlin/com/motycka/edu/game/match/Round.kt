package com.motycka.edu.game.match

data class Round(
    val id: Long = 0,
    val matchId: Long,
    val roundNumber: Int,
    val characterId: Long,
    val healthDelta: Int,
    val staminaDelta: Int,
    val manaDelta: Int
)
