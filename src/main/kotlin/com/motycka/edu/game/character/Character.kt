package com.motycka.edu.game.character

data class Character(
    val id: Long = 0,
    val accountId: Long,
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