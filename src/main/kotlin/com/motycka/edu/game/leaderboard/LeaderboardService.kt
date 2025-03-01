package com.motycka.edu.game.leaderboard

import org.springframework.stereotype.Service

@Service
class LeaderboardService(
    private val leaderboardRepository: LeaderboardRepository
) {

    fun recordWin(characterId: Long) {
        val existing = leaderboardRepository.findByCharacterId(characterId)
        if (existing == null) {
            leaderboardRepository.insert(Leaderboard(characterId, wins = 1, losses = 0, draws = 0))
        } else {
            leaderboardRepository.update(existing.copy(wins = existing.wins + 1))
        }
    }

    fun recordLoss(characterId: Long) {
        val existing = leaderboardRepository.findByCharacterId(characterId)
        if (existing == null) {
            leaderboardRepository.insert(Leaderboard(characterId, wins = 0, losses = 1, draws = 0))
        } else {
            leaderboardRepository.update(existing.copy(losses = existing.losses + 1))
        }
    }

    fun recordDraw(characterId: Long) {
        val existing = leaderboardRepository.findByCharacterId(characterId)
        if (existing == null) {
            leaderboardRepository.insert(Leaderboard(characterId, wins = 0, losses = 0, draws = 1))
        } else {
            leaderboardRepository.update(existing.copy(draws = existing.draws + 1))
        }
    }

    fun getLeaderboard(filterClass: String?): List<LeaderboardEntry> {
        val entries = leaderboardRepository.getLeaderboardEntries(filterClass)
        // sort by wins desc, assign positions
        return entries
            .sortedByDescending { it.wins }
            .mapIndexed { index, entry -> entry.copy(position = index + 1) }
    }
}
