package com.motycka.edu.game.character

import org.springframework.stereotype.Service

@Service
class CharacterService {

    /**
     * In-memory list for demonstration; replace with a real repository if needed.
     */
    private val characters = mutableListOf<Character>()

    /**
     * Create a new character and add it to the in-memory list.
     */
    fun createCharacter(character: Character): Character {
        val newCharacter = character.copy(id = (characters.size + 1).toLong())
        characters.add(newCharacter)
        return newCharacter
    }

    /**
     * Return all characters.
     */
    fun getAllCharacters(): List<Character> = characters

    /**
     * Return a character by ID or null if not found.
     */
    fun getCharacterById(id: Long): Character? {
        return characters.find { it.id == id }
    }

    /**
     * Return characters owned by the given accountId (challengers).
     */
    fun getChallengersForCurrentUser(accountId: Long): List<Character> {
        return characters.filter { it.accountId == accountId }
    }

    /**
     * Return characters NOT owned by the given accountId (opponents).
     */
    fun getOpponentsForCurrentUser(accountId: Long): List<Character> {
        return characters.filter { it.accountId != accountId }
    }

    /**
     * Update an existing character’s attributes. If 'shouldLevelUp' is true,
     * we call levelUp() to increment level and reset experience.
     */
    fun updateCharacter(id: Long, updatedCharacter: Character): Character? {
        val index = characters.indexOfFirst { it.id == id }
        if (index != -1) {
            val oldCharacter = characters[index]
            val newCharacter = updatedCharacter.copy(
                id = oldCharacter.id,
                accountId = oldCharacter.accountId,
                level = if (oldCharacter.shouldLevelUp) oldCharacter.levelUp().level else oldCharacter.level,
                experience = if (oldCharacter.shouldLevelUp) 0 else oldCharacter.experience
            )
            characters[index] = newCharacter
            return newCharacter
        }
        return null
    }
}
