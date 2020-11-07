###Описание:
Работа с Kafka-ой в режиме message-brocker (producer/consumer) с использованием стандартной бинарной сериализации

####Набор микросервисов для запуска в различных окружениях:

1. **dev** - окружение для разработчиков вместе с Kafka
    >docker-compose -f docker-compose.yml -f docker-compose.dev.yml -f confluent-kafka.yml up -d

2. **dev-without-kafka** - окружение для разработчиков без Kafka (считается, что Kafka уже есть на localhost) 
    >docker-compose -f docker-compose.yml -f docker-compose.dev-without-kafka.yml up -d

3. **test** - окружение для тестеров (считается, что Kafka уже есть ...)
    >docker-compose -f docker-compose.yml -f docker-compose.test.yml up -d

4. **prod** - окружение для продакшена (считается, что Kafka уже есть ...)
    >docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d


#####P.S. для запуска окружения **dev-without-kafka** необходимо создать переменную окружения $DOCKERHOST,
В новых версиях Linix будет использоваться встроенная переменная host.docker.internal

>export DOCKERHOST=$(ifconfig | grep -E "([0-9]{1,3}\.){3}[0-9]{1,3}" | grep -v 127.0.0.1 | awk '{ print $2 }' | cut -f2 -d: | head -n1)

####Мониторинг kafka
Для мониторинга kafka используется kafdrop(https://github.com/HomeAdvisor/Kafdrop) и kafka-manager (https://github.com/yahoo/kafka-manager)

Image kafdrop берётся из: thomsch98/kafdrop:latest (https://hub.docker.com/r/thomsch98/kafdrop/)

Image kafka-manager берётся из: hlebalbau/kafka-manager:stable (https://hub.docker.com/r/hlebalbau/kafka-manager/)

Web-интерфейс мониторинга kafdrop:  http://localhost:9010/

Web-интерфейс мониторинга kafka-manager: http://localhost:9000/