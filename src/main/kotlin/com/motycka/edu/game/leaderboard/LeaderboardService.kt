package com.motycka.edu.game.leaderboard

import org.springframework.stereotype.Service

@Service
class LeaderboardService(
    private val leaderboardRepository: LeaderboardRepository
) {

    /**
     * Record a win for the given character.
     */
    fun recordWin(characterId: Long) {
        val existing = leaderboardRepository.findByCharacterId(characterId)
        if (existing == null) {
            leaderboardRepository.insert(
                Leaderboard(characterId, wins = 1, losses = 0, draws = 0)
            )
        } else {
            leaderboardRepository.update(
                existing.copy(wins = existing.wins + 1)
            )
        }
    }

    /**
     * Record a loss for the given character.
     */
    fun recordLoss(characterId: Long) {
        val existing = leaderboardRepository.findByCharacterId(characterId)
        if (existing == null) {
            leaderboardRepository.insert(
                Leaderboard(characterId, wins = 0, losses = 1, draws = 0)
            )
        } else {
            leaderboardRepository.update(
                existing.copy(losses = existing.losses + 1)
            )
        }
    }

    /**
     * Record a draw for the given character.
     */
    fun recordDraw(characterId: Long) {
        val existing = leaderboardRepository.findByCharacterId(characterId)
        if (existing == null) {
            leaderboardRepository.insert(
                Leaderboard(characterId, wins = 0, losses = 0, draws = 1)
            )
        } else {
            leaderboardRepository.update(
                existing.copy(draws = existing.draws + 1)
            )
        }
    }

    /**
     * Retrieve the leaderboard, optionally filtered by class.
     * Sort by wins desc, assign positions.
     */
    fun getLeaderboard(filterClass: String?): List<LeaderboardEntry> {
        val entries = leaderboardRepository.getLeaderboardEntries(filterClass)
        // Sort by wins desc, assign positions
        return entries
            .sortedByDescending { it.wins }
            .mapIndexed { index, entry ->
                entry.copy(position = index + 1)
            }
    }
}
