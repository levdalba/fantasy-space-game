package com.motycka.edu.game.account

import com.motycka.edu.game.account.model.Account
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class AccountService(
    private val accountRepository: AccountRepository
) {

    fun getByUsername(username: String): Account? {
        if (username.isBlank()) {
            throw IllegalArgumentException("Username cannot be empty")
        }
        return accountRepository.selectByUsername(username)
    }

    fun createAccount(account: Account): Account {
        // Validate account fields
        if (account.name.isBlank()) {
            throw IllegalArgumentException("Name cannot be empty")
        }
        if (account.username.isBlank()) {
            throw IllegalArgumentException("Username cannot be empty")
        }
        if (account.password.isBlank()) {
            throw IllegalArgumentException("Password cannot be empty")
        }
        return accountRepository.insertAccount(account) ?: throw IllegalStateException("Failed to create account")
    }

    fun getAccount(): Account {
        val username = SecurityContextHolder.getContext().authentication?.name
            ?: throw IllegalStateException("No authenticated user found")
        return getByUsername(username) ?: throw IllegalArgumentException("User not found: $username")
    }

    fun getCurrentAccountId(): Long {
        val account = getAccount()
        return account.id ?: throw IllegalStateException("Account ID not found")
    }
}