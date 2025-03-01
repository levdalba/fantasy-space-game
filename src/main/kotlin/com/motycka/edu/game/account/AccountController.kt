package com.motycka.edu.game.account

import com.motycka.edu.game.account.model.Account
import com.motycka.edu.game.account.rest.AccountRegistrationRequest
import com.motycka.edu.game.account.rest.AccountResponse
import com.motycka.edu.game.account.rest.toAccount
import com.motycka.edu.game.account.rest.toAccountResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/accounts")
class AccountController(
    private val accountService: AccountService,
    private val passwordEncoder: PasswordEncoder // Inject PasswordEncoder
) {

    @GetMapping
    fun getAccount(): AccountResponse {
        return accountService.getAccount().toAccountResponse()
    }

    @PostMapping
    fun postAccount(@RequestBody request: AccountRegistrationRequest): ResponseEntity<AccountResponse> {
        // Encode the password before creating the account
        val accountWithEncodedPassword = request.toAccount().copy(
            password = passwordEncoder.encode(request.password)
        )
        val created = accountService.createAccount(accountWithEncodedPassword)
        return ResponseEntity.status(HttpStatus.CREATED).body(created.toAccountResponse())
    }
}