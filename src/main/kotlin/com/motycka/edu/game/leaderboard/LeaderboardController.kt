package com.motycka.edu.game.leaderboard

import com.motycka.edu.game.account.AccountService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/leaderboards")
class LeaderboardController(
    private val leaderboardService: LeaderboardService,
    private val accountService: AccountService
) {

    @GetMapping
    fun getLeaderboard(@RequestParam(required = false) classFilter: String?): List<LeaderboardEntry> {
        val currentUserAccountId = accountService.getCurrentAccountId()
        return leaderboardService.getLeaderboard(classFilter, currentUserAccountId)
    }
}