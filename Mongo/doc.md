### 1. запускамем docker 
### 2. в файле etc\hosts (C:\Windows\System32\drivers\etc\hosts на windows)  прописывем:

127.0.0.1       mongo1
127.0.0.1       mongo2
127.0.0.1       mongo3

### 3. переходим в дриректорию с docker-compose файлом с mongo прописываем:
docker-compose build
docker-compose up

поднимается 3 серевера на портах 30001, 30002, 30003 локально и в контейнерах. 

### 4. Доступ к каждому из серверов через mongosh:

docker exec -it mongo1 sh -c "mongo --port 30001"
docker exec -it mongo2 sh -c "mongo --port 30002"
docker exec -it mongo3 sh -c "mongo --port 30003"

winpty docker exec -it mongo1 sh -c "mongo --port 30001" //для windwos добавить winpty 

### 5. Для подключения к кластеру uri:
mongodb://mongo1:30001,mongo2:30002,mongo3:30003/?replicaSet=my-replica-set 
Например через mongo shell: 

mongosh mongodb://mongo1:30001,mongo2:30002,mongo3:30003/?replicaSet=my-replica-set 
Записываем тестовую строку: 
use GameShop
db.Info.insertOne({name: "Vasya", email: "vasya.sobaka.mail.ru"}) 
db.Info.find()


### 6. Или через Питон:
from pymongo import MongoClient
import pandas as pd
client = MongoClient('mongodb://mongo1:30001,mongo2:30002,mongo3:30003/?replicaSet=my-replica-set') 
df = pd.read_csv('test_data.csv')
db = client['GameShop']
collection = db['Info']
collection.insert_many(df.to_dict('records'))

### 7. Ресурсы контейнера 
Озу и CPU  


![image](https://github.com/levs2001/lr-game-shop/assets/86722732/0efecbea-aa7b-46bc-8680-31fc124a8e87)

Память диска задаем в size.  
volumes:  
  - ./data/mongo-1:/data/db,size=100m


### 8. Фактор репликации

Для увеличения фактора репликации MongoDB необходимо добавить новый узел в реплика-сет. Еть три узла (mongo1, mongo2, mongo3), монжно увеличить фактор репликации до 4 или более, добавив новые узлы.

Например добавить новый узел (mongo4) в реплика-сет:

Добавить файл docker-compose:
```
mongo4:
    image: mongo:5
    container_name: mongo4
    command: ["--replSet", "my-replica-set", "--bind_ip_all", "--port", "30004"]
    volumes:
      - ./data/mongo-4:/data/db,size=100m
    ports:
      - 30004:30004
    deploy:
      resources:
        limits:
          memory: 512m
          cpus: '1'
        reservations:
          memory: 256m
          cpus: '0.5'
```

Добавить инициализацию {_id:3,host:\"mongo4:30004\"}, в
 healthcheck:
      test: test $$(echo "rs.initiate({_id:'my-replica-set',members:[{_id:0,host:\"mongo1:30001\"},{_id:1,host:\"mongo2:30002\"},{_id:2,host:\"mongo3:30003\"}, {_id:3,host:\"mongo4:30004\"},]}).ok || rs.status().ok" | mongo --port 30001 --quiet) -eq 1
