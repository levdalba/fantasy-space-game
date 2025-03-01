package com.motycka.edu.game.account

import com.motycka.edu.game.account.model.Account
import com.motycka.edu.game.account.model.AccountId
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class AccountService(
    private val accountRepository: AccountRepository
) {

    fun getAccount(): Account {
        logger.debug { "Getting current user" }
        val currentUserId = getCurrentAccountId()
        return accountRepository.selectById(currentUserId)
            ?: throw UsernameNotFoundException("No user with id=$currentUserId")
    }

    fun getCurrentAccountId(): AccountId {
        val authentication = SecurityContextHolder.getContext().authentication
        val principal = authentication.principal
        if (principal is UserDetails) {
            val account = accountRepository.selectByUsername(principal.username)
                ?: throw UsernameNotFoundException(principal.username)
            return account.id ?: throw UsernameNotFoundException("Account has null ID")
        } else {
            throw IllegalStateException("Unknown principal type: $principal")
        }
    }

    fun createAccount(account: Account): Account {
        logger.debug { "Creating new user: $account" }
        return accountRepository.insertAccount(account)
            ?: error("Account could not be created.")
    }

    fun getByUsername(username: String): Account? {
        logger.debug { "Getting user with username=$username" }
        return accountRepository.selectByUsername(username)
    }
}
