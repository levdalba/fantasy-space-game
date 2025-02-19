package com.motycka.edu.game.character

data class Character(
    val id: Long = 0,
    val name: String,
    val health: Int,
    val attackPower: Int,
    val stamina: Int? = null,
    val defensePower: Int? = null,
    val mana: Int? = null,
    val healingPower: Int? = null,
    val characterClass: String, // WARRIOR or SORCERER
    val level: Int = 1, // Default level
    val experience: Int,
    val shouldLevelUp: Boolean = false, // Calculated based on experience
    val accountId: Long // Still needed internally to track ownership
)