### 1.запускаем docker 
### 2. ```docker-compose build
		  docker-compose up```
		  
### Описание 
init.sh:  
Bash скрипт используется для настройки репликации между экземплярами MinIO.  
Сначала он определяет переменные окружения и получает список всех экземпляров.  
Затем он ждет, пока все экземпляры не станут доступны, и проверяет, какие из них уже настроены для репликации,  
а какие нет. Если найдены экземпляры, которые еще не настроены для репликации, скрипт добавляет их к репликации.  
В конце скрипт выводит сообщение о том, были ли добавлены новые экземпляры для репликации или репликация уже настроена.

https://www.youtube.com/watch?v=BynJ7GH39UA&t=328s&ab_channel=mediumguy - гайд по репликации в minio 

### Взаимодействие 

```
<dependency>
    <groupId>io.minio</groupId>
    <artifactId>minio</artifactId>
    <version>8.3.2</version>
</dependency>
```

```
MinioClient minioClient = MinioClient.builder()
        .endpoint("http://localhost:9000")
        .credentials("accessKey", "secretKey")
        .build();
```

```
String bucketName = "my-bucket";
String objectName = "my-image.jpg";
File file = new File("/path/to/image.jpg");

minioClient.putObject(PutObjectArgs.builder()
        .bucket(bucketName)
        .object(objectName)
        .filename(file.getAbsolutePath())
        .build());
```

```
String downloadFilePath = "/path/to/download/image.jpg";

minioClient.getObject(GetObjectArgs.builder()
        .bucket(bucketName)
        .object(objectName)
        .filename(downloadFilePath)
        .build());
```
```
BufferedImage image = ImageIO.read(new File(downloadFilePath));
```

https://www.youtube.com/watch?v=EIbB3BNwDYg&ab_channel=MinIO - гайд как читать картинки
