package com.motycka.edu.game.account

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ContextConfiguration
import javax.sql.DataSource

@JdbcTest
@ContextConfiguration(classes = [AccountRepository::class])
class AccountRepositoryTest {

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Autowired
    private lateinit var dataSource: DataSource

    @BeforeEach
    fun setUp() {
        // Insert test data
        jdbcTemplate.update(
            "INSERT INTO account (name, username, password) VALUES (?, ?, ?)",
            AccountFixtures.DEVELOPER.name,
            AccountFixtures.DEVELOPER.username,
            AccountFixtures.DEVELOPER.password
        )
    }

    @Test
    fun `insertAccount should return inserted account`() {
        val result = accountRepository.insertAccount(AccountFixtures.TESTER)

        assertNotNull(result)
        assertNotNull(result?.id)
        assertEquals(AccountFixtures.TESTER.copy(id = result?.id), result)
    }

    @Test
    fun `selectByUsername should return account when found`() {
        val result = accountRepository.selectByUsername(AccountFixtures.DEVELOPER.username)
        assertNotNull(result)
        assertNotNull(result?.id)
        assertEquals(AccountFixtures.DEVELOPER.copy(id = result?.id), result)
    }

    @Test
    fun `selectByUsername should return null when not found`() {
        val result = accountRepository.selectByUsername(AccountFixtures.UNKNOWN)
        assertNull(result)
    }

    @Test
    fun `selectById should return account when found`() {
        // Get the ID of the inserted DEVELOPER account
        val insertedId = jdbcTemplate.queryForObject("SELECT id FROM account WHERE username = ?", Long::class.java, AccountFixtures.DEVELOPER.username)
        val result = accountRepository.selectById(insertedId)
        assertNotNull(result)
        assertEquals(AccountFixtures.DEVELOPER.copy(id = insertedId), result)
    }

    @Test
    fun `selectById should return null when not found`() {
        val result = accountRepository.selectById(999L) // Non-existent ID
        assertNull(result)
    }
}