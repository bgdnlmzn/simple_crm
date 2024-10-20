# CRM - система

## Описание проекта

Этот проект представляет собой систему управления продавцами (CRM), реализованную с использованием Java Spring Boot и
PostgreSQL в качестве базы данных. Проект предоставляет REST API для управления продавцами, и их транзакциями.
Система также должна включать функции аналитики для обработки и анализа данных.

## Основной функционал

Управление продавцами:

- Создание, обновление и удаление продавцов;
- Получение списка активных и неактивных продавцов;
- Получение продавца по идентификатору.
  Аналитика:
- Получение наиболее продуктивных продавцов за указанный период (день, месяц, квартал, год);
- Получение продавцов с количеством транзакций ниже указанного значения за определенный временной диапазон;
- Определение лучших временных периодов для продавца, за которые он совершил наибольшее количество транзакций.

> [!NOTE]
> - В проекте реализован soft delete. Вместо физического удаления записей из базы данных, поле isActive устанавливается
    в значение false, что означает, что запись неактивна. Таким образом, данные сохраняются в основной таблице, но
    помечены как удалённые;
> - Для отслеживания изменений используется таблица истории. При каждом обновлении или удалении данных в основной
    таблице создаются соответствующие записи в таблице истории, где фиксируются все изменения (например, изменение
    данных продавца или удаление транзакций).
## Стек
- Java 17;
- Gradle 8.5;
- Lombok;
- PostgreSQL 12;
- Spring Boot 3.2.2;
- Flyway (Инструмент для миграции базы данных);
- Docker (Для контейнеризации).

### Основные зависимости:
- Spring Boot Starter Web — для создания REST API;
- Spring Boot Starter Data JPA — для работы с базой данных через JPA;
- Spring Boot Starter Validation — для валидации данных;
- PostgreSQL — в качестве базы данных;
- Flyway — для миграции базы данных;
- Docker Compose — используется для создания и управления несколькими контейнерами.
### Зависимости для тестирования:
- Spring Boot Starter Test — для тестирования;
- Testcontainers — для использования Docker контейнеров при тестировании базы данных;
- PostgreSQL Testcontainer — для работы с PostgreSQL в контейнере при тестах.

Для написания тестов используются: JUnit5, Mockito, AssertJ.
## Шаги для запуска

1. Клонируйте репозиторий:
    ```shell
   git clone https://github.com/bgdnlmzn/simple_crm.git
   cd simple_crm
   ```
2. Запустите Docker Compose для сборки и запуска сервисов:
   ```shell
   docker-compose up --build
   ```

3. Docker Compose создаст два контейнера:
    - postgres: контейнер для базы данных PostgreSQL.
    - webapi: контейнер для веб-приложения Spring Boot.

![Созданные контейнеры DockerDesktop](images\exampleDockerDesktop.png)

4. После успешного запуска, приложение будет доступно по адресу: http://localhost:8080.

5. Для остановки контейнеров выполните:
   ```shell
   docker-compose down
   ```
6. Для запуска тестов выполните:
   ```shell
   ./gradlew test
   ```

> [!NOTE]
> Перейдите на [официальный сайт Docker](https://www.docker.com/get-started) и следуйте инструкциям по установке.

## Примеры использования API

### SELLER

### POST http://localhost:8080/api/sellers

#### Описание:

Создание нового пользователя.

#### Request:

```json
{
  "sellerName": "Seller",
  "contactInfo": "seller@mail.com"
}
```

#### Responses:

*200(OK):*

Описание: Продавец успешно создан.

```json
{
  "id": 1,
  "sellerName": "Seller",
  "contactInfo": "seller@mail.com",
  "registrationDate": "2024-10-20T16:47:29.473533762",
  "updatedAt": "2024-10-20T16:47:29.473553511",
  "isActive": true
}
```

*400(Bad Request):*

```json
{
  "status": 400,
  "message": "Ошибка валидации",
  "timestamp": "2024-10-20T16:49:28.953816895",
  "fieldErrors": [
    {
      "field": "sellerName",
      "errorMessage": "Имя продавца не должно быть пустым"
    },
    {
      "field": "contactInfo",
      "errorMessage": "Контактная информация должна содержать от 5 до 50 символов"
    },
    {
      "field": "sellerName",
      "errorMessage": "Имя продавца должно содержать от 2 до 50 символов"
    },
    {
      "field": "contactInfo",
      "errorMessage": "Контактная информация не должна быть пустой"
    }
  ]
}
```

*409(Conflict):*

```json
{
  "status": 409,
  "message": "Продавец с таким email уже существует",
  "timestamp": "2024-10-20T16:56:28.799450217"
}
```

### GET http://localhost:8080/api/sellers

#### Описание:

Получение всех активных продавцов.

#### Responses:

*200(OK):*

```json
[
  {
    "id": 1,
    "sellerName": "Seller",
    "contactInfo": "seller@mail.com",
    "registrationDate": "2024-10-20T16:47:29.473534",
    "updatedAt": "2024-10-20T16:47:29.473554",
    "isActive": true
  }
]
```

*404(Not Found):*

```json
{
  "status": 404,
  "message": "Ни одной транзакции не найдено",
  "timestamp": "2024-10-20T17:41:11.394860877"
}
```

### GET http://localhost:8080/api/sellers/all

#### Описание:

Получение всех пользователей(активных и неактивных).

#### Responses:

*200(OK):*

```json
[
  {
    "id": 1,
    "sellerName": "Seller1",
    "contactInfo": "seller1@mail.com",
    "registrationDate": "2024-10-20T16:47:29.473534",
    "updatedAt": "2024-10-20T16:47:29.473554",
    "isActive": true
  },
  {
    "id": 2,
    "sellerName": "Seller2",
    "contactInfo": "seller2@mail.com",
    "registrationDate": "2024-10-20T16:47:29.473534",
    "updatedAt": "2024-10-20T16:47:29.473554",
    "isActive": false
  }
]
```

*404(Not Found):*

```json
{
  "status": 404,
  "message": "Ни одной транзакции не найдено",
  "timestamp": "2024-10-20T17:41:11.394860877"
}
```

### GET http://localhost:8080/api/sellers/{id}

#### Описание:

Получить пользователя по id.

#### Responses:

*200(OK):*

```json
{
  "id": 1,
  "sellerName": "Seller",
  "contactInfo": "seller@mail.com",
  "registrationDate": "2024-10-20T16:47:29.473534",
  "updatedAt": "2024-10-20T16:47:29.473554",
  "isActive": true
}
```

*400(Bad Request):*

```json
{
  "status": 400,
  "message": "Неверный формат параметра: id",
  "timestamp": "2024-10-20T17:55:33.626952457"
}
```

*404(Not Found):*

```json
{
  "status": 404,
  "message": "Продавец с id: 1 не найден",
  "timestamp": "2024-10-20T17:10:03.221505223"
}
```

*404(Not Found):*

```json
{
  "status": 404,
  "message": "Ни одной транзакции не найдено",
  "timestamp": "2024-10-20T17:41:11.394860877"
}
```

### PATCH http://localhost:8080/api/sellers/{id}

#### Описание:

Обновление данных продавца

#### Responses:

*200(OK):*

Описание: Продавец успешно обновлен.

```json
{
  "sellerName": "New Seller",
  "contactInfo": "newseller@mail.com"
}
```

*400(Bad Request):*

```json
{
  "status": 400,
  "message": "Неверный формат параметра: id",
  "timestamp": "2024-10-20T17:55:33.626952457"
}
```

```json
{
  "status": 400,
  "message": "Ошибка валидации",
  "timestamp": "2024-10-20T17:15:28.400605886",
  "fieldErrors": [
    {
      "field": "contactInfo",
      "errorMessage": "Контактная информация должна содержать от 5 до 50 символов"
    },
    {
      "field": "sellerName",
      "errorMessage": "Имя продавца должно содержать от 2 до 50 символов"
    }
  ]
}
```

*404(Not Found):*

```json
{
  "status": 404,
  "message": "Продавец с id: 1 не найден",
  "timestamp": "2024-10-20T17:18:31.390154872"
}
```

*409(Conflict):*

```json
{
  "status": 409,
  "message": "Продавец с таким email уже существует",
  "timestamp": "2024-10-20T17:17:02.22132765"
}
```

### DELETE http://localhost:8080/api/sellers/{id}

#### Описание:

Удаление продавца по его ID.

#### Responses:

*204(No Content)*

Описание: Продавец удален.

*400(Bad Request):*

```json
{
  "status": 400,
  "message": "Неверный формат параметра: id",
  "timestamp": "2024-10-20T17:55:33.626952457"
}
```

*404(Not Found):*

```json
{
  "status": 404,
  "message": "Продавец с id: 1 не найден",
  "timestamp": "2024-10-20T17:23:01.498083967"
}
```

### TRANSACTION

### POST http://localhost:8080/api/transactions

#### Описание:

Добавить транзакцию продавцу.

#### Request:

```json
{
  "sellerId": 1,
  "amount": 100,
  "paymentType": "CARD"
}
```

#### Responses:

*200(OK):*

Описание: Транзакция успешно создана.

```json
{
  "id": 1,
  "sellerId": 1,
  "amount": 100,
  "paymentType": "CARD",
  "transactionDate": "2024-10-20T17:29:26.566673141",
  "updatedAt": "2024-10-20T17:29:26.566695254",
  "isActive": true
}
```

*400(Bad Request):*

```json
{
  "status": 400,
  "message": "Неверный тип оплаты. Доступные типы оплаты: [CASH, CARD, TRANSFER]",
  "timestamp": "2024-10-20T17:30:22.836855588"
}
```

```json
{
  "status": 400,
  "message": "Ошибка валидации",
  "timestamp": "2024-10-20T17:31:33.892095025",
  "fieldErrors": [
    {
      "field": "paymentType",
      "errorMessage": "Тип оплаты не должен быть пустым"
    },
    {
      "field": "amount",
      "errorMessage": "Сумма не должна быть пустой."
    },
    {
      "field": "sellerId",
      "errorMessage": "ID продавца не должен быть пустым"
    }
  ]
}
```

*404(Not Found):*

```json
{
  "status": 404,
  "message": "Продавец с id: 1 не найден",
  "timestamp": "2024-10-20T17:32:43.505568113"
}
```

### GET http://localhost:8080/api/transactions

#### Описание:

Получить все активные транзакции

#### Responses:

*200(OK):*

```json
[
  {
    "id": 1,
    "sellerId": 1,
    "amount": 100.00,
    "paymentType": "CARD",
    "transactionDate": "2024-10-20T17:29:26.566673",
    "updatedAt": "2024-10-20T17:29:26.566695",
    "isActive": true
  }
]
```

*404(Not Found):*

```json
{
  "status": 404,
  "message": "Ни одной транзакции не найдено",
  "timestamp": "2024-10-20T17:41:11.394860877"
}
```

### GET http://localhost:8080/api/transactions/all

#### Описание:

Получить все транзакции(активные/неактивные)

#### Responses:

*200(OK):*

```json
[
  {
    "id": 1,
    "sellerId": 1,
    "amount": 100.00,
    "paymentType": "CARD",
    "transactionDate": "2024-10-20T17:29:26.566673",
    "updatedAt": "2024-10-20T17:29:26.566695",
    "isActive": true
  },
  {
    "id": 2,
    "sellerId": 1,
    "amount": 100.00,
    "paymentType": "CARD",
    "transactionDate": "2024-10-20T17:29:26.566673",
    "updatedAt": "2024-10-20T17:29:26.566695",
    "isActive": false
  }
]
```

*404(Not Found):*

```json
{
  "status": 404,
  "message": "Ни одной транзакции не найдено",
  "timestamp": "2024-10-20T17:41:11.394860877"
}
```

### GET http://localhost:8080/api/transactions/seller/{sellerId}

#### Описание:

Получить все активные транзакции продавца по его ID.

#### Responses:

*200(OK):*

```json
[
  {
    "id": 1,
    "sellerId": 1,
    "amount": 100.00,
    "paymentType": "CARD",
    "transactionDate": "2024-10-20T17:29:26.566673",
    "updatedAt": "2024-10-20T17:29:26.566695",
    "isActive": true
  }
]
```

*400(Bad Request):*

```json
{
  "status": 400,
  "message": "Неверный формат параметра: sellerId",
  "timestamp": "2024-10-20T17:55:33.626952457"
}
```

*404(Not Found):*

```json
{
  "status": 404,
  "message": "Ни одной транзакции не найдено",
  "timestamp": "2024-10-20T17:41:11.394860877"
}
```

```json
{
  "status": 404,
  "message": "Продавец с id: 1 не найден",
  "timestamp": "2024-10-20T17:40:06.394857917"
}
```

### GET http://localhost:8080/api/transactions/{transactionId}

#### Описание:

Получение транзакции по ее ID.

#### Responses:

*200(OK):*

```json
{
  "id": 1,
  "sellerId": 1,
  "amount": 100.00,
  "paymentType": "CARD",
  "transactionDate": "2024-10-20T17:29:26.566673",
  "updatedAt": "2024-10-20T17:29:26.566695",
  "isActive": true
}
```

*400(Bad Request):*

```json
{
  "status": 400,
  "message": "Неверный формат параметра: transactionId",
  "timestamp": "2024-10-20T17:55:33.626952457"
}
```

*404(Not Found):*

```json
{
  "status": 404,
  "message": "Транзакция с id: 1 не найдена",
  "timestamp": "2024-10-20T17:45:28.022803448"
}
```

### PATCH http://localhost:8080/api/transactions/{transactionId}

#### Описание:

Обновление транзакции по ее ID.

#### Request:

```json
{
  "amount": 1000,
  "paymentType": "CASH"
}
```

#### Responses:

*200(OK):*

Описание: Транзакция успешно обновлена.

```json
{
  "id": 1,
  "sellerId": 1,
  "amount": 1000,
  "paymentType": "CASH",
  "transactionDate": "2024-10-20T17:29:26.566673",
  "updatedAt": "2024-10-20T17:48:53.522741292",
  "isActive": true
}
```

*400(Bad Request):*

```json
{
  "status": 400,
  "message": "Неверный формат параметра: transactionId",
  "timestamp": "2024-10-20T17:55:33.626952457"
}
```

```json
{
  "status": 400,
  "message": "Неверный тип оплаты. Доступные типы оплаты: [CASH, CARD, TRANSFER]",
  "timestamp": "2024-10-20T17:50:02.212600878"
}
```

```json
{
  "status": 400,
  "message": "Ошибка валидации",
  "timestamp": "2024-10-20T17:49:12.606325415",
  "fieldErrors": [
    {
      "field": "amount",
      "errorMessage": "Сумма должна быть положительной"
    }
  ]
}
```

*404(Not Found):*

```json
{
  "status": 404,
  "message": "Транзакция с id: 1 не найдена",
  "timestamp": "2024-10-20T17:49:02.929478731"
}
```

### DELETE http://localhost:8080/api/transactions/{transactionId}

#### Описание:

Удаление транзакции по ее ID.

#### Responses:

*204(No Content):*

Описание: Транзакция успешно удалена.

*400(Bad Request):*

```json
{
  "status": 400,
  "message": "Неверный формат параметра: transactionId",
  "timestamp": "2024-10-20T17:55:33.626952457"
}
```

*404(Not Found):*

```json
{
  "status": 404,
  "message": "Транзакция с id: 1 не найдена",
  "timestamp": "2024-10-20T17:53:27.915927218"
}
```

### ANALYTICS

### GET http://localhost:8080/api/analytics/best-periods/seller/{id}

#### Описание:

Получить наилучшие периоды времени для продавца по его ID, в
течение которых он совершил наибольшее количество транзакций.

#### Пример Request:

GET http://localhost:8080/api/analytics/best-periods/seller/1

#### Responses:

*200(OK):*

```json
{
  "bestDayPeriod": {
    "startDate": "2024-10-20T00:00:00",
    "endDate": "2024-10-20T23:59:59",
    "periodType": "DAY",
    "transactionCount": 1
  },
  "bestWeekPeriod": {
    "startDate": "2024-10-14T00:00:00",
    "endDate": "2024-10-20T23:59:59",
    "periodType": "WEEK",
    "transactionCount": 1
  },
  "bestMonthPeriod": {
    "startDate": "2024-10-01T00:00:00",
    "endDate": "2024-10-31T23:59:59",
    "periodType": "MONTH",
    "transactionCount": 1
  }
}
```

*400(Bad Request):*

```json
{
  "status": 400,
  "message": "Неверный формат параметра: id",
  "timestamp": "2024-10-20T18:00:10.677893849"
}
```

*404(Not Found):*

```json
{
  "status": 404,
  "message": "У продавца нет транзакций",
  "timestamp": "2024-10-20T18:00:22.281134465"
}
```

### GET http://localhost:8080/api/analytics/most-productive

#### Описание:

Получить самого продуктивного продавца в рамках дня, месяцы, квартала, года (самый
продуктивный, тот у которого сумма всех транзакции больше всех других продавцов). Если таких продавцов несколько, то
выведется каждый из них.

#### Request params:

- date - дата от которой считать период
- period - период из {DAY, MONTH, YEAR}
- active - выборка из активных или неактивных пользователей

#### Пример Request:

GET http://localhost:8080/api/analytics/most-productive?date=2024-10-20&period=DAY&active=true

#### Responses:

*200(OK):*

```json
[
  {
    "id": 1,
    "sellerName": "Seller",
    "contactInfo": "sellers@mail.com",
    "amount": 100.00,
    "registrationDate": "2024-10-20T17:29:01.659646"
  }
]
```

*400(Bad Request):*

```json
{
  "status": 400,
  "message": "Некорректный временной промежуток.",
  "timestamp": "2024-10-20T18:12:18.84443093"
}
```

```json
{
  "status": 400,
  "message": "Неверный формат параметра: {requestParam}",
  "timestamp": "2024-10-20T18:12:38.119793972"
}
```

*404(Not Found):*

```json
{
  "status": 404,
  "message": "Ни одного продавца не найдено",
  "timestamp": "2024-10-20T18:30:06.088826882"
}
```

### GET http://localhost:8080/api/analytics/less-then

#### Описание:

Получение списка продавцов, у которых сумма всех транзакции за выбранный период
меньше переданного параметра суммы.

#### Request params:

- amount - параметр суммы транзакций;
- start - начальная дата, ISO 8601;
- end - конечная дата, ISO 8601;
- active - выборка из активных или неактивных продавцов.

#### Пример Request:

GET http://localhost:8080/api/analytics/less-then?amount=101&start=2024-10-19&end=2024-10-21&active=true

#### Responses:

*400(Bad Request):*

````json
{
  "status": 400,
  "message": "Неверный формат параметра: {requestParam}",
  "timestamp": "2024-10-20T18:18:36.297080562"
}
````

```json
{
  "status": 400,
  "message": "Введенная начальная дата не должна быть позже конечной даты",
  "timestamp": "2024-10-20T18:19:07.278383741"
}
```

*404(Not Found):*

```json
{
  "status": 404,
  "message": "По данным параметрам ни одного пользователя не найдено",
  "timestamp": "2024-10-20T18:18:21.203966614"
}
```