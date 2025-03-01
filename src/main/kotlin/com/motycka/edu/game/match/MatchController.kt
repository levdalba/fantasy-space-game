package com.motycka.edu.game.match

import com.motycka.edu.game.account.AccountService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/matches")
class MatchController(
    private val matchService: MatchService,
    private val accountService: AccountService
) {
    @PostMapping
    fun createMatch(@RequestBody request: CreateMatchRequest): ResponseEntity<MatchResponse> {
        val currentUserAccountId = accountService.getCurrentAccountId()
        val response = matchService.createMatch(
            rounds = request.rounds,
            challengerId = request.challengerId,
            opponentId = request.opponentId,
            currentUserAccountId = currentUserAccountId
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping
    fun getAllMatches(): List<MatchResponse> {
        return matchService.getAllMatches()
    }
}