# 🚀 Быстрый старт

## 1. Запуск одной командой

```bash
docker-compose up --build
```

Готово! Приложение доступно на http://localhost:8080

## 2. Получить токен

```bash
curl -X POST "http://localhost:8080/api/dev/token/admin?username=admin"
```

## 3. Использовать API

```bash
TOKEN="ваш-токен"
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/sellers
```

## Swagger UI

http://localhost:8080/swagger-ui.html

---

Полная документация в [README.md](README.md)

