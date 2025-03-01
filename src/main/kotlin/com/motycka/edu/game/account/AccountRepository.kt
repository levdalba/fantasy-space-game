package com.motycka.edu.game.account

import com.motycka.edu.game.account.model.Account
import com.motycka.edu.game.account.model.AccountId
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Repository
import java.sql.ResultSet

private val logger = KotlinLogging.logger {}

@Repository
class AccountRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    fun selectById(id: AccountId): Account? {
        logger.debug { "Selecting user by id=$id" }
        val sql = "SELECT id, name, username, password FROM account WHERE id = ?"
        return jdbcTemplate.query(sql, { rs, _ -> mapRow(rs) }, id).firstOrNull()
    }

    fun selectByUsername(username: String): Account? {
        logger.debug { "Selecting user by username=***" }
        val sql = "SELECT id, name, username, password FROM account WHERE username = ?"
        return jdbcTemplate.query(sql, { rs, _ -> mapRow(rs) }, username).firstOrNull()
    }

    fun insertAccount(account: Account): Account? {
        logger.debug { "Inserting new user ${account.copy(password = "***")}" }
        val sql = """
            INSERT INTO account (name, username, password)
            VALUES (?, ?, ?)
        """.trimIndent()

        // Use GeneratedKeyHolder to retrieve the generated ID
        val keyHolder: KeyHolder = GeneratedKeyHolder()
        jdbcTemplate.update({ connection ->
            val ps = connection.prepareStatement(sql, arrayOf("id"))
            ps.setString(1, account.name)
            ps.setString(2, account.username)
            ps.setString(3, account.password)
            ps
        }, keyHolder)

        val newId = keyHolder.key?.toLong() ?: return null
        return account.copy(id = newId)
    }

    private fun mapRow(rs: ResultSet): Account {
        return Account(
            id = rs.getLong("id"),
            name = rs.getString("name"),
            username = rs.getString("username"),
            password = rs.getString("password")
        )
    }
}