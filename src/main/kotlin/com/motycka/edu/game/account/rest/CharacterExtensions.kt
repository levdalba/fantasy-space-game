package com.motycka.edu.game.account.rest

import com.motycka.edu.game.character.Character

fun CharacterRegistrationRequest.toCharacter(accountId: Long): Character {
    val charClass = when (this.characterClass.uppercase()) {
        "WARRIOR" -> Character.CharacterClass.WARRIOR
        "SORCERER" -> Character.CharacterClass.SORCERER
        else -> throw IllegalArgumentException("Invalid character class: ${this.characterClass}")
    }
    return Character(
        accountId = accountId,
        name = this.name,
        health = this.health,
        attackPower = this.attackPower,
        stamina = this.stamina,
        defensePower = this.defensePower,
        mana = this.mana,
        healingPower = this.healingPower,
        characterClass = charClass, // Now using the enum
        level = 1, // Default level
        experience = 0, // Default experience
        shouldLevelUp = false // Default
    )
}

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
        characterClass = this.characterClass.name, // Convert enum to String
        level = this.level,
        experience = this.experience,
        shouldLevelUp = this.shouldLevelUp,
        isOwner = this.accountId == currentUserAccountId // Check if current user owns this character
    )
}