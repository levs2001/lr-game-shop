-- Create a keyspace
CREATE KEYSPACE IF NOT EXISTS shop WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : '3' };

-- Create a table
CREATE TABLE game_localization (
    game_id UUID,
    language_code TEXT,
    title TEXT,
    description TEXT,
    PRIMARY KEY ((game_id), language_code)
);

-- Insert some data
INSERT INTO game_localization (game_id, language_code, title, description)
VALUES (uuid(), 'en','The Legend of Zelda: Breath of the Wild', 'Explore a vast open world filled with puzzles, dungeons, and enemies in this critically-acclaimed game.');

INSERT INTO game_localization (game_id, language_code, title, description)
VALUES (uuid(), 'en', 'The Legend of Zelda: Breath of the Wild', 'Explore a vast open world filled with puzzles, dungeons, and enemies in this critically-acclaimed game.');

INSERT INTO game_localization (game_id, language_code, title, description)
VALUES (uuid(), 'en', 'Red Dead Redemption 2', 'Experience the life of an outlaw in the American West in this epic game set in the late 1800s.');

INSERT INTO game_localization (game_id, language_code, title, description)
VALUES (uuid(), 'en', 'The Witcher 3: Wild Hunt', 'Embark on a journey to find your missing daughter in this sprawling, open-world RPG.');

INSERT INTO game_localization (game_id, language_code, title, description)
VALUES (uuid(), 'en', 'Fortnite', 'Battle it out with other players in this popular online multiplayer game.');

INSERT INTO game_localization (game_id, language_code, title, description)
VALUES (uuid(), 'en', 'Minecraft', 'Build, explore, and survive in a blocky, procedurally-generated world.');

INSERT INTO game_localization (game_id, language_code, title, description)
VALUES (uuid(), 'en', 'Grand Theft Auto V', 'Explore the city of Los Santos and its surrounding areas as three different characters in this open-world crime game.');

INSERT INTO game_localization (game_id, language_code, title, description)
VALUES (uuid(), 'en', 'Portal 2', 'Solve puzzles using portals and other tools in this critically-acclaimed game.');

INSERT INTO game_localization (game_id, language_code, title, description)
VALUES (uuid(), 'en', 'Dark Souls III', 'Face challenging enemies and bosses in this dark, atmospheric game.');

INSERT INTO game_localization (game_id, language_code, title, description)
VALUES (uuid(), 'en', 'Overwatch', 'Choose from a diverse cast of heroes and battle it out in this team-based multiplayer game.');

INSERT INTO game_localization (game_id, language_code, title, description)
VALUES (uuid(), 'en', 'The Elder Scrolls V: Skyrim', 'Explore the vast world of Skyrim and become the Dragonborn in this epic RPG.');
