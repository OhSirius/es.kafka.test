###Описание:
Работа с бд PostgreSQL

####Набор микросервисов для запуска в различных окружениях:

1. **dev** - окружение для разработчиков вместе с PostgreSQL
    >docker-compose -f docker-compose.yml -f docker-compose.dev.yml -f postgres.yml up -d

2. **dev-without-posgre** - окружение для разработчиков без PostgreSQL (считается, что PostgreSQL уже есть на localhost) 
    >docker-compose -f docker-compose.yml -f docker-compose.dev-without-postgre.yml up -d


#####P.S. для запуска окружения **dev-without-postgre** необходимо создать переменную окружения $DOCKERHOST,
В новых версиях Linix будет использоваться встроенная переменная host.docker.internal

>export DOCKERHOST=$(ifconfig | grep -E "([0-9]{1,3}\.){3}[0-9]{1,3}" | grep -v 127.0.0.1 | awk '{ print $2 }' | cut -f2 -d: | head -n1)

