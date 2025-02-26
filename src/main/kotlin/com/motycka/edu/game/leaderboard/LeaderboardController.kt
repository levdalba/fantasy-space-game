package com.motycka.edu.game.leaderboard

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/leaderboards")
class LeaderboardController(
    private val leaderboardService: LeaderboardService
) {

    @GetMapping
    fun getLeaderboard(@RequestParam(required = false) classFilter: String?): List<LeaderboardEntry> {
        return leaderboardService.getLeaderboard(classFilter)
    }
}
