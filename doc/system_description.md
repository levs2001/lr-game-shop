# Описание системы

## ТЗ от преподавателя
Необходимо сформировать базу даннных состоящую из как минимум 3х узлов (3 кластера, в каждом узле своя субд). Необходимо использовать NoSql хранилища.

### Описание предметной области
* Множество компьютерных игр, продаваемых в магазине. Основной информацией об игре
выступает ее название, уникальный идентификатор, дата выхода, описание и изображение(аватар) игры для отображения в магазине. У каждой игры есть своя компания-разработчик и компания-издатель, также каждая игра принадлежит к определенному жанру, для упрощения поиска и группирования схожих игр. В связи с разнообразием стран и народов, возникает необходимость переводить игры на другие языки.

## Описание системы
После анализа ТЗ было сформировано краткое описание системы и предлагаемое решения.

### Необходимые колонки (общая схема)
* **game_id**: A unique identifier for each game in the table. This column is the primary key, which means that each value must be unique and not null.
* **title**: The name of the game. This column is a string of up to 100 characters.
* **developer**: The name of the developer or development team responsible for creating the game. This column is a string of up to 100 characters.
* **publisher**: The name of the publisher or publishing company responsible for releasing the game. This column is a string of up to 100 characters.
* **release_date**: The date when the game was released. This column is a date data type.
* **genre**: The genre or category of the game, such as action, adventure, strategy, etc. This column is a string of up to 50 characters.
* **platform**: The platform or operating system on which the game can be played, such as PC, PlayStation, Xbox, etc. This column is a string of up to 50 characters.
* **description**: A brief description of the game, including its features, gameplay mechanics, and storyline. This column is a text data type, which can store longer strings of text.
* **rating**: The average rating of the game, based on reviews from players and critics. This column is a decimal data type with two digits after the decimal point, which allows for ratings up to 9.99.
* **price**: The current price of the game, in US dollars or another currency. This column is a decimal data type with two digits after the decimal point, which allows for prices up to $999.99.
* **avatar** - image for game.

### API для пользователя
Пользователь не будет знать деталей реализации. 
* Можно будет получить все колонки сразу, указав язык и id игры. 
* Также возможен поиск по полному жанру, издателю, дате игры, разработчику с пагинацией.
* Остальное обсудить с Королевой.

### Хранение статических полей
Статические данные имеют фиксированный размер, не зависят от языка. Их будем хранить в документоориентированной БД Mongo.
* game_id
* developer
* publisher
* release_date
* genre
* platform
* rating

### Хранение регионозависимых полей
* game_id
* language_code (ru, en, etc.)
* title 
* description
* price

CQL запросы в Cassandra (Mistral generated):
```
CREATE TABLE game_localization (
    game_id UUID,
    language_code TEXT,
    title TEXT,
    description TEXT,
    PRIMARY KEY ((game_id), language_code)
);
```
```
SELECT title, description
FROM game_localization
WHERE game_id = 123e4567-e89b-12d3-a456-426614174000 AND language_code = 'en';
```

### Хранение картинок
* Имеем отдельную sql таблицу и S3
* Можно добавить колонку image_id, и по этому id хранить картинки в blob storage (S3).

SQL таблица images:
```
CREATE TABLE images (
    image_id INT PRIMARY KEY,
    game_id INT,
    image_key VARCHAR(255),
    FOREIGN KEY (game_id) REFERENCES computer_games(game_id)
);
```
Вставка картинки происходит в 2 этапа:
1. Вставляем в соответствующий бакет по ключу картинку
2. Вставляем строку images

Пример вставки:
* Загружаем в S3 картинку в my-bucket по ключу 'my-bucket/game-avatars/game1.jpg'
* Вставляем строку в images:
```
INSERT INTO images (game_id, image_key)
VALUES (1, 'my-bucket/game-avatars/game1.jpg');
```

Пример взятия картинки:
```
SELECT c.title, i.image_key
FROM computer_games c
JOIN images i ON c.game_id = i.game_id
WHERE c.game_id = 1;
```
И по этому ключу (image_key) идем в S3.

### Итог: используемые БД
Mongo - основная инфа об игре
S3 - картинки
SQL (можно и что-то другое) - пути к картинкам

Cassandra - для перевода текстов?

### Генерация данных
С помощью Mistral уже нагененерировал 100 штук. Для 100 млн можно либо синтетические сделать (на основе 1000 sample-ов из Minstral), либо попробовать dataset найти.

## TODO и Спорные моменты
### TODO
* Нужно дополнить колонки типами (в зависимости от используемой бд и типов там).

### Анализ ТЗ и колонок:
В связи с разнообразием стран и народов, возникает необходимость переводить игры на другие языки.
Перевод нужен только для колонок title, description
Или может для genre тоже?
Делаем таблицу в Cassandra для регионозависимых полей.

### Вопросы для Королевы
* Перевод нужен только для колонок title, description
Или может для genre тоже?
* Обговорить Api, нужен ли полнотекстовый поиск, или все по полному названию, нужен ли поиск по цене и дате?
