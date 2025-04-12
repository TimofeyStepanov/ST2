# Запуск приложения

#### Необходимо выставить в файле хостов

127.0.0.1 keycloak

127.0.0.1 manager

#### В текущей директории выполнить команды

docker swarm init

docker stack deploy -c docker-compose.yml my-stack

### Перейти в Swagger
http://manager:8081/swagger-ui/index.html

### Авторизоваться 

логин: user

пароль: 1234

### Приложение доступно для использования
