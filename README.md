# Телефонный Справочник

Телефонный справочник с интуитивно понятным дизайном, обеспечивающий удобное управление пользователями через фронтенд и бэкенд. Поддерживает поиск по ФИО или номеру телефона, сортировку, пагинацию и полные CRUD операции.

## Возможности

- **CRUD Операции**: Создание, Чтение, Обновление и Удаление пользователей.
- **Поиск**: Поиск пользователей по фамилии, имени, отчеству или номеру телефона.
- **Сортировка**: Сортировка списка пользователей по различным полям.
- **Пагинация**: Разбиение списка пользователей на страницы для удобства навигации.
- **Стандартизированное Хранение Номеров Телефонов**: Автоматическое форматирование и хранение номеров телефонов в формате `79136667788`.
- **Интуитивно Понятный Дизайн**: Приятный и удобный интерфейс для работы с данными.

## Технологии

### Бэкенд

- **Java 17**
- **Spring Boot**
- **Spring Data JPA**
- **PostgreSQL**

### Фронтенд

- **HTML5, CSS3, JavaScript**
- **Font Awesome**

## Начало Работы

### Требования

- **Java 17**
- **Maven**
- **PostgreSQL** база данных

### Установка

1. **Клонирование Репозитория**

    ```bash
    git clone https://github.com/Belaquaa/database-pr.git
    cd database-pr
    ```

2. **Настройка Базы Данных**

   Запустите файл 'docker-compose.yml' для запуска PostgreSQL в контейнере Docker:

    ```bash
    docker-compose up -d
    ```
   
3. **Сборка и Запуск Бэкенда**

   Выполните в корневой директории проекта:

    ```bash
    mvn clean install
    mvn spring-boot:run
    ```
   
4. **Запуск Приложения**

   Откройте [localhost:8080](http://localhost:8080/) в браузере.

## API Эндпоинты

### Создание Пользователя

- **URL**: `/api/users`
- **Метод**: `POST`

### Получение Пользователя по External ID

- **URL**: `/api/users/{externalId}`
- **Метод**: `GET`

### Обновление Пользователя

- **URL**: `/api/users/{externalId}`
- **Метод**: `PUT`

### Удаление Пользователя

- **URL**: `/api/users/{externalId}`
- **Метод**: `DELETE`

### Список Пользователей с Пагинацией

- **URL**: `/api/users`
- **Метод**: `GET`
- **Параметры Запроса**:
    - `page` (по умолчанию: 0)
    - `size` (по умолчанию: 10)
    - `sortField` (по умолчанию: externalId)
    - `sortDir` (по умолчанию: asc)
    - `search` (опционально)

## Инициализация Данных

При запуске приложения, если база данных пуста, автоматически создаются 35 примеров пользователей с уникальными номерами телефонов в формате `79136667788`.

## Вклад в Проект

Вклады приветствуются! Пожалуйста, откройте issue или отправьте pull request для предложений по улучшению или исправлению ошибок.

## Лицензия

Этот проект лицензирован под [MIT License](https://www.youtube.com/watch?v=dQw4w9WgXcQ).

## Контакты

Для вопросов или предложений, пожалуйста, свяжитесь с [eee_pokkk@mail.ru](mailto:eee_pokkk@mail.ru).
