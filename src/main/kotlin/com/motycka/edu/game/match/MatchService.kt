package com.motycka.edu.game.match

import com.motycka.edu.game.character.CharacterService
import com.motycka.edu.game.leaderboard.LeaderboardService
import org.springframework.stereotype.Service
import kotlin.math.min

@Service
class MatchService(
    private val matchRepository: MatchRepository,
    private val characterService: CharacterService,
    private val leaderboardService: LeaderboardService
) {

    fun createMatch(rounds: Int, challengerId: Long, opponentId: Long): MatchResponse {
        // Simple "simulateMatch" logic
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

        // Generate rounds and save them
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

        // Update character stats
        updateCharacterStats(challengerId, challengerXp, outcome == "WIN")
        updateCharacterStats(opponentId, opponentXp, outcome == "LOSS")

        // Update leaderboard
        updateLeaderboard(challengerId, opponentId, outcome)

        // Build the response
        val challenger = characterService.getCharacterById(challengerId)
            ?: error("Challenger not found with id=$challengerId")
        val opponent = characterService.getCharacterById(opponentId)
            ?: error("Opponent not found with id=$opponentId")

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

    fun getAllMatches(): List<MatchResponse> {
        val matchWithRounds = matchRepository.getAllMatches()
        if (matchWithRounds.isEmpty()) return emptyList()

        return matchWithRounds.map { (match, rounds) ->
            val challenger = characterService.getCharacterById(match.challengerId)
            val opponent = characterService.getCharacterById(match.opponentId)
            if (challenger == null || opponent == null) {
                null
            } else {
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
     * Random logic: challenger gets 10 XP per round (max 100), opponent gets 5 XP per round (max 50).
     * Outcome is random (WIN or LOSS, no draws).
     */
    private fun simulateMatch(rounds: Int): Triple<Int, Int, String> {
        val challengerXp = min(rounds * 10, 100)
        val opponentXp = min(rounds * 5, 50)
        val outcome = if (Math.random() < 0.5) "WIN" else "LOSS"
        return Triple(challengerXp, opponentXp, outcome)
    }

    private fun updateCharacterStats(characterId: Long, xpGained: Int, isVictor: Boolean) {
        val character = characterService.getCharacterById(characterId) ?: return
        val newExp = character.experience + xpGained
        val newShouldLevelUp = newExp >= (character.level * 1000)
        val updated = character.copy(experience = newExp, shouldLevelUp = newShouldLevelUp)
        characterService.updateCharacter(characterId, updated)
    }

    private fun updateLeaderboard(challengerId: Long, opponentId: Long, outcome: String) {
        when (outcome) {
            "WIN" -> {
                leaderboardService.recordWin(challengerId)
                leaderboardService.recordLoss(opponentId)
            }
            "LOSS" -> {
                leaderboardService.recordWin(opponentId)
                leaderboardService.recordLoss(challengerId)
            }
            else -> {
                leaderboardService.recordDraw(challengerId)
                leaderboardService.recordDraw(opponentId)
            }
        }
    }

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
