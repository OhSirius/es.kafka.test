### Описание:
Redis клиент
Redis сервер

#### Сборка сервера
Открыть sbt shell в IntelliJ IDEA
В командной строке sbt выполнить:

    compile
    assembly

jar файлы будет находиться по пути 

    applications/server/target/scala-2.12/es-kafkatest-app-server.jar
    applications/client/target/scala-2.12/es-kafkatest-app-client.jar

#### Сборка docker контейнеров
   Перейти в папку с нужными docker-compose файлами:
   
   cd compose/redis
   ##### сам редис
   1. **dev** - окружение для разработчиков вместе с redis
   
        docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d --build
        
   2. **dev-without** - окружение для разработчиков без redis
        
        docker-compose -f docker-compose.yml -f docker-compose-without-redis.dev.yml -f up -d --build
   
   3. **test** - окружение для тестеров (считается, что redis уже есть на тестовой VM)
    
        docker-compose -f docker-compose.yml -f docker-compose.test.yml up -d --build
     
   4. **prod** - окружение для продакшена (считается, что redis уже есть)
    
        docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d --build

#### Запуск сервера в различных окружениях

   Перейти в папку с нужными docker-compose файлами:
   
   cd compose/redis
   #####Выполнить docker-compose:
   1. **dev**- окружение для разработчиков. Порт сервера - 6379
    
        docker-compose -f docker-compose.yml -f docker-compose.dev.yml -f  redis.yml up -d
    
   2. **test** - окружение для тестирования. IP: virt102.aetp.nn  Порт сервера - 6379
    
        docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
    
   3. **prod** - боевое окружение. Порт сервера - 6379
    
        docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
    

Для запуска окружения ```dev``` необходимо создать переменную окружения ```$DOCKERHOST```,

    export DOCKERHOST=$(ifconfig | grep -E "([0-9]{1,3}\.){3}[0-9]{1,3}" | grep -v 127.0.0.1 | awk '{ print $2 }' | cut -f2 -d: | head -n1)
В новых версиях Linux будет использоваться встроенная переменная host.docker.internal

Для отправки данных в редис необходиимо зайти в контейнер клиента app-client-generic-avro

выполнить: sh run_jar.sh
#### Сервис мониторинга REDIS
Для мониторинга redis используется redmon (https://github.com/steelThread/redmon)
Image берётся из: krsyoung/redmon:latest (https://hub.docker.com/r/krsyoung/redmon/)

#####Выполнить docker-compose:
   docker-compose -f redis-monitor-redmon.test.yml
   
   Web-интерфейс мониторинга: http://localhost:4567/
  
