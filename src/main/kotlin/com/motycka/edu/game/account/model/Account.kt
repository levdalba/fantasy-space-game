package com.motycka.edu.game.account.model

typealias MyAccountId = Long

data class Account(
    val id: MyAccountId? = null,
    val name: String,
    val username: String,
    val password: String
)