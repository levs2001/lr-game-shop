# Docs and tutorials
Небольшое видео-туториал (там не докер, но суть та же): https://www.youtube.com/watch?v=MceviB8j1mY&t=41s&ab_channel=DataEngineering

Дока при работе с докером: https://github.com/docker-library/docs/tree/master/cassandra

# Making cluster 
Поднять кластер из 3х нод:
```
docker run --name 1.game-shop -d --network cassandra -e CASSANDRA_SEEDS=1.game-shop,2.game-shop,3.game-shop -e CASSANDRA_CLUSTER_NAME=game-shop cassandra

docker run --name 2.game-shop -d --network cassandra -e CASSANDRA_SEEDS=1.game-shop,2.game-shop,3.game-shop -e CASSANDRA_CLUSTER_NAME=game-shop cassandra

docker run --name 3.game-shop -d --network cassandra -e CASSANDRA_SEEDS=1.game-shop,2.game-shop,3.game-shop -e CASSANDRA_CLUSTER_NAME=game-shop cassandra
```

Поднимайте ноды по очереди, дожидаясь инициализации каждой. Иначе кластер может подняться не взяв в ротацию часть нод.

В файле data.cql команды для созданий keyspace и таблицы.

# Client
https://github.com/apache/cassandra-java-driver - connect to cluster with java
https://www.baeldung.com/cassandra-with-java

# TODO
* Разобраться с volume storage, у нас будут большие объемы данных надо понять, где контейнер их хранит и как настраивать размер хранилища.
* Написать клиент