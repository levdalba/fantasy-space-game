package com.motycka.edu.game.match

data class MatchResponse(
    val id: String,
    val challenger: CharacterSummary,
    val opponent: CharacterSummary,
    val rounds: List<RoundResponse>,
    val matchOutcome: String
)
