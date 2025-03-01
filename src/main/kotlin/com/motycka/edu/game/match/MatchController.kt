package com.motycka.edu.game.match

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/matches")
class MatchController(
    private val matchService: MatchService
) {

    @GetMapping
    fun getAllMatches(): ResponseEntity<List<MatchResponse>> {
        val matches = matchService.getAllMatches()
        return ResponseEntity.ok(matches)
    }

    @PostMapping
    fun createMatch(@RequestBody matchRequest: MatchRequest): ResponseEntity<Any> {
        return try {
            val matchResponse = matchService.createMatch(
                rounds = matchRequest.rounds,
                challengerId = matchRequest.challengerId,
                opponentId = matchRequest.opponentId
            )
            ResponseEntity.status(HttpStatus.CREATED).body(matchResponse)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to e.message))
        }
    }
}
