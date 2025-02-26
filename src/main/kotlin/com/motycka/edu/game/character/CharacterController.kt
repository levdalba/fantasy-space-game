package com.motycka.edu.game.character

import com.motycka.edu.game.account.AccountService
import com.motycka.edu.game.account.rest.CharacterRegistrationRequest
import com.motycka.edu.game.account.rest.CharacterResponse
import com.motycka.edu.game.account.rest.toCharacterResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/characters")
class CharacterController(
    private val characterService: CharacterService,
    private val accountService: AccountService
) {

    /**
     * Return all characters owned by the current user.
     * This is the "challengers" list in the UI.
     */
    @GetMapping("/challengers")
    fun getChallengers(): ResponseEntity<List<CharacterResponse>> {
        val currentUserAccountId = accountService.getCurrentAccountId()
        val challengers = characterService.getChallengersForCurrentUser(currentUserAccountId)
        // Always return 200 with an array (even if empty).
        return ResponseEntity.ok(challengers.map { it.toCharacterResponse(currentUserAccountId) })
    }

    /**
     * Return all characters not owned by the current user.
     * This is the "opponents" list in the UI.
     */
    @GetMapping("/opponents")
    fun getOpponents(): ResponseEntity<List<CharacterResponse>> {
        val currentUserAccountId = accountService.getCurrentAccountId()
        val opponents = characterService.getOpponentsForCurrentUser(currentUserAccountId)
        // Always return 200 with an array (even if empty).
        return ResponseEntity.ok(opponents.map { it.toCharacterResponse(currentUserAccountId) })
    }

    /**
     * Return all characters. Supports optional filters:
     * - classFilter: "WARRIOR" or "SORCERER"
     * - nameFilter: partial name match
     */
    @GetMapping
    fun getAllCharacters(
        @RequestParam(required = false) classFilter: String?,
        @RequestParam(required = false) nameFilter: String?
    ): List<CharacterResponse> {
        val currentUserAccountId = accountService.getCurrentAccountId()
        var characters = characterService.getAllCharacters()

        if (!classFilter.isNullOrBlank()) {
            characters = characters.filter { it.characterClass.name == classFilter.uppercase() }
        }
        if (!nameFilter.isNullOrBlank()) {
            characters = characters.filter { it.name.contains(nameFilter, ignoreCase = true) }
        }

        return characters.map { it.toCharacterResponse(currentUserAccountId) }
    }

    /**
     * Return a specific character by ID.
     */
    @GetMapping("/{id}")
    fun getCharacterById(@PathVariable id: Long): ResponseEntity<CharacterResponse> {
        val currentUserAccountId = accountService.getCurrentAccountId()
        val character = characterService.getCharacterById(id)
        return if (character != null) {
            ResponseEntity.ok(character.toCharacterResponse(currentUserAccountId))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    /**
     * Create a new character for the current user.
     */
    @PostMapping
    fun createCharacter(
        @RequestBody characterRequest: CharacterRegistrationRequest
    ): ResponseEntity<CharacterResponse> {
        val currentUserAccountId = accountService.getCurrentAccountId()
        val character = characterService.createCharacter(characterRequest.toCharacter(currentUserAccountId))
        return ResponseEntity.status(HttpStatus.CREATED).body(character.toCharacterResponse(currentUserAccountId))
    }

    /**
     * Update a character by ID. Typically used for leveling up or changing stats.
     */
    @PutMapping("/{id}")
    fun updateCharacter(@PathVariable id: Long, @RequestBody updatedCharacter: Character): ResponseEntity<Character> {
        val character = characterService.updateCharacter(id, updatedCharacter)
        return if (character != null) {
            ResponseEntity.ok(character)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
