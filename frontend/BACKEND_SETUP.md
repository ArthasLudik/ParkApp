# 🔧 Настройка Backend

## Проблема: "Failed to fetch parking sessions"

Эта ошибка возникает, когда frontend не может подключиться к backend API.

## Решение:

### 1. Запустите Spring Boot Backend

```bash
# Перейдите в корневую папку проекта
cd /Users/arthas/IdeaProjects/Park

# Запустите Spring Boot приложение
./mvnw spring-boot:run
```

### 2. Проверьте, что backend запущен

Backend должен быть доступен по адресу: http://localhost:8080

Вы можете проверить это, открыв в браузере:
- http://localhost:8080/api/spots/all
- http://localhost:8080/api/session

### 3. Проверьте консоль backend

В терминале, где запущен backend, вы должны увидеть:
```
Started ParkApplication in X.XXX seconds
```

### 4. Убедитесь, что база данных настроена

Проверьте файл `src/main/resources/application.yaml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/parking_db
    username: your_username
    password: your_password
```

### 5. Если PostgreSQL не установлен

Установите PostgreSQL или измените настройки на H2 (встроенная база данных):

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  h2:
    console:
      enabled: true
```

## Проверка работы

1. **Backend запущен**: http://localhost:8080
2. **Frontend запущен**: http://localhost:5173 (или 5174, 5175)
3. **В frontend** должен появиться зеленый индикатор "Backend подключен"

## Возможные проблемы

### Порт 8080 занят
```bash
# Найдите процесс, использующий порт 8080
lsof -i :8080

# Остановите процесс
kill -9 <PID>
```

### Ошибки базы данных
```bash
# Проверьте логи backend
./mvnw spring-boot:run
```

### CORS ошибки
Добавьте в backend конфигурацию CORS (если нужно):
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173", "http://localhost:5174", "http://localhost:5175")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH");
    }
}
``` 