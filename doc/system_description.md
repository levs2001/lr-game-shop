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
* Остальное обсудить с Юлией Александровной.

### Хранение статических полей
Статические данные имеют фиксированный размер, не зависят от языка. Их будем хранить в документоориентированной БД Mongo.
* game_id
* developer
* publisher
* release_date
* genre
* platform
* rating
* image_key - ключ для s3, где лежат картинки состоит из склеенного id игры и titl-а: 123_God_of_war (префикс пути чтобы поддержать больше одной картинки)

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
* В Mongo есть поле image_key
* По этому image_key храним картинки в blob storage (S3), не забываем к image_key приклеить бакет и "папку": 'games-bucket/images/123_God_of_war'.

games-bucket/images/123_God_of_war - папка своего рода, если нужен аватар, то добавляем постфикс '/avatar':
'games-bucket/images/123_God_of_war/avatar'.

### Итог: используемые БД
* Mongo - основная инфа об игре (статические поля)
* S3 - картинки
* Cassandra - для регионозависимых полей

### Генерация данных
С помощью Mistral уже нагененерировал 100 штук. Для 100 млн можно либо синтетические сделать (на основе 1000 sample-ов из Minstral), либо попробовать dataset найти.

## TODO и Спорные моменты
### TODO
* Нужно дополнить колонки типами (в зависимости от используемой бд и типов там).
* Сформировать API для пользователя, также нужна UI как будто это админка магазина.
* Придумать куда впихнуть графовую БД.

### Вопросы
* Перевод нужен только для колонок title, description
Или может для genre тоже?