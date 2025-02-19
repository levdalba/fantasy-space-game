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
}