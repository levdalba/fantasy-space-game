package com.motycka.edu.game.character

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class CharacterRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    fun insertCharacter(character: Character): Character {
        val sql = """
            INSERT INTO characters (
                account_id, name, health, attack_power, stamina, defense_power, mana, healing_power, character_class,
                level, experience, should_level_up
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        // Use GeneratedKeyHolder to retrieve the generated ID
        val keyHolder: KeyHolder = GeneratedKeyHolder()
        jdbcTemplate.update({ connection ->
            val ps = connection.prepareStatement(sql, arrayOf("id"))
            ps.setLong(1, character.accountId)
            ps.setString(2, character.name)
            ps.setInt(3, character.health)
            ps.setInt(4, character.attackPower)
            ps.setObject(5, character.stamina)
            ps.setObject(6, character.defensePower)
            ps.setObject(7, character.mana)
            ps.setObject(8, character.healingPower)
            ps.setString(9, character.characterClass.name)
            ps.setInt(10, character.level)
            ps.setInt(11, character.experience)
            ps.setBoolean(12, character.shouldLevelUp)
            ps
        }, keyHolder)

        val newId = keyHolder.key?.toLong() ?: 0
        return character.copy(id = newId)
    }

    fun findAll(): List<Character> {
        val sql = """
            SELECT
                id, account_id, name, health, attack_power, stamina, defense_power, mana, healing_power,
                character_class, level, experience, should_level_up
            FROM characters
        """.trimIndent()
        return jdbcTemplate.query(sql) { rs, _ -> mapRow(rs) }
    }

    fun findById(id: Long): Character? {
        val sql = """
            SELECT
                id, account_id, name, health, attack_power, stamina, defense_power, mana, healing_power,
                character_class, level, experience, should_level_up
            FROM characters
            WHERE id = ?
        """.trimIndent()
        return jdbcTemplate.query(sql, { rs, _ -> mapRow(rs) }, id).firstOrNull()
    }

    fun updateCharacter(character: Character): Character {
        val sql = """
            UPDATE characters
            SET
                name = ?,
                health = ?,
                attack_power = ?,
                stamina = ?,
                defense_power = ?,
                mana = ?,
                healing_power = ?,
                character_class = ?,
                level = ?,
                experience = ?,
                should_level_up = ?
            WHERE id = ?
        """.trimIndent()
        jdbcTemplate.update(
            sql,
            character.name,
            character.health,
            character.attackPower,
            character.stamina,
            character.defensePower,
            character.mana,
            character.healingPower,
            character.characterClass.name,
            character.level,
            character.experience,
            character.shouldLevelUp,
            character.id
        )
        return character
    }

    fun findByAccountId(accountId: Long): List<Character> {
        val sql = """
            SELECT
                id, account_id, name, health, attack_power, stamina, defense_power, mana, healing_power,
                character_class, level, experience, should_level_up
            FROM characters
            WHERE account_id = ?
        """.trimIndent()
        return jdbcTemplate.query(sql, { rs, _ -> mapRow(rs) }, accountId)
    }

    private fun mapRow(rs: ResultSet): Character {
        return Character(
            id = rs.getLong("id"),
            accountId = rs.getLong("account_id"),
            name = rs.getString("name"),
            health = rs.getInt("health"),
            attackPower = rs.getInt("attack_power"),
            stamina = rs.getInt("stamina").takeIf { !rs.wasNull() },
            defensePower = rs.getInt("defense_power").takeIf { !rs.wasNull() },
            mana = rs.getInt("mana").takeIf { !rs.wasNull() },
            healingPower = rs.getInt("healing_power").takeIf { !rs.wasNull() },
            characterClass = Character.CharacterClass.valueOf(rs.getString("character_class")),
            level = rs.getInt("level"),
            experience = rs.getInt("experience"),
            shouldLevelUp = rs.getBoolean("should_level_up")
        )
    }
}