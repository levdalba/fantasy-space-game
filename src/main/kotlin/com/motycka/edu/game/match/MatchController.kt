package com.motycka.edu.game.match

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/matches")
class MatchController(
    private val matchService: MatchService
) {

    /**
     * Retrieve all matches. The UI will display them under “Matches”.
     */
    @GetMapping
    fun getAllMatches(): ResponseEntity<List<MatchResponse>> {
        val matches = matchService.getAllMatches()
        // Return an empty list if no matches exist so the UI won't fail.
        return ResponseEntity.ok(matches)
    }

    /**
     * Create a new match with the given number of rounds, challengerId, and opponentId.
     */
    @PostMapping
    fun createMatch(@RequestBody matchRequest: MatchRequest): ResponseEntity<Any> {
        return try {
            val matchResponse = matchService.createMatch(
                rounds = matchRequest.rounds,
                challengerId = matchRequest.challengerId,
                opponentId = matchRequest.opponentId
            )
            // Return 201 Created with the new match data
            ResponseEntity.status(HttpStatus.CREATED).body(matchResponse)
        } catch (e: Exception) {
            // If something goes wrong, return 400 with an error message
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to e.message))
        }
    }
}
