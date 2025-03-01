package com.motycka.edu.game.character

data class CharacterUpdateRequest(
    val name: String,
    val health: Int,
    val attackPower: Int,
    val stamina: Int? = null,
    val defensePower: Int? = null,
    val mana: Int? = null,
    val healingPower: Int? = null,
    val experience: Int,
    val shouldLevelUp: Boolean
)