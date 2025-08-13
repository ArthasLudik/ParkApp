# 🔧 Решение проблем

## Проблема с импортом PrimeVue CSS

### Ошибка:
```
Failed to resolve import "primevue/resources/themes/lara-light-blue/theme.css"
```

### Решение:
PrimeVue CSS файлы подключены через CDN в `index.html`:

```html
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/primevue@3/resources/themes/lara-light-blue/theme.css">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/primevue@3/resources/primevue.css">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/primeicons@6/primeicons.css">
```

### Альтернативное решение:
Если CDN недоступен, можно использовать локальные файлы:

1. Установить дополнительные пакеты:
```bash
npm install @primevue/resources
```

2. Импортировать в main.ts:
```typescript
import '@primevue/resources/themes/lara-light-blue/theme.css'
import '@primevue/resources/primevue.css'
```

## Другие возможные проблемы

### 1. Порт занят
Если порт 5173 занят, Vite автоматически выберет следующий доступный порт (5174, 5175, etc.)

### 2. Backend не запущен
Убедитесь, что Spring Boot backend запущен на порту 8080:
```bash
./mvnw spring-boot:run
```

### 3. TypeScript ошибки
Запустите проверку типов:
```bash
npm run type-check
```

### 4. Проблемы с зависимостями
Переустановите зависимости:
```bash
rm -rf node_modules package-lock.json
npm install
``` 