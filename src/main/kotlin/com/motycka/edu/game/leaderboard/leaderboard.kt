package com.motycka.edu.game.leaderboard

data class Leaderboard(
    val characterId: Long,
    val wins: Int,
    val losses: Int,
    val draws: Int
)