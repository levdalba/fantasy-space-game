package com.motycka.edu.game.character

import com.motycka.edu.game.account.rest.CharacterRegistrationRequest
import com.motycka.edu.game.account.rest.CharacterResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/characters")
class CharacterController(
    private val characterService: CharacterService
) {

    @GetMapping
    fun getAllCharacters(
        @RequestParam(required = false) classFilter: String?,
        @RequestParam(required = false) nameFilter: String?
    ): List<CharacterResponse> {
        val currentUserAccountId = getCurrentUserAccountId()
        var characters = characterService.getAllCharacters()

        if (!classFilter.isNullOrBlank()) {
            characters = characters.filter { it.characterClass.name.equals(classFilter, ignoreCase = true) }
        }
        if (!nameFilter.isNullOrBlank()) {
            characters = characters.filter { it.name.contains(nameFilter, ignoreCase = true) }
        }

        return characters.map { CharacterResponse.fromCharacter(it, currentUserAccountId) }
    }

    @GetMapping("/{id}")
    fun getCharacterById(@PathVariable id: Long): ResponseEntity<CharacterResponse> {
        val currentUserAccountId = getCurrentUserAccountId()
        val character = characterService.getCharacterById(id)
        return if (character != null) {
            ResponseEntity.ok(CharacterResponse.fromCharacter(character, currentUserAccountId))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createCharacter(
        @RequestBody characterRequest: CharacterRegistrationRequest
    ): ResponseEntity<CharacterResponse> {
        val currentUserAccountId = getCurrentUserAccountId()
        val character = characterService.createCharacter(characterRequest.toCharacter(currentUserAccountId))
        return ResponseEntity.status(HttpStatus.CREATED).body(CharacterResponse.fromCharacter(character, currentUserAccountId))
    }

    @GetMapping("/challengers")
    fun getChallengers(): ResponseEntity<List<CharacterResponse>> {
        val currentUserAccountId = getCurrentUserAccountId()
        val challengers = characterService.getChallengersForCurrentUser(currentUserAccountId)
        return if (challengers.isNotEmpty()) {
            ResponseEntity.ok(challengers.map { CharacterResponse.fromCharacter(it, currentUserAccountId) })
        } else {
            ResponseEntity.noContent().build()
        }
    }

    @PostMapping("/challengers")
    fun createChallenger(
        @RequestBody characterRequest: CharacterRegistrationRequest
    ): ResponseEntity<CharacterResponse> {
        val currentUserAccountId = getCurrentUserAccountId()
        val character = characterService.createCharacter(characterRequest.toCharacter(currentUserAccountId))
        return ResponseEntity.status(HttpStatus.CREATED).body(CharacterResponse.fromCharacter(character, currentUserAccountId))
    }

    @GetMapping("/opponents")
    fun getOpponents(): ResponseEntity<List<CharacterResponse>> {
        val currentUserAccountId = getCurrentUserAccountId()
        val opponents = characterService.getOpponentsForCurrentUser(currentUserAccountId)
        return if (opponents.isNotEmpty()) {
            ResponseEntity.ok(opponents.map { CharacterResponse.fromCharacter(it, currentUserAccountId) })
        } else {
            ResponseEntity.noContent().build()
        }
    }

    @PutMapping("/{id}")
    fun updateCharacter(
        @PathVariable id: Long,
        @RequestBody updatedCharacter: CharacterRegistrationRequest
    ): ResponseEntity<CharacterResponse> {
        val currentUserAccountId = getCurrentUserAccountId()
        val existingCharacter = characterService.getCharacterById(id) ?: return ResponseEntity.notFound().build()

        // Convert updatedCharacter to Character, preserving class and accountId
        val newCharacter = updatedCharacter.toCharacter(existingCharacter.accountId).copy(
            id = existingCharacter.id,
            level = existingCharacter.level,
            experience = existingCharacter.experience,
            shouldLevelUp = existingCharacter.shouldLevelUp
        )

        val updated = characterService.updateCharacter(id, newCharacter)
        return if (updated != null) {
            ResponseEntity.ok(CharacterResponse.fromCharacter(updated, currentUserAccountId))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    private fun getCurrentUserAccountId(): Long {
        return 1L // Hardcoded for testing; replace with actual logic
    }
}