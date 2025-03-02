package com.motycka.edu.game.match

import com.fasterxml.jackson.annotation.JsonProperty

data class MatchResponse(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("challenger")
    val challenger: CharacterSummary,
    @JsonProperty("opponent")
    val opponent: CharacterSummary,
    @JsonProperty("rounds")
    val rounds: List<RoundResponse>,
    @JsonProperty("roundsPlayed")
    val roundsPlayed: Int,
    @JsonProperty("matchOutcome")
    val matchOutcome: String
)