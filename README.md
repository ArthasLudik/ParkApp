# Parking System

Полнофункциональная система управления парковкой с Spring Boot backend и Vue.js frontend.

## 🏗️ Архитектура

- **Backend**: Spring Boot + Java + PostgreSQL
- **Frontend**: Vue.js 3 + TypeScript + PrimeVue + Pinia
- **База данных**: PostgreSQL

## 🚀 Быстрый старт

### 1. Запуск Backend

```bash
# Перейти в корневую папку проекта
cd /Users/arthas/IdeaProjects/Park

# Запустить Spring Boot приложение
./mvnw spring-boot:run
```

Backend будет доступен по адресу: http://localhost:8080

### 2. Запуск Frontend

```bash
# В новом терминале перейти в папку frontend
cd frontend

# Установить зависимости (если еще не установлены)
npm install

# Запустить в режиме разработки
npm run dev
```

Frontend будет доступен по адресу: http://localhost:5173

## 📁 Структура проекта

```
Park/
├── src/                    # Spring Boot backend
│   ├── main/java/
│   │   └── org/example/park/
│   │       ├── controller/ # REST API контроллеры
│   │       ├── service/    # Бизнес-логика
│   │       ├── repository/ # Доступ к данным
│   │       └── model/      # DTO и сущности
│   └── resources/
│       └── db/migration/   # Миграции базы данных
├── frontend/               # Vue.js frontend
│   ├── src/
│   │   ├── views/          # Страницы приложения
│   │   ├── stores/         # Pinia stores
│   │   ├── services/       # API сервисы
│   │   └── types/          # TypeScript типы
│   └── package.json
└── README.md
```

## 🔧 API Endpoints

### Парковочные места
- `GET /api/spots/all` - получить все места
- `GET /api/spots/free` - получить свободные места
- `POST /api/spots` - создать новое место
- `PUT /api/spots` - обновить место
- `DELETE /api/spots/{id}` - удалить место

### Сессии
- `GET /api/session` - получить все сессии
- `POST /api/session` - создать новую сессию
- `PUT /api/session` - завершить сессию
- `DELETE /api/session/{id}` - удалить сессию

### Пользователи
- `POST /api/user` - создать пользователя
- `GET /api/user/{id}` - получить пользователя
- `PATCH /api/user/{id}` - обновить роль
- `DELETE /api/user/{id}` - удалить пользователя

### Транспортные средства
- `POST /api/vehicles` - создать транспортное средство
- `DELETE /api/vehicles/{id}` - удалить транспортное средство

## 🎯 Функциональность

### Backend
- RESTful API
- Валидация данных
- Обработка ошибок
- Миграции базы данных
- Логирование

### Frontend
- Современный UI с PrimeVue
- TypeScript для типизации
- Управление состоянием с Pinia
- Маршрутизация с Vue Router
- Адаптивный дизайн
- Toast уведомления
- Подтверждения действий

## 🛠️ Разработка

### Backend
```bash
# Запуск тестов
./mvnw test

# Сборка JAR
./mvnw clean package
```

### Frontend
```bash
# Проверка типов
npm run type-check

# Сборка для продакшена
npm run build

# Предварительный просмотр
npm run preview
```

## 📊 База данных

Система использует PostgreSQL. Схема создается автоматически при первом запуске через Flyway миграции.

## 🔐 Безопасность

- Валидация входных данных
- Обработка ошибок
- Логирование операций

## 📝 Лицензия

MIT 