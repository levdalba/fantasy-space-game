package com.motycka.edu.game.account

import com.motycka.edu.game.account.model.Account
import com.motycka.edu.game.account.rest.AccountRegistrationRequest
import com.motycka.edu.game.account.rest.AccountResponse
import com.motycka.edu.game.account.rest.toAccount
import com.motycka.edu.game.account.rest.toAccountResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/accounts")
class AccountController(
    private val accountService: AccountService
) {

    @GetMapping
    fun getAccount(): AccountResponse {
        return accountService.getAccount().toAccountResponse()
    }

    @PostMapping
    fun postAccount(@RequestBody request: AccountRegistrationRequest): ResponseEntity<AccountResponse> {
        // Validate input
        if (request.name.isBlank()) {
            throw IllegalArgumentException("Name cannot be empty")
        }
        if (request.username.isBlank()) {
            throw IllegalArgumentException("Username cannot be empty")
        }
        if (request.password.isBlank()) {
            throw IllegalArgumentException("Password cannot be empty")
        }

        // Check for duplicate username
        if (accountService.getByUsername(request.username) != null) {
            throw IllegalArgumentException("Username already exists")
        }

        // No password encoding needed; store as plain text
        val account = request.toAccount()
        val created = accountService.createAccount(account)
        return ResponseEntity.status(HttpStatus.CREATED).body(created.toAccountResponse())
    }
}