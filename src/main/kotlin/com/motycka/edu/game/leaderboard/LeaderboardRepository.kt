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
        val sql = "SELECT character_id, wins, losses, draws FROM leaderboard WHERE character_id = ?"
        return jdbcTemplate.query(sql, { rs, _ -> mapRow(rs) }, characterId).firstOrNull()
    }

    fun insert(leaderboard: Leaderboard) {
        jdbcTemplate.update(
            "INSERT INTO leaderboard (character_id, wins, losses, draws) VALUES (?, ?, ?, ?)",
            leaderboard.characterId, leaderboard.wins, leaderboard.losses, leaderboard.draws
        )
    }

    fun update(leaderboard: Leaderboard) {
        jdbcTemplate.update(
            "UPDATE leaderboard SET wins = ?, losses = ?, draws = ? WHERE character_id = ?",
            leaderboard.wins, leaderboard.losses, leaderboard.draws, leaderboard.characterId
        )
    }

    /**
     * Retrieve all leaderboard entries, optionally filter by class.
     */
    fun getLeaderboardEntries(filterClass: String?): List<LeaderboardEntry> {
        // We join the 'character' table to get needed character info.
        // "class" is a reserved word in Kotlin, so in the DB we have it as "class".
        val baseSql = """
            SELECT lb.character_id,
                   lb.wins,
                   lb.losses,
                   lb.draws,
                   c.name,
                   c.class,
                   c.health,
                   c.attack,
                   c.defense,
                   c.stamina,
                   c.healing,
                   c.mana,
                   c.experience
            FROM leaderboard lb
            JOIN character c ON lb.character_id = c.id
        """.trimIndent()

        val conditions = mutableListOf<String>()
        val params = mutableListOf<Any>()
        if (!filterClass.isNullOrBlank()) {
            conditions.add("c.class = ?")
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
        val characterClass = rs.getString("class")
        val characterResponse = CharacterResponse(
            id = characterId,
            name = rs.getString("name"),
            health = rs.getInt("health"),
            attackPower = rs.getInt("attack"),
            stamina = rs.getInt("stamina").takeIf { !rs.wasNull() },
            defensePower = rs.getInt("defense").takeIf { !rs.wasNull() },
            mana = rs.getInt("mana").takeIf { !rs.wasNull() },
            healingPower = rs.getInt("healing").takeIf { !rs.wasNull() },
            characterClass = characterClass,
            level = 1,            // In a more advanced setup, you'd store level in the DB
            experience = rs.getInt("experience"),
            shouldLevelUp = false,
            isOwner = false       // We can't determine ownership from here, or you can join "account_id" if needed
        )
        return LeaderboardEntry(
            position = 0,  // will be set later
            character = characterResponse,
            wins = wins,
            losses = losses,
            draws = draws
        )
    }
}

/**
 * Minimal data class for leaderboard row.
 */
data class Leaderboard(
    val characterId: Long,
    val wins: Int,
    val losses: Int,
    val draws: Int
)
