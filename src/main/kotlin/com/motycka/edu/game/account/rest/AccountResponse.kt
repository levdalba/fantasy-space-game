package com.motycka.edu.game.account.rest

import com.motycka.edu.game.account.model.AccountId

data class AccountResponse(
    val id: AccountId,
    val name: String,
    val username: String,
    val password: String
)
