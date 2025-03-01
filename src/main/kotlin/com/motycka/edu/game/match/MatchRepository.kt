package com.motycka.edu.game.match

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
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

        // Use GeneratedKeyHolder to retrieve the generated ID
        val keyHolder: KeyHolder = GeneratedKeyHolder()
        jdbcTemplate.update({ connection ->
            val ps = connection.prepareStatement(sql, arrayOf("id"))
            ps.setLong(1, match.challengerId)
            ps.setLong(2, match.opponentId)
            ps.setString(3, match.matchOutcome)
            ps.setInt(4, match.challengerXp)
            ps.setInt(5, match.opponentXp)
            ps
        }, keyHolder)

        val newId = keyHolder.key?.toLong() ?: 0
        return match.copy(id = newId)
    }

    fun saveRound(round: Round): Round {
        val sql = """
            INSERT INTO rounds (match_id, round_number, character_id, health_delta, stamina_delta, mana_delta)
            VALUES (?, ?, ?, ?, ?, ?)
        """.trimIndent()

        // Use GeneratedKeyHolder to retrieve the generated ID
        val keyHolder: KeyHolder = GeneratedKeyHolder()
        jdbcTemplate.update({ connection ->
            val ps = connection.prepareStatement(sql, arrayOf("id"))
            ps.setLong(1, round.matchId)
            ps.setInt(2, round.roundNumber)
            ps.setLong(3, round.characterId)
            ps.setInt(4, round.healthDelta)
            ps.setInt(5, round.staminaDelta)
            ps.setInt(6, round.manaDelta)
            ps
        }, keyHolder)

        val newId = keyHolder.key?.toLong() ?: 0
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