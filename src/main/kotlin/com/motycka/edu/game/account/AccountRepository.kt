package com.motycka.edu.game.account

import com.motycka.edu.game.account.model.Account
import com.motycka.edu.game.account.model.AccountId
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.jdbc.core.JdbcTemplate
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
        jdbcTemplate.update(sql, account.name, account.username, account.password)

        // Retrieve the new ID from H2
        val idSql = "CALL IDENTITY()"
        val newId = jdbcTemplate.queryForObject(idSql, Long::class.java) ?: return null
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
