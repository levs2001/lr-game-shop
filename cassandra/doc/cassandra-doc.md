# Docs and tutorials
Небольшое видео-туториал (там не докер, но суть та же): https://www.youtube.com/watch?v=MceviB8j1mY&t=41s&ab_channel=DataEngineering

Дока при работе с докером: https://github.com/docker-library/docs/tree/master/cassandra

Дока по подъему через docker-compose: https://javahowtos.com/guides/446-create-cassandra-cluster-with-docker-compose.html

# Making cluster 
Можно поднимать через docker-compose. Там описан кластер из 3х нод. Сиды настроены так, что каждая нода gossip-ит каждую (на маленьком кластере это ок).

Перед запуском кластера соберите image клиента. См. параграф отдельный клиент.

В папке с docker-compose.yml запустите команду
```
docker-compose up
```

После подъема кластера проверьте, что ноды видят друг друга:
```
docker exec -it 1.game-shop.cassandra nodetool status
```

Выключение кластера:
```
docker compose down
```

Не забывайте, что можно посмотреть сколько RAM и cpu тратит каждая нода:
```
docker stats
```

## Ram limit
Я задал лимит RAM для контейнеров кассандры в 3гб. Чтобы приложение кассандры не пыталась использовать больше, я задал 2  env параметра приложения (они задаются в паре):
```
    environment:
      - MAX_HEAP_SIZE=2048M
      - HEAP_NEWSIZE=128M
```
Подробнее про эти параетры тут https://docs.datastax.com/en/cassandra-oss/3.0/cassandra/operations/opsTuneJVM.html

# Исполнение запросов 
## Отдельный клиент
Я создал клиент, который позволяет общаться с кластером кассандры (lr-game-shop/cassandra/client/lr-cassandra-client).

Клиент нужно собрать, как отдельный image, для этого перейдите в папку lr-cassandra-client:
```
cd ..../lr-game-shop/cassandra/client/lr-cassandra-client
```
Соберите jar-ник приложения:
```
./gradlew bootJar
```
Создайте docker image:
```
docker build -t cassandra-client:0.0.1 .
```

Подробнее про упаковку своего приложения в docker контейнер можно почитать тут https://skillbox.ru/media/base/kak_rabotat_s_docker_upakovka_spring_boot_prilozheniya_v_konteyner/

## Исполнение с одной из нод
Подключиесь к консоли одной из нод: 
```
docker exec -it 1.game-shop.cassandra bash
```
Введите команду
```
cqlsh
```

## Отдельный cassandra контейнер
The following command starts another Cassandra container instance and runs cqlsh (Cassandra Query Language Shell) against your original Cassandra container, allowing you to execute CQL statements against your database instance:
```
docker run -it --network some-network --rm cassandra cqlsh some-cassandra
```

# Заливка данных
В файле data.cql команды для созданий keyspace и таблицы.
