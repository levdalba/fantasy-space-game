-- Insert sample accounts
INSERT INTO account (name, username, password) VALUES ('Game', 'game', 'game');
INSERT INTO account (name, username, password) VALUES ('Motyka', 'motyka', 'heslo');

-- Insert sample character for testing (for SORCERER) for user 'motyka'
INSERT INTO characters (
    account_id, name, health, attack_power, stamina, defense_power, mana, healing_power, character_class, level, experience, should_level_up
) VALUES (
             (SELECT id FROM account WHERE username = 'motyka'),
             'Moni The Teacher',
             100,
             40,
             NULL,       -- Warriors only; leave as NULL for Sorcerer
             NULL,       -- Warriors only; leave as NULL for Sorcerer
             30,
             30,
             'SORCERER',
             1,
             6000,
             FALSE
         );

-- Insert Harry Potter Characters for user 'game'
INSERT INTO characters (
    account_id, name, health, attack_power, mana, healing_power, experience, character_class, level, should_level_up
) VALUES (
             (SELECT id FROM account WHERE username = 'game'),
             'Harry Potter',
             100,
             40,
             30,
             30,
             0,
             'SORCERER',
             1,
             FALSE
         );

INSERT INTO characters (
    account_id, name, health, attack_power, mana, healing_power, experience, character_class, level, should_level_up
) VALUES (
             (SELECT id FROM account WHERE username = 'game'),
             'Hermione Granger',
             90,
             40,
             40,
             30,
             0,
             'SORCERER',
             1,
             FALSE
         );

INSERT INTO characters (
    account_id, name, health, attack_power, mana, healing_power, experience, character_class, level, should_level_up
) VALUES (
             (SELECT id FROM account WHERE username = 'game'),
             'Ron Weasley',
             120,
             50,
             20,
             10,
             0,
             'SORCERER',
             1,
             FALSE
         );

INSERT INTO characters (
    account_id, name, health, attack_power, mana, healing_power, experience, character_class, level, should_level_up
) VALUES (
             (SELECT id FROM account WHERE username = 'game'),
             'Severus Snape',
             80,
             60,
             30,
             30,
             0,
             'SORCERER',
             1,
             FALSE
         );

INSERT INTO characters (
    account_id, name, health, attack_power, mana, healing_power, experience, character_class, level, should_level_up
) VALUES (
             (SELECT id FROM account WHERE username = 'game'),
             'Albus Dumbledore',
             90,
             40,
             40,
             30,
             0,
             'SORCERER',
             1,
             FALSE
         );

INSERT INTO characters (
    account_id, name, health, attack_power, mana, healing_power, experience, character_class, level, should_level_up
) VALUES (
             (SELECT id FROM account WHERE username = 'game'),
             'Lord Voldemort',
             80,
             80,
             10,
             30,
             0,
             'SORCERER',
             1,
             FALSE
         );

INSERT INTO characters (
    account_id, name, health, attack_power, mana, healing_power, experience, character_class, level, should_level_up
) VALUES (
             (SELECT id FROM account WHERE username = 'game'),
             'Minerva McGonagall',
             100,
             40,
             30,
             30,
             0,
             'SORCERER',
             1,
             FALSE
         );

INSERT INTO characters (
    account_id, name, health, attack_power, mana, healing_power, experience, character_class, level, should_level_up
) VALUES (
             (SELECT id FROM account WHERE username = 'game'),
             'Bellatrix Lestrange',
             80,
             70,
             20,
             30,
             0,
             'SORCERER',
             1,
             FALSE
         );

INSERT INTO characters (
    account_id, name, health, attack_power, mana, healing_power, experience, character_class, level, should_level_up
) VALUES (
             (SELECT id FROM account WHERE username = 'game'),
             'Draco Malfoy',
             100,
             40,
             30,
             30,
             0,
             'SORCERER',
             1,
             FALSE
         );

INSERT INTO characters (
    account_id, name, health, attack_power, mana, healing_power, experience, character_class, level, should_level_up
) VALUES (
             (SELECT id FROM account WHERE username = 'game'),
             'Neville Longbottom',
             130,
             30,
             10,
             30,
             0,
             'SORCERER',
             1,
             FALSE
         );

-- Insert Star Wars Characters for user 'game'
INSERT INTO characters (
    account_id, name, health, attack_power, stamina, defense_power, experience, character_class, level, should_level_up
) VALUES (
             (SELECT id FROM account WHERE username = 'game'),
             'Luke Skywalker',
             110,
             40,
             20,
             30,
             0,
             'WARRIOR',
             1,
             FALSE
         );

INSERT INTO characters (
    account_id, name, health, attack_power, stamina, defense_power, experience, character_class, level, should_level_up
) VALUES (
             (SELECT id FROM account WHERE username = 'game'),
             'Yoda',
             80,
             30,
             50,
             40,
             0,
             'WARRIOR',
             1,
             FALSE
         );

INSERT INTO characters (
    account_id, name, health, attack_power, stamina, defense_power, experience, character_class, level, should_level_up
) VALUES (
             (SELECT id FROM account WHERE username = 'game'),
             'Han Solo',
             120,
             40,
             10,
             30,
             0,
             'WARRIOR',
             1,
             FALSE
         );

INSERT INTO characters (
    account_id, name, health, attack_power, stamina, defense_power, experience, character_class, level, should_level_up
) VALUES (
             (SELECT id FROM account WHERE username = 'game'),
             'Darth Vader',
             100,
             60,
             10,
             30,
             0,
             'WARRIOR',
             1,
             FALSE
         );

INSERT INTO characters (
    account_id, name, health, attack_power, stamina, defense_power, experience, character_class, level, should_level_up
) VALUES (
             (SELECT id FROM account WHERE username = 'game'),
             'Obi-Wan Kenobi',
             100,
             40,
             30,
             30,
             0,
             'WARRIOR',
             1,
             FALSE
         );

INSERT INTO characters (
    account_id, name, health, attack_power, stamina, defense_power, experience, character_class, level, should_level_up
) VALUES (
             (SELECT id FROM account WHERE username = 'game'),
             'Emperor Palpatine',
             80,
             80,
             10,
             30,
             0,
             'WARRIOR',
             1,
             FALSE
         );

INSERT INTO characters (
    account_id, name, health, attack_power, stamina, defense_power, experience, character_class, level, should_level_up
) VALUES (
             (SELECT id FROM account WHERE username = 'game'),
             'Mace Windu',
             110,
             40,
             20,
             30,
             0,
             'WARRIOR',
             1,
             FALSE
         );

INSERT INTO characters (
    account_id, name, health, attack_power, stamina, defense_power, experience, character_class, level, should_level_up
) VALUES (
             (SELECT id FROM account WHERE username = 'game'),
             'Darth Maul',
             90,
             60,
             20,
             30,
             0,
             'WARRIOR',
             1,
             FALSE
         );

INSERT INTO characters (
    account_id, name, health, attack_power, stamina, defense_power, experience, character_class, level, should_level_up
) VALUES (
             (SELECT id FROM account WHERE username = 'game'),
             'Kylo Ren',
             100,
             50,
             20,
             30,
             0,
             'WARRIOR',
             1,
             FALSE
         );

INSERT INTO characters (
    account_id, name, health, attack_power, stamina, defense_power, experience, character_class, level, should_level_up
) VALUES (
             (SELECT id FROM account WHERE username = 'game'),
             'Finn',
             130,
             20,
             10,
             40,
             0,
             'WARRIOR',
             1,
             FALSE
         );
