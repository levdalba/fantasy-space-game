package com.motycka.edu.game.match

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class MatchRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    fun saveMatch(match: Match): Match {
        val sql = """
            INSERT INTO matches (challenger_id, opponent_id, match_outcome, challenger_xp, opponent_xp)
            VALUES (?, ?, ?, ?, ?)
        """.trimIndent()
        jdbcTemplate.update(sql,
            match.challengerId,
            match.opponentId,
            match.matchOutcome,
            match.challengerXp,
            match.opponentXp
        )

        // retrieve the new ID
        val newId = jdbcTemplate.queryForObject("CALL IDENTITY()", Long::class.java) ?: 0
        return match.copy(id = newId)
    }

    fun saveRound(round: Round): Round {
        val sql = """
            INSERT INTO rounds (match_id, round_number, character_id, health_delta, stamina_delta, mana_delta)
            VALUES (?, ?, ?, ?, ?, ?)
        """.trimIndent()
        jdbcTemplate.update(
            sql,
            round.matchId,
            round.roundNumber,
            round.characterId,
            round.healthDelta,
            round.staminaDelta,
            round.manaDelta
        )
        val newId = jdbcTemplate.queryForObject("CALL IDENTITY()", Long::class.java) ?: 0
        return round.copy(id = newId)
    }

    fun getAllMatches(): List<MatchWithRounds> {
        // get matches
        val matchesSql = """
            SELECT 
                m.id,
                m.challenger_id,
                m.opponent_id,
                m.match_outcome,
                m.challenger_xp,
                m.opponent_xp
            FROM matches m
            ORDER BY m.id
        """.trimIndent()

        val matches = jdbcTemplate.query(matchesSql) { rs, _ ->
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

        // gather rounds
        val matchIds = matches.joinToString(",") { it.id.toString() }
        val roundsSql = """
            SELECT
                r.id,
                r.match_id,
                r.round_number,
                r.character_id,
                r.health_delta,
                r.stamina_delta,
                r.mana_delta
            FROM rounds r
            WHERE r.match_id IN ($matchIds)
            ORDER BY r.match_id, r.round_number
        """.trimIndent()

        val rounds = jdbcTemplate.query(roundsSql) { rs, _ ->
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
