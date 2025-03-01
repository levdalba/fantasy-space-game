package com.motycka.edu.game.character

import org.springframework.stereotype.Service

@Service
class CharacterService(
    private val characterRepository: CharacterRepository
) {

    fun createCharacter(character: Character): Character {
        character.validate() // Ensure validation is called
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

    fun updateCharacter(id: Long, updateRequest: CharacterUpdateRequest): Character? {
        val existing = characterRepository.findById(id) ?: return null
        val updated = existing.copy(
            name = updateRequest.name,
            health = updateRequest.health,
            attackPower = updateRequest.attackPower,
            stamina = updateRequest.stamina,
            defensePower = updateRequest.defensePower,
            mana = updateRequest.mana,
            healingPower = updateRequest.healingPower,
            experience = updateRequest.experience,
            shouldLevelUp = updateRequest.shouldLevelUp
        )
        updated.validate()
        return characterRepository.updateCharacter(updated)
    }

    fun levelUpCharacter(id: Long, updateRequest: CharacterUpdateRequest): Character? {
        val existing = characterRepository.findById(id) ?: return null
        val updated = existing.levelUp().copy(
            name = updateRequest.name,
            health = updateRequest.health,
            attackPower = updateRequest.attackPower,
            stamina = updateRequest.stamina,
            defensePower = updateRequest.defensePower,
            mana = updateRequest.mana,
            healingPower = updateRequest.healingPower,
            experience = 0 // Reset experience on level-up
        )
        updated.validate()
        return characterRepository.updateCharacter(updated)
    }
}