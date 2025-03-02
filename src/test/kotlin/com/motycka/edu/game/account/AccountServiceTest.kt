package com.motycka.edu.game.account

import com.motycka.edu.game.config.SecurityContextHolderHelper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithMockUser

class AccountServiceTest {

    private val accountRepository: AccountRepository = mockk()
    private val accountService: AccountService = AccountService(
        accountRepository = accountRepository
    )

    @BeforeEach
    fun setUp() {
        SecurityContextHolderHelper.setSecurityContext(AccountFixtures.DEVELOPER)
        every { accountRepository.selectByUsername(AccountFixtures.DEVELOPER.username) } returns AccountFixtures.DEVELOPER
        every { accountRepository.selectByUsername(AccountFixtures.UNKNOWN) } returns null
    }

    @Test
    fun `getByUsername should return account when found`() {
        val result = accountService.getByUsername(AccountFixtures.DEVELOPER.username)
        assertEquals(AccountFixtures.DEVELOPER, result)

        verify { accountRepository.selectByUsername(AccountFixtures.DEVELOPER.username) }
    }

    @Test
    fun `getByUsername should return null when not found`() {
        val result = accountService.getByUsername(AccountFixtures.UNKNOWN)
        assertNull(result)

        verify { accountRepository.selectByUsername(AccountFixtures.UNKNOWN) }
    }

    @Test
    fun `getByUsername should throw IllegalArgumentException for empty username`() {
        val exception = org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            accountService.getByUsername("")
        }
        assertEquals("Username cannot be empty", exception.message)
    }

    @Test
    fun `createAccount should return created account`() {
        every { accountRepository.insertAccount(AccountFixtures.DEVELOPER) } returns AccountFixtures.DEVELOPER

        val result = accountService.createAccount(AccountFixtures.DEVELOPER)
        assertEquals(AccountFixtures.DEVELOPER, result)

        verify { accountRepository.insertAccount(AccountFixtures.DEVELOPER) }
    }

    @Test
    fun `createAccount should throw IllegalArgumentException for empty name`() {
        val invalidAccount = AccountFixtures.DEVELOPER.copy(name = "")
        val exception = org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            accountService.createAccount(invalidAccount)
        }
        assertEquals("Name cannot be empty", exception.message)
    }

    @Test
    fun `createAccount should throw IllegalArgumentException for empty username`() {
        val invalidAccount = AccountFixtures.DEVELOPER.copy(username = "")
        val exception = org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            accountService.createAccount(invalidAccount)
        }
        assertEquals("Username cannot be empty", exception.message)
    }

    @Test
    fun `createAccount should throw IllegalArgumentException for empty password`() {
        val invalidAccount = AccountFixtures.DEVELOPER.copy(password = "")
        val exception = org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            accountService.createAccount(invalidAccount)
        }
        assertEquals("Password cannot be empty", exception.message)
    }

    @Test
    fun `createAccount should throw IllegalStateException if insertion fails`() {
        every { accountRepository.insertAccount(AccountFixtures.DEVELOPER) } returns null

        val exception = org.junit.jupiter.api.assertThrows<IllegalStateException> {
            accountService.createAccount(AccountFixtures.DEVELOPER)
        }
        assertEquals("Failed to create account", exception.message)
    }

    @Test
    @WithMockUser(username = "developer")
    fun `getCurrentAccountId should return account id`() {
        every { accountRepository.selectByUsername(AccountFixtures.DEVELOPER.username) } returns AccountFixtures.DEVELOPER

        val result = accountService.getCurrentAccountId()
        assertEquals(AccountFixtures.DEVELOPER.id, result)

        verify { accountRepository.selectByUsername(AccountFixtures.DEVELOPER.username) }
    }

    @Test
    @WithMockUser(username = "unknown")
    fun `getCurrentAccountId should throw IllegalArgumentException when user not found`() {
        every { accountRepository.selectByUsername(AccountFixtures.UNKNOWN) } returns null

        val exception = org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            accountService.getCurrentAccountId()
        }
        assertEquals("User not found: unknown", exception.message)

        verify { accountRepository.selectByUsername(AccountFixtures.UNKNOWN) }
    }

    @Test
    fun `getCurrentAccountId should throw IllegalStateException when no authenticated user`() {
        // Clear the security context
        SecurityContextHolder.clearContext()

        val exception = org.junit.jupiter.api.assertThrows<IllegalStateException> {
            accountService.getCurrentAccountId()
        }
        assertEquals("No authenticated user found", exception.message)
    }

    @Test
    @WithMockUser(username = "developer")
    fun `getCurrentAccountId should throw IllegalStateException when account ID is null`() {
        every { accountRepository.selectByUsername(AccountFixtures.DEVELOPER.username) } returns AccountFixtures.DEVELOPER.copy(id = null)

        val exception = org.junit.jupiter.api.assertThrows<IllegalStateException> {
            accountService.getCurrentAccountId()
        }
        assertEquals("Account ID not found", exception.message)

        verify { accountRepository.selectByUsername(AccountFixtures.DEVELOPER.username) }
    }
}