package com.motycka.edu.game.character

import com.motycka.edu.game.account.AccountService
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
    private val characterService: CharacterService,
    private val accountService: AccountService
) {

    @GetMapping("/challengers")
    fun getChallengers(): ResponseEntity<List<CharacterResponse>> {
        val currentUserAccountId = accountService.getCurrentAccountId()
        val challengers = characterService.getChallengersForCurrentUser(currentUserAccountId)
        return ResponseEntity.ok(challengers.map { it.toCharacterResponse(currentUserAccountId) })
    }

    @GetMapping("/opponents")
    fun getOpponents(): ResponseEntity<List<CharacterResponse>> {
        val currentUserAccountId = accountService.getCurrentAccountId()
        val opponents = characterService.getOpponentsForCurrentUser(currentUserAccountId)
        return ResponseEntity.ok(opponents.map { it.toCharacterResponse(currentUserAccountId) })
    }

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

    @GetMapping("/{id}")
    fun getCharacterById(@PathVariable id: Long): ResponseEntity<CharacterResponse> {
        val currentUserAccountId = accountService.getCurrentAccountId()
        val character = characterService.getCharacterById(id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(character.toCharacterResponse(currentUserAccountId))
    }

    @PostMapping
    fun createCharacter(@RequestBody request: CharacterRegistrationRequest): ResponseEntity<CharacterResponse> {
        val currentUserAccountId = accountService.getCurrentAccountId()
        val character = characterService.createCharacter(request.toCharacter(currentUserAccountId))
        return ResponseEntity.status(HttpStatus.CREATED).body(character.toCharacterResponse(currentUserAccountId))
    }

    @PutMapping("/{id}")
    fun updateCharacter(@PathVariable id: Long, @RequestBody updated: Character): ResponseEntity<Character> {
        val character = characterService.updateCharacter(id, updated) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(character)
    }
}
