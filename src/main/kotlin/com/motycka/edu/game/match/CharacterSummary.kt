package com.motycka.edu.game.match

data class CharacterSummary(
    val id: String,
    val name: String,
    val characterClass: String,
    val level: String,
    val experienceTotal: Int,
    val experienceGained: Int,
    val isVictor: Boolean
)