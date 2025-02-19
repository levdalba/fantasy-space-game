package com.motycka.edu.game.account.rest

data class CharacterResponse(
    val id: Long,
    val name: String,
    val classType: String,
    val health: Int,
    val attack: Int,
    val experience: Int,
    val defense: Int? = null,
    val stamina: Int? = null,
    val healing: Int? = null,
    val mana: Int? = null
)