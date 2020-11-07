###FAQ

1. Сборка и деплой сервисов в Docker
    > Собираем проект и готовим jar-файлы (см. п.6)
    
    > Заходим в папку compose->__<<процесс>>__->README.md и запускаем необходимое окружение
    
1. Остановка и удаление выполняемых контейнеров
    > docker-compose down         
    
1. Остановка выполняемых контейнеров
    > docker-compose stop         

1. Установка последней версии docker-compose 3.0

    *Удаляем тек. версию:*
    >sudo apt-get remove docker-compose
    
    *Скачиваем и устанавливаем поледнюю*
    >sudo curl -L https://github.com/docker/compose/releases/download/1.23.0/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose
    
    >sudo chmod +x /usr/local/bin/docker-compose
    
2. Права на docker для текущего пользователя
    >sudo usermod -a -G docker $USER
    
    >и перезагрузить компьютер!
    
3. Сборка проекта и создание jar-файлов
    >Заходим в sbt shell и выполняем 2 команды:    
    
    >compile
    
    >assembly    
     
2. Как писать .md файлы?
    >https://help.github.com/articles/basic-writing-and-formatting-syntax/

3. Пересборка docker images и запуск сервисов:
    >docker-compose -f docker-compose.yml -f docker-compose.dev-without-kafka.yml up -d --build
    
4. Заходим в контейнер
    >docker exec -it <container_name> sh
    
5. Просмотр всех images
    >docker images

6. Остановка контейнера
    >docker container stop <container_name>

7. Удаление контейнера
    >docker container rm <container_name>

8. Удаление volumes
    >docker volume rm <my_vol>

9. Удаление image
    >docker image rm -f <container_name>

10. Просмотр логов
    >docker logs <container_name>    