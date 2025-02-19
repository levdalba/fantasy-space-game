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

    @PostMapping
    fun createCharacter(
        @RequestBody characterRequest: CharacterRegistrationRequest
    ): ResponseEntity<CharacterResponse> {
        val character = characterService.createCharacter(characterRequest.toCharacter())
        return ResponseEntity.status(HttpStatus.CREATED).body(character.toCharacterResponse())
    }

    @GetMapping
    fun getAllCharacters(): List<CharacterResponse> {
        return characterService.getAllCharacters().map { it.toCharacterResponse() }
    }

    @GetMapping("/{id}")
    fun getCharacterById(@PathVariable id: Long): ResponseEntity<CharacterResponse> {
        val character = characterService.getCharacterById(id)
        return if (character != null) {
            ResponseEntity.ok(character.toCharacterResponse())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/challengers")
    fun getChallengers(): ResponseEntity<List<CharacterResponse>> {
        val currentUserAccountId = getCurrentUserAccountId()
        val challengers = characterService.getChallengersForCurrentUser(currentUserAccountId)
        return if (challengers.isNotEmpty()) {
            ResponseEntity.ok(challengers.map { it.toCharacterResponse() })
        } else {
            ResponseEntity.noContent().build()
        }
    }

    @PostMapping("/challengers")
    fun createChallenger(
        @RequestBody characterRequest: CharacterRegistrationRequest
    ): ResponseEntity<CharacterResponse> {
        // Ensure the character is associated with the current user
        val currentUserAccountId = getCurrentUserAccountId()
        val character = characterRequest.toCharacter().copy(accountId = currentUserAccountId)
        val createdCharacter = characterService.createCharacter(character)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCharacter.toCharacterResponse())
    }

    private fun getCurrentUserAccountId(): Long {
        // For now, simulate with a hardcoded value. In a real app, use SecurityContextHolder:
        return 1L // Hardcoded for testing; replace with actual logic
    }
}