package com.motycka.edu.game.account.rest

data class CharacterRegistrationRequest(
    val name: String,
    val health: Int,
    val attackPower: Int,
    val stamina: Int? = null,
    val defensePower: Int? = null,
    val mana: Int? = null,
    val healingPower: Int? = null,
    val characterClass: String // WARRIOR or SORCERER
)