package com.motycka.edu.game.match
import com.motycka.edu.game.match.Match
import com.motycka.edu.game.match.Round
import com.motycka.edu.game.match.MatchResponse
import com.motycka.edu.game.match.CharacterSummary
import com.motycka.edu.game.character.CharacterService
import org.springframework.stereotype.Service

@Service
class MatchService(
    private val matchRepository: MatchRepository,
    private val characterService: CharacterService
) {

    fun createMatch(rounds: Int, challengerId: Long, opponentId: Long): MatchResponse {
        // Simulate match outcome for now
        val (challengerXp, opponentXp, outcome) = simulateMatch(rounds)

        val match = matchRepository.saveMatch(Match(
            challengerId = challengerId,
            opponentId = opponentId,
            matchOutcome = outcome,
            challengerXp = challengerXp,
            opponentXp = opponentXp
        ))

        val roundsList = (1..rounds).flatMap { roundNumber ->
            listOf(
                Round(matchId = match.id, roundNumber = roundNumber, characterId = challengerId, healthDelta = -10, staminaDelta = -5, manaDelta = 0),
                Round(matchId = match.id, roundNumber = roundNumber, characterId = opponentId, healthDelta = -20, staminaDelta = 0, manaDelta = -10)
            )
        }
        roundsList.forEach { matchRepository.saveRound(it) }

        // Update character stats
        updateCharacterStats(challengerId, challengerXp, outcome == "WIN")
        updateCharacterStats(opponentId, opponentXp, outcome == "LOSS")

        val challenger = characterService.getCharacterById(challengerId)!!
        val opponent = characterService.getCharacterById(opponentId)!!
        return MatchResponse(
            id = match.id.toString(),
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
            rounds = roundsList
        )
    }

    fun getAllMatches(): List<MatchResponse> {
        return matchRepository.getAllMatches()
    }

    private fun simulateMatch(rounds: Int): Triple<Int, Int, String> {
        // Simplified simulation; in reality, this would involve more complex game mechanics
        val challengerXp = (rounds * 10).coerceAtMost(100)
        val opponentXp = (rounds * 5).coerceAtMost(50)
        val outcome = if (Math.random() < 0.5) "WIN" else "LOSS"
        return Triple(challengerXp, opponentXp, outcome)
    }

    private fun updateCharacterStats(characterId: Long, xpGained: Int, isVictor: Boolean) {
        val character = characterService.getCharacterById(characterId)
        character?.let {
            val newExperience = it.experience + xpGained
            val newShouldLevelUp = newExperience >= (it.level * 1000)
            val updatedCharacter = it.copy(
                experience = newExperience,
                shouldLevelUp = newShouldLevelUp
            )
            characterService.updateCharacter(characterId, updatedCharacter)
        }
    }
}