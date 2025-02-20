package com.motycka.edu.game.character

import org.springframework.stereotype.Service

@Service
class CharacterService {
    private val characters = mutableListOf<Character>()

    fun createCharacter(character: Character): Character {
        val newCharacter = character.copy(id = (characters.size + 1).toLong())
        characters.add(newCharacter)
        return newCharacter
    }

    fun getAllCharacters(): List<Character> = characters

    fun getCharacterById(id: Long): Character? {
        return characters.find { it.id == id }
    }

    fun getChallengersForCurrentUser(accountId: Long): List<Character> {
        return characters.filter { it.accountId == accountId }
    }

    fun getOpponentsForCurrentUser(accountId: Long): List<Character> {
        return characters.filter { it.accountId != accountId }
    }

    fun updateCharacter(id: Long, updatedCharacter: Character): Character? {
        val index = characters.indexOfFirst { it.id == id }
        if (index != -1) {
            val oldCharacter = characters[index]
            val newCharacter = oldCharacter.copy(
                name = updatedCharacter.name,
                health = updatedCharacter.health,
                attackPower = updatedCharacter.attackPower,
                stamina = updatedCharacter.stamina,
                defensePower = updatedCharacter.defensePower,
                mana = updatedCharacter.mana,
                healingPower = updatedCharacter.healingPower,
                level = if (oldCharacter.shouldLevelUp) oldCharacter.levelUp().level else oldCharacter.level,
                experience = if (oldCharacter.shouldLevelUp) 0 else oldCharacter.experience,
                shouldLevelUp = updatedCharacter.shouldLevelUp
            )
            characters[index] = newCharacter
            return newCharacter
        }
        return null
    }
}