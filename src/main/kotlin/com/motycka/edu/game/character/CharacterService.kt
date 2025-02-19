package com.motycka.edu.game.character

import org.springframework.stereotype.Service

@Service
class CharacterService {
    private val characters = mutableListOf<Character>()

    fun createCharacter(character: Character): Character {
        characters.add(character.copy(id = (characters.size + 1).toLong()))
        return characters.last()
    }

    fun getAllCharacters(): List<Character> = characters.map { character ->
        character.copy(shouldLevelUp = character.experience >= (character.level * 1000))
    }

    fun getCharacterById(id: Long): Character? {
        return characters.find { character -> character.id == id }?.let { character ->
            character.copy(shouldLevelUp = character.experience >= (character.level * 1000))
        }
    }

    fun getChallengersForCurrentUser(accountId: Long): List<Character> {
        return characters.filter { character -> character.accountId == accountId }.map { character ->
            character.copy(shouldLevelUp = character.experience >= (character.level * 1000))
        }
    }

    fun getOpponentsForCurrentUser(accountId: Long): List<Character> {
        return characters.filter { character -> character.accountId != accountId }.map { character ->
            character.copy(shouldLevelUp = character.experience >= (character.level * 1000))
        }
    }

    fun updateCharacter(id: Long, updatedCharacter: Character): Character? {
        val index = characters.indexOfFirst { character -> character.id == id }
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
                shouldLevelUp = updatedCharacter.shouldLevelUp && updatedCharacter.experience >= (updatedCharacter.level * 1000)
            )
            characters[index] = newCharacter
            return newCharacter
        }
        return null
    }
}