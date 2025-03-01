package com.motycka.edu.game.account.rest

data class AccountRegistrationRequest(
    val name: String,
    val username: String,
    val password: String
)
