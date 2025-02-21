package com.motycka.edu.game.match

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class MatchRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    fun saveMatch(match: Match): Match {
        jdbcTemplate.update(
            "INSERT INTO match (challenger_id, opponent_id, match_outcome, challenger_xp, opponent_xp) VALUES (?, ?, ?, ?, ?)",
            match.challengerId, match.opponentId, match.matchOutcome, match.challengerXp, match.opponentXp
        )
        val id = jdbcTemplate.queryForObject("SELECT IDENTITY()", Long::class.java)
        return match.copy(id = id!!)
    }

    fun saveRound(round: Round): Round {
        jdbcTemplate.update(
            "INSERT INTO round (match_id, round_number, character_id, health_delta, stamina_delta, mana_delta) VALUES (?, ?, ?, ?, ?, ?)",
            round.matchId, round.roundNumber, round.characterId, round.healthDelta, round.staminaDelta, round.manaDelta
        )
        val id = jdbcTemplate.queryForObject("SELECT IDENTITY()", Long::class.java)
        return round.copy(id = id!!)
    }

    // The rest of your getAllMatches() method remains the same
    fun getAllMatches(): List<MatchResponse> {
        return jdbcTemplate.query(
            """
            SELECT m.id, m.challenger_id, m.opponent_id, m.match_outcome, m.challenger_xp, m.opponent_xp,
                   c1.id as challenger_character_id, c1.name as challenger_name, c1.class as challenger_class, c1.experience as challenger_experience,
                   c2.id as opponent_character_id, c2.name as opponent_name, c2.class as opponent_class, c2.experience as opponent_experience,
                   r.id as round_id, r.round_number, r.character_id, r.health_delta, r.stamina_delta, r.mana_delta
            FROM match m
            JOIN character c1 ON m.challenger_id = c1.id
            JOIN character c2 ON m.opponent_id = c2.id
            LEFT JOIN round r ON m.id = r.match_id
            ORDER BY m.id, r.round_number
            """
        ) { rs, _ ->
            // Assuming we're collecting rounds for each match
            val matchId = rs.getLong("id")
            val match = matches.getOrPut(matchId) {
                MatchResponse(
                    id = matchId.toString(),
                    challenger = CharacterSummary(
                        id = rs.getString("challenger_character_id"),
                        name = rs.getString("challenger_name"),
                        characterClass = rs.getString("challenger_class"),
                        level = "1", // Adjust based on how you calculate level
                        experienceTotal = rs.getInt("challenger_experience"),
                        experienceGained = rs.getInt("challenger_xp"),
                        isVictor = rs.getString("match_outcome") == "WIN"
                    ),
                    opponent = CharacterSummary(
                        id = rs.getString("opponent_character_id"),
                        name = rs.getString("opponent_name"),
                        characterClass = rs.getString("opponent_class"),
                        level = "1", // Adjust based on how you calculate level
                        experienceTotal = rs.getInt("opponent_experience"),
                        experienceGained = rs.getInt("opponent_xp"),
                        isVictor = rs.getString("match_outcome") == "LOSS"
                    ),
                    rounds = mutableListOf()
                )
            }
            // Add round if exists
            if (!rs.wasNull()) {
                (match.rounds as MutableList).add(
                    Round(
                        id = rs.getLong("round_id"),
                        matchId = matchId,
                        roundNumber = rs.getInt("round_number"),
                        characterId = rs.getLong("character_id"),
                        healthDelta = rs.getInt("health_delta"),
                        staminaDelta = rs.getInt("stamina_delta"),
                        manaDelta = rs.getInt("mana_delta")
                    )
                )
            }
            match
        }.distinctBy { it.id } // Remove duplicates since we might have multiple rows per match due to rounds
    }

    private val matches = mutableMapOf<Long, MatchResponse>()
}