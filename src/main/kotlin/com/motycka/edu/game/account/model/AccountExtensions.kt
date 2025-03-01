package com.motycka.edu.game.account.model

import com.motycka.edu.game.account.rest.AccountRegistrationRequest
import com.motycka.edu.game.account.rest.AccountResponse

fun AccountRegistrationRequest.toAccount() = Account(
    id = null,
    name = this.name,
    username = this.username,
    password = this.password
)

fun Account.toAccountResponse() = AccountResponse(
    id = requireNotNull(this.id) { "Account id must not be null" },
    name = this.name,
    username = this.username,
    password = "***" // do not expose real password
)
