package com.motycka.edu.game.character

import org.springframework.stereotype.Service

@Service
class CharacterService(
    private val characterRepository: CharacterRepository
) {

    fun createCharacter(character: Character): Character {
        return characterRepository.insertCharacter(character)
    }

    fun getAllCharacters(): List<Character> {
        return characterRepository.findAll()
    }

    fun getCharacterById(id: Long): Character? {
        return characterRepository.findById(id)
    }

    fun getChallengersForCurrentUser(accountId: Long): List<Character> {
        return characterRepository.findByAccountId(accountId)
    }

    fun getOpponentsForCurrentUser(accountId: Long): List<Character> {
        return characterRepository.findAll().filter { it.accountId != accountId }
    }

    fun updateCharacter(id: Long, updatedCharacter: Character): Character? {
        val existing = characterRepository.findById(id) ?: return null
        // If the character is flagged to level up, handle that logic
        val finalCharacter = if (updatedCharacter.shouldLevelUp) {
            existing.levelUp().copy(
                // preserve any updated stats (like name changes) if you allow them
                name = updatedCharacter.name,
                health = updatedCharacter.health,
                attackPower = updatedCharacter.attackPower,
                stamina = updatedCharacter.stamina,
                defensePower = updatedCharacter.defensePower,
                mana = updatedCharacter.mana,
                healingPower = updatedCharacter.healingPower,
                experience = updatedCharacter.experience // or reset to 0 if leveling up
            )
        } else {
            // otherwise just keep updates
            existing.copy(
                name = updatedCharacter.name,
                health = updatedCharacter.health,
                attackPower = updatedCharacter.attackPower,
                stamina = updatedCharacter.stamina,
                defensePower = updatedCharacter.defensePower,
                mana = updatedCharacter.mana,
                healingPower = updatedCharacter.healingPower,
                experience = updatedCharacter.experience,
                shouldLevelUp = updatedCharacter.shouldLevelUp
            )
        }
        return characterRepository.updateCharacter(finalCharacter)
    }
}
