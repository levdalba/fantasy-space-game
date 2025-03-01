package com.motycka.edu.game.account

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
        val created = accountService.createAccount(request.toAccount())
        return ResponseEntity.status(HttpStatus.CREATED).body(created.toAccountResponse())
    }
}
