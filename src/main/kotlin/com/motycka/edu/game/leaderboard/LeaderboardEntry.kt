package com.motycka.edu.game.leaderboard

import com.motycka.edu.game.account.rest.CharacterResponse

data class LeaderboardEntry(
    val position: Int,
    val character: CharacterResponse,
    val wins: Int,
    val losses: Int,
    val draws: Int
)
