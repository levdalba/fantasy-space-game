package com.motycka.edu.game.account.rest

import com.motycka.edu.game.character.Character

data class CharacterRegistrationRequest(
    val name: String,
    val health: Int,
    val attackPower: Int,
    val stamina: Int? = null,
    val defensePower: Int? = null,
    val mana: Int? = null,
    val healingPower: Int? = null,
    val characterClass: String
) {
    fun toCharacter(accountId: Long): Character {
        val charClass = when (characterClass.uppercase()) {
            "WARRIOR" -> Character.CharacterClass.WARRIOR
            "SORCERER" -> Character.CharacterClass.SORCERER
            else -> throw IllegalArgumentException("Invalid character class: $characterClass")
        }
        return Character(
            accountId = accountId,
            name = name,
            health = health,
            attackPower = attackPower,
            stamina = if (charClass == Character.CharacterClass.WARRIOR) stamina else null,
            defensePower = if (charClass == Character.CharacterClass.WARRIOR) defensePower else null,
            mana = if (charClass == Character.CharacterClass.SORCERER) mana else null,
            healingPower = if (charClass == Character.CharacterClass.SORCERER) healingPower else null,
            characterClass = charClass,
            level = 1,
            experience = 0,
            shouldLevelUp = false
        )
    }
}
