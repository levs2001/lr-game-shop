# Запуск

Клиент может работать только в паре с кассандрой и монгой, соответственно разворачивать
их надо вместе.

Используйте 21ю java [eclipse/temurin](https://projects.eclipse.org/projects/adoptium.temurin). Сбилдите jar-ник:

```shell
./gradlew clean build -x test
```

Сбилдите docker образ:

```shell
docker build -t lr-game-shop-clien:0.0.1 .
```

В папке _compose_ лежит 2 конфига:

* Для кластера с бд, у которых по 1 ноде _./compose/one_node/docker-compose.yml_
* Для кластера с бд, у которых по 3 ноды _./compose/multi_node/docker-compose.yml_

# Использование
Обратите внимание, клиент сам создаст все нужные таблицы и индексы в базах. 
Все взаимодействие должно происходить только через клиент.

Для документации и примеров запросов развернут swagger, если приложение запущено,
он доступен по ссылке: http://localhost:8080/swagger-ui/index.html

Пример запроса на вставку игры:

```
curl -X 'POST' \
  'http://localhost:8080/game-shop/add-game/' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "commonInfo": {
    "gameId": 12345,
    "developer": "string",
    "publisher": "string",
    "releaseDate": "2024-04-17T13:14:43.150Z",
    "genre": "string",
    "platform": "string",
    "rating": 0
  },
  "countryInfos": {
    "RU": {
      "title": "string",
      "description": "string",
      "price": 0
    },
    "EN": {
      "title": "string",
      "description": "string",
      "price": 0
    },
    "FR": {
     "title": "string",
     "description": "string",
     "price": 0
    }
  }
}'
```

# Тестирование

Прежде чем запускать тест подними базы:

```shell
docker-compose -f ./test_cluster/docker-compose.yml up
```

Если какие-то тесты закончились с ошибкой то лучше пересоздать контейнер
перед следующим запуском:

```shell
docker-compose -f ./test_cluster/docker-compose.yml up --force-recreate
```

Дождитесь пока кластер поднимется.