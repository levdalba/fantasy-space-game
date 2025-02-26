package com.motycka.edu.game.match

data class RoundResponse(
    val round: Int,
    val characterId: String,
    val healthDelta: Int,
    val staminaDelta: Int,
    val manaDelta: Int
)