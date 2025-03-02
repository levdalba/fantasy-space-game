package com.motycka.edu.game.match

import com.motycka.edu.game.character.Character
import com.motycka.edu.game.character.CharacterService
import com.motycka.edu.game.character.CharacterUpdateRequest
import com.motycka.edu.game.leaderboard.LeaderboardService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import kotlin.math.min
import kotlin.random.Random

private val logger = KotlinLogging.logger {}

@Service
class MatchService(
    private val matchRepository: MatchRepository,
    private val characterService: CharacterService,
    private val leaderboardService: LeaderboardService
) {

    fun createMatch(rounds: Int, challengerId: Long, opponentId: Long, currentUserAccountId: Long): MatchResponse {
        logger.info { "Creating match with $rounds rounds, challengerId=$challengerId, opponentId=$opponentId" }

        // Validate challenger ownership
        val challenger = characterService.getCharacterById(challengerId)
            ?: error("Challenger not found with id=$challengerId")
        if (challenger.accountId != currentUserAccountId) {
            throw IllegalArgumentException("Challenger does not belong to the current user")
        }

        val opponent = characterService.getCharacterById(opponentId)
            ?: error("Opponent not found with id=$opponentId")

        // Simulate match
        val (challengerXp, opponentXp, outcome, roundsList, roundsPlayed) = simulateMatch(rounds, challenger, opponent)
        logger.info { "Match simulated: outcome=$outcome, challengerXp=$challengerXp, opponentXp=$opponentXp, roundsList.size=${roundsList.size}, roundsPlayed=$roundsPlayed" }

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
        logger.info { "Match saved with id=${savedMatch.id}" }

        // Save rounds
        val savedRounds = roundsList.map { it.copy(matchId = savedMatch.id) }.map { matchRepository.saveRound(it) }
        logger.info { "Saved ${savedRounds.size} rounds for match id=${savedMatch.id}" }

        // Update character stats
        updateCharacterStats(challengerId, challengerXp, outcome == "WIN")
        updateCharacterStats(opponentId, opponentXp, outcome == "LOSS")

        // Update leaderboard
        updateLeaderboard(challengerId, opponentId, outcome)

        // Build the response
        val updatedChallenger = characterService.getCharacterById(challengerId)
            ?: error("Challenger not found with id=$challengerId")
        val updatedOpponent = characterService.getCharacterById(opponentId)
            ?: error("Opponent not found with id=$opponentId")

        val roundResponses = savedRounds.map { toRoundResponse(it) }

        return MatchResponse(
            id = savedMatch.id.toString(),
            challenger = CharacterSummary(
                id = updatedChallenger.id.toString(),
                name = updatedChallenger.name,
                characterClass = updatedChallenger.characterClass.name,
                level = updatedChallenger.level.toString(),
                experienceTotal = updatedChallenger.experience,
                experienceGained = challengerXp,
                isVictor = outcome == "WIN"
            ),
            opponent = CharacterSummary(
                id = updatedOpponent.id.toString(),
                name = updatedOpponent.name,
                characterClass = updatedOpponent.characterClass.name,
                level = updatedOpponent.level.toString(),
                experienceTotal = updatedOpponent.experience,
                experienceGained = opponentXp,
                isVictor = outcome == "LOSS"
            ),
            rounds = roundResponses,
            roundsPlayed = roundsPlayed, // Should be 2 based on the battle timeline
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
                    roundsPlayed = rounds.map { it.roundNumber }.maxOrNull() ?: 0, // Calculate rounds played from the database
                    matchOutcome = when (match.matchOutcome) {
                        "WIN" -> "CHALLENGER_WON"
                        "LOSS" -> "OPPONENT_WON"
                        else -> "DRAW"
                    }
                )
            }
        }.filterNotNull()
    }

    private fun simulateMatch(rounds: Int, challenger: Character, opponent: Character): Quintuple<Int, Int, String, List<Round>, Int> {
        val roundsList = mutableListOf<Round>()
        var challengerHealth = challenger.health
        var opponentHealth = opponent.health
        var challengerStamina = challenger.stamina ?: 0
        var opponentStamina = opponent.stamina ?: 0
        var challengerMana = challenger.mana ?: 0
        var opponentMana = opponent.mana ?: 0
        var roundsPlayed = 0

        for (roundNum in 1..rounds) {
            roundsPlayed++

            // Challenger attacks opponent
            val challengerDamage = if (challenger.characterClass == Character.CharacterClass.WARRIOR) {
                (challenger.attackPower - (opponent.defensePower ?: 0)).coerceAtLeast(0)
            } else {
                challenger.attackPower
            }
            opponentHealth -= challengerDamage
            val challengerStaminaDelta = if (challenger.characterClass == Character.CharacterClass.WARRIOR) -5 else 0
            val challengerManaDelta = if (challenger.characterClass == Character.CharacterClass.SORCERER) -5 else 0
            challengerStamina += challengerStaminaDelta
            challengerMana += challengerManaDelta

            val challengerRound = Round(
                matchId = 0,
                roundNumber = roundNum,
                characterId = challenger.id,
                healthDelta = 0,
                staminaDelta = challengerStaminaDelta,
                manaDelta = challengerManaDelta
            )

            // Opponent attacks challenger
            val opponentDamage = if (opponent.characterClass == Character.CharacterClass.SORCERER) {
                opponent.attackPower
            } else {
                (opponent.attackPower - (challenger.defensePower ?: 0)).coerceAtLeast(0)
            }
            challengerHealth -= opponentDamage
            val opponentStaminaDelta = if (opponent.characterClass == Character.CharacterClass.WARRIOR) -5 else 0
            val opponentManaDelta = if (opponent.characterClass == Character.CharacterClass.SORCERER) -5 else 0
            opponentStamina += opponentStaminaDelta
            opponentMana += opponentManaDelta

            // Update the challenger's round with the actual health delta
            challengerRound.healthDelta = -opponentDamage
            roundsList.add(challengerRound)

            val opponentRound = Round(
                matchId = 0,
                roundNumber = roundNum,
                characterId = opponent.id,
                healthDelta = -challengerDamage,
                staminaDelta = opponentStaminaDelta,
                manaDelta = opponentManaDelta
            )
            roundsList.add(opponentRound)

            // Check if match should end early
            if (challengerHealth <= 0 || opponentHealth <= 0) {
                break
            }
        }

        // Determine outcome based on health
        val outcome = when {
            challengerHealth <= 0 && opponentHealth <= 0 -> "DRAW"
            challengerHealth <= 0 -> "LOSS"
            opponentHealth <= 0 -> "WIN"
            else -> {
                when {
                    challengerHealth > opponentHealth -> "WIN"
                    opponentHealth > challengerHealth -> "LOSS"
                    else -> "DRAW"
                }
            }
        }

        // Calculate XP based on rounds played
        val challengerXp = min(roundsPlayed * 10, 100)
        val opponentXp = min(roundsPlayed * 5, 50)

        logger.info { "Match simulation completed: challengerHealth=$challengerHealth, opponentHealth=$opponentHealth, outcome=$outcome, roundsPlayed=$roundsPlayed" }

        return Quintuple(challengerXp, opponentXp, outcome, roundsList, roundsPlayed)
    }

    private fun updateCharacterStats(characterId: Long, xpGained: Int, isVictor: Boolean) {
        val character = characterService.getCharacterById(characterId) ?: return
        val newExp = character.experience + xpGained
        val newShouldLevelUp = newExp >= (character.level * 1000)
        val updated = character.copy(experience = newExp, shouldLevelUp = newShouldLevelUp)
        characterService.updateCharacter(characterId, CharacterUpdateRequest(
            name = updated.name,
            health = updated.health,
            attackPower = updated.attackPower,
            stamina = updated.stamina,
            defensePower = updated.defensePower,
            mana = updated.mana,
            healingPower = updated.healingPower,
            experience = updated.experience,
            shouldLevelUp = updated.shouldLevelUp
        )
        )
        logger.info { "Updated character stats for characterId=$characterId: experience=${updated.experience}, shouldLevelUp=$newShouldLevelUp" }
    }

    private fun updateLeaderboard(challengerId: Long, opponentId: Long, outcome: String) {
        when (outcome) {
            "WIN" -> {
                leaderboardService.recordWin(challengerId)
                leaderboardService.recordLoss(opponentId)
                logger.info { "Recorded win for challengerId=$challengerId, loss for opponentId=$opponentId" }
            }
            "LOSS" -> {
                leaderboardService.recordWin(opponentId)
                leaderboardService.recordLoss(challengerId)
                logger.info { "Recorded win for opponentId=$opponentId, loss for challengerId=$challengerId" }
            }
            else -> {
                leaderboardService.recordDraw(challengerId)
                leaderboardService.recordDraw(opponentId)
                logger.info { "Recorded draw for challengerId=$challengerId and opponentId=$opponentId" }
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

data class Quintuple<A, B, C, D, E>(val first: A, val second: B, val third: C, val fourth: D, val fifth: E)