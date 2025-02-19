package com.motycka.edu.game.account.rest

import com.motycka.edu.game.character.Character
import com.motycka.edu.game.account.rest.CharacterResponse

fun CharacterRegistrationRequest.toCharacter(): Character {
    return Character(
        accountId = this.accountId,
        name = this.name,
        classType = this.classType,
        health = this.health,
        attack = this.attack,
        experience = this.experience,
        defense = this.defense,
        stamina = this.stamina,
        healing = this.healing,
        mana = this.mana
    )
}

fun Character.toCharacterResponse(): CharacterResponse {
    return CharacterResponse(
        id = this.id,
        name = this.name,
        classType = this.classType,
        health = this.health,
        attack = this.attack,
        experience = this.experience,
        defense = this.defense,
        stamina = this.stamina,
        healing = this.healing,
        mana = this.mana
    )
}