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
}