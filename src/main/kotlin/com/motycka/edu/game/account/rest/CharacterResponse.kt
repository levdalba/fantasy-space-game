package com.motycka.edu.game.account.rest

import com.motycka.edu.game.character.Character

data class CharacterResponse(
    val id: Long,
    val name: String,
    val health: Int,
    val attackPower: Int,
    val stamina: Int? = null,
    val defensePower: Int? = null,
    val mana: Int? = null,
    val healingPower: Int? = null,
    val characterClass: String,
    val level: String, // Changed from Int to String to match README
    val experience: Int,
    val shouldLevelUp: Boolean,
    val isOwner: Boolean
)

fun Character.toCharacterResponse(currentUserAccountId: Long): CharacterResponse {
    return CharacterResponse(
        id = this.id,
        name = this.name,
        health = this.health,
        attackPower = this.attackPower,
        stamina = this.stamina,
        defensePower = this.defensePower,
        mana = this.mana,
        healingPower = this.healingPower,
        characterClass = this.characterClass.name,
        level = this.level.toString(), // Convert Int to String
        experience = this.experience,
        shouldLevelUp = this.shouldLevelUp,
        isOwner = this.accountId == currentUserAccountId
    )
}