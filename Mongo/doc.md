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

