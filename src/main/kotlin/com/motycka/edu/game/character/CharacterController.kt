package com.motycka.edu.game.character

import com.motycka.edu.game.account.rest.CharacterRegistrationRequest
import com.motycka.edu.game.account.rest.CharacterResponse
import com.motycka.edu.game.account.rest.toCharacter
import com.motycka.edu.game.account.rest.toCharacterResponse
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

        // Apply filters
        if (!classFilter.isNullOrBlank()) {
            characters = characters.filter { it.characterClass.equals(classFilter, ignoreCase = true) }
        }
        if (!nameFilter.isNullOrBlank()) {
            characters = characters.filter { it.name.contains(nameFilter, ignoreCase = true) }
        }

        return characters.map { it.toCharacterResponse(currentUserAccountId) }
    }

    @GetMapping("/{id}")
    fun getCharacterById(@PathVariable id: Long): ResponseEntity<CharacterResponse> {
        val currentUserAccountId = getCurrentUserAccountId()
        val character = characterService.getCharacterById(id)
        return if (character != null) {
            ResponseEntity.ok(character.toCharacterResponse(currentUserAccountId))
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
        return ResponseEntity.status(HttpStatus.CREATED).body(character.toCharacterResponse(currentUserAccountId))
    }

    @GetMapping("/challengers")
    fun getChallengers(): ResponseEntity<List<CharacterResponse>> {
        val currentUserAccountId = getCurrentUserAccountId()
        val challengers = characterService.getChallengersForCurrentUser(currentUserAccountId)
        return if (challengers.isNotEmpty()) {
            ResponseEntity.ok(challengers.map { it.toCharacterResponse(currentUserAccountId) })
        } else {
            ResponseEntity.noContent().build()
        }
    }

    @PostMapping("/challengers")
    fun createChallenger(
        @RequestBody characterRequest: CharacterRegistrationRequest
    ): ResponseEntity<CharacterResponse> {
        val currentUserAccountId = getCurrentUserAccountId()
        val character = characterRequest.toCharacter(currentUserAccountId)
        val createdCharacter = characterService.createCharacter(character)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCharacter.toCharacterResponse(currentUserAccountId))
    }

    private fun getCurrentUserAccountId(): Long {
        return 1L // Hardcoded for testing; replace with actual logic
    }
}