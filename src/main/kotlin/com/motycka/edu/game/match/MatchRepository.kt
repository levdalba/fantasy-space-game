package com.motycka.edu.game.match

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class MatchRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    /**
     * Save match to the database and return it with the new ID.
     */
    fun saveMatch(match: Match): Match {
        jdbcTemplate.update(
            """
                INSERT INTO match (challenger_id, opponent_id, match_outcome, challenger_xp, opponent_xp) 
                VALUES (?, ?, ?, ?, ?)
            """.trimIndent(),
            match.challengerId, match.opponentId, match.matchOutcome, match.challengerXp, match.opponentXp
        )
        val id = jdbcTemplate.queryForObject("SELECT IDENTITY()", Long::class.java)
        return match.copy(id = id!!)
    }

    /**
     * Save a single Round to the database.
     */
    fun saveRound(round: Round): Round {
        jdbcTemplate.update(
            """
                INSERT INTO round (match_id, round_number, character_id, health_delta, stamina_delta, mana_delta) 
                VALUES (?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            round.matchId, round.roundNumber, round.characterId, round.healthDelta, round.staminaDelta, round.manaDelta
        )
        val id = jdbcTemplate.queryForObject("SELECT IDENTITY()", Long::class.java)
        return round.copy(id = id!!)
    }

    /**
     * Retrieve all matches plus their rounds in one shot.
     */
    fun getAllMatches(): List<MatchWithRounds> {
        val matches = jdbcTemplate.query(
            """
            SELECT 
                m.id, 
                m.challenger_id, 
                m.opponent_id, 
                m.match_outcome,
                m.challenger_xp,
                m.opponent_xp
            FROM match m
            ORDER BY m.id
            """
        ) { rs, _ ->
            Match(
                id = rs.getLong("id"),
                challengerId = rs.getLong("challenger_id"),
                opponentId = rs.getLong("opponent_id"),
                matchOutcome = rs.getString("match_outcome"),
                challengerXp = rs.getInt("challenger_xp"),
                opponentXp = rs.getInt("opponent_xp")
            )
        }

        if (matches.isEmpty()) return emptyList()

        // Gather all rounds for these matches
        val matchIds = matches.joinToString(", ") { it.id.toString() }
        val rounds = jdbcTemplate.query(
            """
            SELECT
                r.id,
                r.match_id,
                r.round_number,
                r.character_id,
                r.health_delta,
                r.stamina_delta,
                r.mana_delta
            FROM round r
            WHERE r.match_id IN ($matchIds)
            ORDER BY r.match_id, r.round_number
            """
        ) { rs, _ ->
            Round(
                id = rs.getLong("id"),
                matchId = rs.getLong("match_id"),
                roundNumber = rs.getInt("round_number"),
                characterId = rs.getLong("character_id"),
                healthDelta = rs.getInt("health_delta"),
                staminaDelta = rs.getInt("stamina_delta"),
                manaDelta = rs.getInt("mana_delta")
            )
        }

        val roundsByMatchId = rounds.groupBy { it.matchId }

        return matches.map { match ->
            MatchWithRounds(
                match = match,
                rounds = roundsByMatchId[match.id] ?: emptyList()
            )
        }
    }
}

/**
 * Helper class: a Match plus its list of Rounds from the DB.
 */
data class MatchWithRounds(
    val match: Match,
    val rounds: List<Round>
)
