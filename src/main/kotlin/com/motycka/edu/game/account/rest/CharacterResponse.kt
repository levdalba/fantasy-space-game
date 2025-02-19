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
    val level: Int,
    val experience: Int,
    val shouldLevelUp: Boolean,
    val isOwner: Boolean
) {
    companion object {
        fun fromCharacter(character: Character, currentUserAccountId: Long): CharacterResponse {
            return CharacterResponse(
                id = character.id,
                name = character.name,
                health = character.health,
                attackPower = character.attackPower,
                stamina = character.stamina,
                defensePower = character.defensePower,
                mana = character.mana,
                healingPower = character.healingPower,
                characterClass = character.characterClass.name,
                level = character.level,
                experience = character.experience,
                shouldLevelUp = character.shouldLevelUp,
                isOwner = character.accountId == currentUserAccountId
            )
        }
    }
}