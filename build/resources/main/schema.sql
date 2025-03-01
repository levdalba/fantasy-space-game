DROP TABLE IF EXISTS rounds;
DROP TABLE IF EXISTS matches;
DROP TABLE IF EXISTS leaderboard;
DROP TABLE IF EXISTS characters;
DROP TABLE IF EXISTS account;

CREATE TABLE account (
                         id IDENTITY PRIMARY KEY,
                         name VARCHAR(100) NOT NULL,
                         username VARCHAR(50) NOT NULL UNIQUE,
                         password VARCHAR(100) NOT NULL
);

CREATE TABLE characters (
                            id IDENTITY PRIMARY KEY,
                            account_id BIGINT NOT NULL,
                            name VARCHAR(100) NOT NULL,
                            health INT NOT NULL,
                            attack_power INT NOT NULL,
                            stamina INT,
                            defense_power INT,
                            mana INT,
                            healing_power INT,
                            character_class VARCHAR(20) NOT NULL,
                            level INT NOT NULL,
                            experience INT NOT NULL,
                            should_level_up BOOLEAN NOT NULL
);

CREATE TABLE leaderboard (
                             character_id BIGINT PRIMARY KEY,
                             wins INT NOT NULL,
                             losses INT NOT NULL,
                             draws INT NOT NULL
);

CREATE TABLE matches (
                         id IDENTITY PRIMARY KEY,
                         challenger_id BIGINT NOT NULL,
                         opponent_id BIGINT NOT NULL,
                         match_outcome VARCHAR(10) NOT NULL,
                         challenger_xp INT NOT NULL,
                         opponent_xp INT NOT NULL
);

CREATE TABLE rounds (
                        id IDENTITY PRIMARY KEY,
                        match_id BIGINT NOT NULL,
                        round_number INT NOT NULL,
                        character_id BIGINT NOT NULL,
                        health_delta INT NOT NULL,
                        stamina_delta INT NOT NULL,
                        mana_delta INT NOT NULL
);
