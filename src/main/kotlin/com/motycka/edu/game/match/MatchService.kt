package com.motycka.edu.game.match

import com.motycka.edu.game.character.CharacterService
import com.motycka.edu.game.leaderboard.LeaderboardService
import org.springframework.stereotype.Service

@Service
class MatchService(
    private val matchRepository: MatchRepository,
    private val characterService: CharacterService,
    private val leaderboardService: LeaderboardService
) {

    /**
     * Create a match, generate rounds, update characters, update leaderboard, and build a MatchResponse.
     */
    fun createMatch(rounds: Int, challengerId: Long, opponentId: Long): MatchResponse {
        // Simple "simulateMatch" logic: random outcome, XP gains.
        val (challengerXp, opponentXp, outcome) = simulateMatch(rounds)

        // Save the match record
        val savedMatch = matchRepository.saveMatch(
            Match(
                challengerId = challengerId,
                opponentId = opponentId,
                matchOutcome = outcome,
                challengerXp = challengerXp,
                opponentXp = opponentXp
            )
        )

        // Generate and save each Round
        val roundsList = (1..rounds).flatMap { roundNumber ->
            listOf(
                Round(
                    matchId = savedMatch.id,
                    roundNumber = roundNumber,
                    characterId = challengerId,
                    healthDelta = -10,
                    staminaDelta = -5,
                    manaDelta = 0
                ),
                Round(
                    matchId = savedMatch.id,
                    roundNumber = roundNumber,
                    characterId = opponentId,
                    healthDelta = -20,
                    staminaDelta = 0,
                    manaDelta = -10
                )
            )
        }
        roundsList.forEach { matchRepository.saveRound(it) }

        // Update character stats (experience, level-up if needed)
        updateCharacterStats(challengerId, challengerXp, outcome == "WIN")
        updateCharacterStats(opponentId, opponentXp, outcome == "LOSS")

        // Update the leaderboard (wins, losses, draws)
        updateLeaderboard(challengerId, opponentId, outcome)

        // Build and return the final JSON response that the UI expects
        val challenger = characterService.getCharacterById(challengerId)
            ?: throw IllegalStateException("Challenger not found with id=$challengerId")
        val opponent = characterService.getCharacterById(opponentId)
            ?: throw IllegalStateException("Opponent not found with id=$opponentId")

        // Convert domain rounds to RoundResponse (UI wants "round" instead of "roundNumber")
        val roundResponses = roundsList.map { toRoundResponse(it) }

        return MatchResponse(
            id = savedMatch.id.toString(),
            challenger = CharacterSummary(
                id = challenger.id.toString(),
                name = challenger.name,
                characterClass = challenger.characterClass.name,
                level = challenger.level.toString(),
                experienceTotal = challenger.experience,
                experienceGained = challengerXp,
                isVictor = outcome == "WIN"
            ),
            opponent = CharacterSummary(
                id = opponent.id.toString(),
                name = opponent.name,
                characterClass = opponent.characterClass.name,
                level = opponent.level.toString(),
                experienceTotal = opponent.experience,
                experienceGained = opponentXp,
                isVictor = outcome == "LOSS"
            ),
            rounds = roundResponses,
            matchOutcome = when (outcome) {
                "WIN" -> "CHALLENGER_WON"
                "LOSS" -> "OPPONENT_WON"
                else -> "DRAW"
            }
        )
    }

    /**
     * Retrieve all matches from the DB, build a list of MatchResponse objects.
     */
    fun getAllMatches(): List<MatchResponse> {
        val matchWithRounds = matchRepository.getAllMatches()
        if (matchWithRounds.isEmpty()) return emptyList()

        return matchWithRounds.map { (match, rounds) ->
            val challenger = characterService.getCharacterById(match.challengerId)
            val opponent = characterService.getCharacterById(match.opponentId)
            if (challenger == null || opponent == null) {
                // If somehow we can't find the characters, skip
                null
            } else {
                // Convert domain rounds to RoundResponse
                val roundResponses = rounds.map { toRoundResponse(it) }

                MatchResponse(
                    id = match.id.toString(),
                    challenger = CharacterSummary(
                        id = challenger.id.toString(),
                        name = challenger.name,
                        characterClass = challenger.characterClass.name,
                        level = challenger.level.toString(),
                        experienceTotal = challenger.experience,
                        experienceGained = match.challengerXp,
                        isVictor = match.matchOutcome == "WIN"
                    ),
                    opponent = CharacterSummary(
                        id = opponent.id.toString(),
                        name = opponent.name,
                        characterClass = opponent.characterClass.name,
                        level = opponent.level.toString(),
                        experienceTotal = opponent.experience,
                        experienceGained = match.opponentXp,
                        isVictor = match.matchOutcome == "LOSS"
                    ),
                    rounds = roundResponses,
                    matchOutcome = when (match.matchOutcome) {
                        "WIN" -> "CHALLENGER_WON"
                        "LOSS" -> "OPPONENT_WON"
                        else -> "DRAW"
                    }
                )
            }
        }.filterNotNull()
    }

    /**
     * A simple random simulation:
     * - challenger gets 10 XP per round (max 100)
     * - opponent gets 5 XP per round (max 50)
     * - outcome is random (WIN or LOSS)
     */
    private fun simulateMatch(rounds: Int): Triple<Int, Int, String> {
        val challengerXp = (rounds * 10).coerceAtMost(100)
        val opponentXp = (rounds * 5).coerceAtMost(50)
        val outcome = if (Math.random() < 0.5) "WIN" else "LOSS"
        return Triple(challengerXp, opponentXp, outcome)
    }

    /**
     * Increase character’s experience, set shouldLevelUp if experience >= level*1000
     */
    private fun updateCharacterStats(characterId: Long, xpGained: Int, isVictor: Boolean) {
        val character = characterService.getCharacterById(characterId) ?: return
        val newExperience = character.experience + xpGained
        val newShouldLevelUp = newExperience >= (character.level * 1000)
        val updatedCharacter = character.copy(
            experience = newExperience,
            shouldLevelUp = newShouldLevelUp
        )
        characterService.updateCharacter(characterId, updatedCharacter)
    }

    /**
     * Update the leaderboard with wins/losses/draws.
     */
    private fun updateLeaderboard(challengerId: Long, opponentId: Long, outcome: String) {
        when (outcome) {
            "WIN" -> {
                // Challenger wins
                leaderboardService.recordWin(challengerId)
                leaderboardService.recordLoss(opponentId)
            }
            "LOSS" -> {
                // Opponent wins
                leaderboardService.recordWin(opponentId)
                leaderboardService.recordLoss(challengerId)
            }
            else -> {
                // Draw
                leaderboardService.recordDraw(challengerId)
                leaderboardService.recordDraw(opponentId)
            }
        }
    }

    /**
     * Transform a domain Round into a RoundResponse that matches the UI’s JSON:
     * "round" instead of "roundNumber", "characterId" as a string.
     */
    private fun toRoundResponse(round: Round): RoundResponse {
        return RoundResponse(
            round = round.roundNumber,
            characterId = round.characterId.toString(),
            healthDelta = round.healthDelta,
            staminaDelta = round.staminaDelta,
            manaDelta = round.manaDelta
        )
    }
}
