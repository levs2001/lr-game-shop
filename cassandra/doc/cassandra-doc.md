# Docs and tutorials
Небольшое видео-туториал (там не докер, но суть та же): https://www.youtube.com/watch?v=MceviB8j1mY&t=41s&ab_channel=DataEngineering

Дока при работе с докером: https://github.com/docker-library/docs/tree/master/cassandra

Дока по подъему через docker-compose: https://javahowtos.com/guides/446-create-cassandra-cluster-with-docker-compose.html

# Making cluster 
Можно поднимать через docker-compose. Там описан кластер из 3х нод. Сиды настроены так, что каждая нода gossip-ит каждую (на маленьком кластере это ок).
В папке с docker-compose.yml запустите команду
```
docker-compose up​ -d
```

После подъема кластера проверьте, что ноды видят друг друга:
```
docker exec -it 1.game-shop.cassandra nodetool status
```

Выключение кластера:
```
docker compose down
```


# Исполнение запросов 
## Отдельный клиент
Рекомендуемый вариант.
https://github.com/DataStax-Examples/java-cassandra-driver-from3x-to4x - тут пример с datastax, надо его посмотреть. Пока у меняне получается
TODO

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

# Client
https://github.com/apache/cassandra-java-driver - connect to cluster with java
https://www.baeldung.com/cassandra-with-java

# TODO
* Разобраться с volume storage, у нас будут большие объемы данных надо понять, где контейнер их хранит и как настраивать размер хранилища.
* Написать клиент