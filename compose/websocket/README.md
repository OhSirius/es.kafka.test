### Описание:
Http и WebSocket сервер на базе AkkaHttp.

#### Сборка сервера
Открыть sbt shell в IntelliJ IDEA или выполнить ```sbt``` в терминале в папке проекта KafkaTest. 
В командной строке sbt выполнить:

    project websocketServer
    compile
    assembly

jar файл будет находиться по пути 

    applications/websocketServer/target/scala-2.12/es-kafkatest-app-websocketserver.jar

#### Сборка docker контейнеров
    cd compose/websocket
    docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d --build
    docker-compose -f docker-compose.yml -f docker-compose.test.yml up -d --build
    docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d --build

#### Запуск сервера в различных окружениях

Перейти в папку с нужными docker-compose файлами:
```
cd compose/websocket
```
Выполнить docker-compose:
1. `dev` - окружение для разработчиков. Порт сервера - 5000 
    ```
    docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
    ```
1. `test` - окружение для тестирования. Порт сервера - 1337
    ```
    docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
    ```
1. `prod` - боевое окружение. Порт сервера - 1337
    ```
    docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
    ```

Для запуска окружения ```dev``` необходимо создать переменную окружения ```$DOCKERHOST```,

    export DOCKERHOST=$(ifconfig | grep -E "([0-9]{1,3}\.){3}[0-9]{1,3}" | grep -v 127.0.0.1 | awk '{ print $2 }' | cut -f2 -d: | head -n1)
В новых версиях Linux будет использоваться встроенная переменная host.docker.internal

#### Список сервисов
 - `http://host:port/random` - http сервис, возвращает случайное число.
 - `ws://host:port/wsEcho` - websocket сервис, возвращает отправленный ему текст
 - `ws://host:port/wsNotificator` - websocket сервис, возвращает отправленный ему текст, плюс периодически отправляет уведомления клиенту.

`host` и `port` соответствуют `WEBSOCKETSERVERHOST` и `WEBSOCKETSERVERPORT` из файлов `docker-compose.*.yml`

Для подключения к http сервисам можно использовать любой браузер.

Для подключения к websocket сервисам рекомендуется использовать websocket клиент с отображением истории принятых сообщений, например - https://chrome.google.com/webstore/detail/browser-websocket-client/mdmlhchldhfnfnkfmljgeinlffmdgkjo