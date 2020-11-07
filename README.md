EasySalesKafkaTest

##### Команды для получения информации об объектах в kafka

Список топиков:

`kafka-topics --zookeeper <адреса zookeeper'ов> --list`

`kafka-topics --zookeeper virt235:22181,virt235:32181,virt235:42181 --list`

Информация о топике:

`kafka-topics --zookeeper <адреса zookeeper'ов> --describe --topic <имя топика>`

`kafka-topics --zookeeper virt235:22181,virt235:32181,virt235:42181 --describe --topic es-kafka-test-streams-topic-input-users`


Список групп consumer'ов:

`kafka-consumer-groups --bootstrap-server <адреса kafka нод> --list`

`kafka-consumer-groups --bootstrap-server virt235:22181,virt235:32181,virt235:42181 --list`
 
Информация о группе consumer'ов:

`kafka-consumer-groups --bootstrap-server <адреса kafka нод> --describe --group <имя consumer группы>`

`kafka-consumer-groups --bootstrap-server virt235:29092 --describe --group es-kafka-test-schema-registry-consumer-test-2`

###### Пример выполнения с docker-compose

`docker-compose exec <имя сервиса из yml файла> <команда>`

`docker-compose exec kafka-1 kafka-consumer-groups --bootstrap-server virt235:22181,virt235:32181,virt235:42181 --list`