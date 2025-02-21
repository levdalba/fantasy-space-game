package com.motycka.edu.game.match
import com.motycka.edu.game.match.MatchRequest
import com.motycka.edu.game.match.MatchResponse
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
        return if (matches.isNotEmpty()) {
            ResponseEntity.ok(matches)
        } else {
            ResponseEntity.noContent().build()
        }
    }

    @PostMapping
    fun createMatch(
        @RequestBody matchRequest: MatchRequest
    ): ResponseEntity<Any> {
        return try {
            val matchResponse = matchService.createMatch(
                matchRequest.rounds,
                matchRequest.challengerId,
                matchRequest.opponentId
            )
            ResponseEntity.status(HttpStatus.CREATED).body(matchResponse)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse("error", e.message)
            )
        }
    }
}

data class ErrorResponse(
    val type: String,
    val message: String?
)