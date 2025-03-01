package com.motycka.edu.game.leaderboard

import com.motycka.edu.game.account.rest.CharacterResponse
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class LeaderboardRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    fun findByCharacterId(characterId: Long): Leaderboard? {
        val sql = """
            SELECT character_id, wins, losses, draws 
            FROM leaderboard 
            WHERE character_id = ?
        """.trimIndent()
        return jdbcTemplate.query(sql, { rs, _ -> mapRow(rs) }, characterId).firstOrNull()
    }

    fun insert(leaderboard: Leaderboard) {
        val sql = """
            INSERT INTO leaderboard (character_id, wins, losses, draws)
            VALUES (?, ?, ?, ?)
        """.trimIndent()
        jdbcTemplate.update(sql, leaderboard.characterId, leaderboard.wins, leaderboard.losses, leaderboard.draws)
    }

    fun update(leaderboard: Leaderboard) {
        val sql = """
            UPDATE leaderboard
            SET wins = ?, losses = ?, draws = ?
            WHERE character_id = ?
        """.trimIndent()
        jdbcTemplate.update(sql, leaderboard.wins, leaderboard.losses, leaderboard.draws, leaderboard.characterId)
    }

    /**
     * Retrieve all leaderboard entries, optionally filter by class.
     */
    fun getLeaderboardEntries(filterClass: String?): List<LeaderboardEntry> {
        // Join the 'characters' table to get needed character info
        val baseSql = """
            SELECT lb.character_id,
                   lb.wins,
                   lb.losses,
                   lb.draws,
                   c.name,
                   c.character_class,
                   c.health,
                   c.attack_power,
                   c.stamina,
                   c.defense_power,
                   c.mana,
                   c.healing_power,
                   c.experience,
                   c.level,
                   c.account_id
            FROM leaderboard lb
            JOIN characters c ON lb.character_id = c.id
        """.trimIndent()

        val conditions = mutableListOf<String>()
        val params = mutableListOf<Any>()
        if (!filterClass.isNullOrBlank()) {
            conditions.add("UPPER(c.character_class) = ?")
            params.add(filterClass.uppercase())
        }

        val sql = if (conditions.isEmpty()) {
            "$baseSql ORDER BY lb.wins DESC"
        } else {
            "$baseSql WHERE ${conditions.joinToString(" AND ")} ORDER BY lb.wins DESC"
        }

        return jdbcTemplate.query(sql, { rs, _ ->
            mapLeaderboardRow(rs)
        }, *params.toTypedArray())
    }

    private fun mapRow(rs: ResultSet): Leaderboard {
        return Leaderboard(
            characterId = rs.getLong("character_id"),
            wins = rs.getInt("wins"),
            losses = rs.getInt("losses"),
            draws = rs.getInt("draws")
        )
    }

    private fun mapLeaderboardRow(rs: ResultSet): LeaderboardEntry {
        val characterId = rs.getLong("character_id")
        val wins = rs.getInt("wins")
        val losses = rs.getInt("losses")
        val draws = rs.getInt("draws")

        val characterResponse = CharacterResponse(
            id = characterId,
            name = rs.getString("name"),
            health = rs.getInt("health"),
            attackPower = rs.getInt("attack_power"),
            stamina = rs.getInt("stamina").takeIf { !rs.wasNull() },
            defensePower = rs.getInt("defense_power").takeIf { !rs.wasNull() },
            mana = rs.getInt("mana").takeIf { !rs.wasNull() },
            healingPower = rs.getInt("healing_power").takeIf { !rs.wasNull() },
            characterClass = rs.getString("character_class"),
            level = rs.getInt("level"),
            experience = rs.getInt("experience"),
            shouldLevelUp = false, // we don't know here
            isOwner = false        // can't determine ownership here
        )

        return LeaderboardEntry(
            position = 0, // assigned later
            character = characterResponse,
            wins = wins,
            losses = losses,
            draws = draws
        )
    }
}
