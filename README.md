# CRM система с JWT аутентификацией

Система управления продавцами (CRM) с JWT токенами и LDAP аутентификацией.

## 📦 Архитектура

Проект состоит из двух модулей:

- **crm-auth-ldap** - JWT аутентификация + LDAP интеграция
- **crm-core** - Бизнес-логика (продавцы, транзакции, аналитика)

## 🚀 Быстрый старт

### Вариант 1: Docker (рекомендуется)

```bash
# Запустить всё (база данных + приложение + LDAP)
docker-compose up --build

# Приложение будет доступно на http://localhost:8080
```

### Вариант 2: Локальный запуск

```bash
# 1. Запустить только базу данных
docker-compose up database -d

# 2. Запустить приложение (dev режим, без LDAP)
./gradlew :crm-core:bootRun --args='--spring.profiles.active=dev'
```

## 🔑 Получение JWT токена

### Dev режим (без LDAP)

```bash
# Получить токен администратора
curl -X POST "http://localhost:8080/api/dev/token/admin?username=admin"

# Ответ:
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "username": "admin",
  "authorities": ["ROLE_ADMIN", "READ_SELLERS", "WRITE_SELLERS"]
}
```

### LDAP режим

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

## 📝 Использование API

### С curl

```bash
# Сохранить токен
TOKEN="ваш-токен-здесь"

# Получить всех продавцов
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/sellers

# Создать продавца
curl -X POST http://localhost:8080/api/sellers \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "sellerName": "Test Seller",
    "contactInfo": "test@example.com"
  }'
```

### Swagger UI

1. Откройте http://localhost:8080/swagger-ui.html
2. Кликните **Authorize** 🔒
3. Введите: `Bearer YOUR_TOKEN`
4. Используйте API через интерфейс

## 📋 API Endpoints

### Аутентификация (публичные)

- `POST /api/auth/login` - войти через LDAP
- `POST /api/dev/token/admin` - получить токен админа (dev)
- `POST /api/dev/token/user` - получить токен юзера (dev)

### Продавцы (требуют авторизации)

- `GET /api/sellers` - все активные продавцы
- `GET /api/sellers/{id}` - продавец по ID
- `POST /api/sellers` - создать (нужен `ROLE_ADMIN` или `WRITE_SELLERS`)
- `PATCH /api/sellers/{id}` - обновить (нужен `ROLE_ADMIN` или `WRITE_SELLERS`)
- `DELETE /api/sellers/{id}` - удалить (только `ROLE_ADMIN`)

### Транзакции (требуют авторизации)

- `GET /api/transactions` - все активные транзакции
- `GET /api/transactions/{id}` - транзакция по ID
- `POST /api/transactions` - создать
- `PATCH /api/transactions/{id}` - обновить
- `DELETE /api/transactions/{id}` - удалить

### Аналитика (требуют авторизации)

- `GET /api/analytics/most-productive` - самый продуктивный продавец
- `GET /api/analytics/less-then` - продавцы с малым количеством транзакций
- `GET /api/analytics/best-periods/seller/{id}` - лучшие периоды

## 🔐 Права доступа

| Операция | Требуемые права |
|----------|-----------------|
| Чтение данных | Любой авторизованный пользователь |
| Создание/обновление | `ROLE_ADMIN` или `WRITE_SELLERS` |
| Удаление | Только `ROLE_ADMIN` |

### Примеры authorities

- `ROLE_ADMIN` - полный доступ
- `ROLE_USER` - базовый доступ
- `READ_SELLERS` - чтение продавцов
- `WRITE_SELLERS` - создание/обновление продавцов
- `READ_TRANSACTIONS` - чтение транзакций
- `WRITE_TRANSACTIONS` - создание/обновление транзакций

## ⚙️ Конфигурация

### Environment variables

```bash
# База данных
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/crm
SPRING_DATASOURCE_USERNAME=crm
SPRING_DATASOURCE_PASSWORD=crm

# JWT
JWT_SECRET=your-secret-key-256-bits-minimum
JWT_EXPIRATION=86400000  # 24 часа

# LDAP (опционально)
LDAP_URL=ldap://localhost:389
LDAP_BASE_DN=dc=example,dc=com

# Профиль
SPRING_PROFILES_ACTIVE=dev  # dev или prod
```

### application.yaml

```yaml
jwt:
  secret: ${JWT_SECRET}
  expiration: ${JWT_EXPIRATION:86400000}

ldap:
  url: ${LDAP_URL:ldap://localhost:389}
  base-dn: ${LDAP_BASE_DN:dc=example,dc=com}
  user-dn-pattern: uid={0},ou=people
  user-search-base: ou=people
  user-search-filter: (uid={0})
  group-search-base: ou=groups
```

## 🛠️ Разработка

### Сборка

```bash
# Собрать всё
./gradlew build

# Без тестов
./gradlew build -x test

# Только auth модуль
./gradlew :crm-auth-ldap:build

# Только core модуль
./gradlew :crm-core:build
```

### Тестирование

```bash
# Все тесты
./gradlew test

# Только auth
./gradlew :crm-auth-ldap:test

# Только core
./gradlew :crm-core:test
```

### Локальный запуск

```bash
# Dev режим (без LDAP)
./gradlew :crm-core:bootRun --args='--spring.profiles.active=dev'

# Production режим (с LDAP)
./gradlew :crm-core:bootRun
```

## 🐳 Docker команды

```bash
# Запустить всё
docker-compose up --build

# Запустить в фоне
docker-compose up -d

# Остановить
docker-compose down

# Пересобрать и запустить
docker-compose up --build --force-recreate

# Посмотреть логи
docker-compose logs -f crm-app

# Только база данных
docker-compose up database -d
```

## 📊 Структура проекта

```
simple_crm/
├── crm-auth-ldap/              # Auth модуль
│   ├── src/main/java/ru/cft/crm/auth/
│   │   ├── security/jwt/       # JWT токены
│   │   ├── ldap/              # LDAP интеграция
│   │   └── config/            # Security конфигурация
│   └── build.gradle
│
├── crm-core/                   # Core модуль
│   ├── src/main/java/ru/cft/crm/
│   │   ├── controller/        # REST контроллеры
│   │   ├── service/           # Бизнес-логика
│   │   ├── repository/        # JPA репозитории
│   │   ├── entity/            # Сущности БД
│   │   └── Application.java   # Main класс
│   ├── src/main/resources/
│   │   ├── application.yaml
│   │   └── db.migration/      # Flyway миграции
│   └── build.gradle
│
├── docker-compose.yml          # Docker конфигурация
├── Dockerfile                  # Образ приложения
└── README.md                   # Эта документация
```

## 🔍 Примеры использования

### Полный пример: создание продавца

```bash
# 1. Запустить приложение
docker-compose up -d

# 2. Получить токен
TOKEN=$(curl -s -X POST "http://localhost:8080/api/dev/token/admin?username=admin" | jq -r '.accessToken')

# 3. Создать продавца
curl -X POST http://localhost:8080/api/sellers \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "sellerName": "John Doe",
    "contactInfo": "john@example.com"
  }' | jq

# 4. Получить всех продавцов
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/sellers | jq
```

### Создание транзакции

```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "sellerId": 1,
    "amount": 1000.50,
    "paymentType": "CARD"
  }' | jq
```

### Получение аналитики

```bash
# Самый продуктивный продавец за день
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/analytics/most-productive?date=2024-12-27&period=DAY&active=true" | jq
```

## 🧪 Тестирование JWT

### Декодирование токена

```bash
# Linux/Mac
echo "$TOKEN" | cut -d. -f2 | base64 -d | jq

# Вы увидите:
{
  "sub": "admin",
  "authorities": ["ROLE_ADMIN", "READ_SELLERS", "WRITE_SELLERS"],
  "scope": ["ROLE_ADMIN", "READ_SELLERS", "WRITE_SELLERS"],
  "iat": 1703678400,
  "exp": 1703764800
}
```

### Проверка прав доступа

```bash
# С правами админа - работает
TOKEN_ADMIN=$(curl -s -X POST "http://localhost:8080/api/dev/token/admin?username=admin" | jq -r '.accessToken')
curl -X DELETE -H "Authorization: Bearer $TOKEN_ADMIN" http://localhost:8080/api/sellers/1

# Без прав - 403 Forbidden
TOKEN_USER=$(curl -s -X POST "http://localhost:8080/api/dev/token/user?username=user" | jq -r '.accessToken')
curl -X DELETE -H "Authorization: Bearer $TOKEN_USER" http://localhost:8080/api/sellers/1
```

## 🛡️ Безопасность

### В production

1. **Измените JWT secret** на случайную строку минимум 256 бит
2. **Используйте HTTPS**
3. **Настройте LDAPS** вместо LDAP
4. **Не используйте dev профиль**
5. **Настройте переменные окружения** вместо хардкода

### Пример production конфигурации

```bash
# docker-compose.prod.yml
services:
  crm-app:
    environment:
      SPRING_PROFILES_ACTIVE: prod
      JWT_SECRET: ${JWT_SECRET}  # Из переменной окружения
      LDAP_URL: ldaps://ldap.company.com:636
      # ...
```

## 📚 Технологии

- **Java 17**
- **Spring Boot 3.2.2**
  - Spring Security
  - Spring Data JPA
  - Spring LDAP
- **JWT** (JJWT 0.12.3)
- **PostgreSQL 12**
- **Flyway** (миграции БД)
- **Gradle 8.5**
- **Docker & Docker Compose**
- **Swagger/OpenAPI**

## 🐛 Troubleshooting

### "JAVA_HOME is not set"

```bash
# Windows
set JAVA_HOME=C:\Program Files\Java\jdk-17

# Linux/Mac
export JAVA_HOME=/path/to/java-17
```

### "Connection refused" к базе данных

```bash
# Проверить что база запущена
docker-compose ps

# Перезапустить
docker-compose restart database

# Посмотреть логи
docker-compose logs database
```

### "401 Unauthorized"

- Проверьте что токен в заголовке: `Authorization: Bearer <token>`
- Проверьте что токен не истек (по умолчанию 24 часа)
- Получите новый токен

### "403 Forbidden"

- У пользователя недостаточно прав
- Декодируйте токен и проверьте authorities
- Используйте токен с нужными правами (например admin)

## 📞 Полезные ссылки

- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/api-docs
- Health Check: http://localhost:8080/actuator/health

## 📄 Лицензия

Учебный проект CFT.

---

**Готово к использованию!** 🚀

Запустите: `docker-compose up --build`
