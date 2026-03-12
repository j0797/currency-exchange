# Currency Exchange Service

Веб-приложение для конвертации валют и управления обменными курсами.

## 🚀 Технологии

- **Backend:** Java 21, Jakarta Servlets, Gson, Lombok, HikariCP, MapStruct
- **Frontend:** HTML, CSS (Bootstrap), JavaScript (jQuery)
- **База данных:** SQLite
- **Сборка:** Maven
- **Сервер:** Apache Tomcat 11
- **Деплой:** VPS (Ubuntu 24.04)

## 📦 Функциональность

- Получение списка всех валют (`GET /currencies`)
- Добавление новой валюты (`POST /currencies`)
- Получение конкретной валюты по коду (`GET /currency/USD`)
- Получение списка всех обменных курсов (`GET /exchangeRates`)
- Добавление нового курса (`POST /exchangeRates`)
- Обновление курса (`PATCH /exchangeRate/USDEUR`)
- Конвертация валют (`GET /exchange?from=USD&to=EUR&amount=100`)

## ⚙️ Настройка базы данных

При первом запуске база данных создаётся автоматически, таблицы инициализируются скриптом init.sql. Конфигурация подключения вынесена в файл src/main/resources/db.properties:

```db.driver=org.sqlite.JDBC
db.url=jdbc:sqlite:/opt/tomcat11/data/database.db   # путь к файлу БД на сервере

# Параметры пула HikariCP:
pool.maxSize=10
pool.minIdle=2
pool.connectionTimeout=30000
pool.idleTimeout=600000
pool.maxLifetime=1800000
```

## 🔧 Запуск локально

1. Клонировать репозиторий: git clone https://github.com/j0797/currency-exchange.git
2. Перейти в папку проекта и собрать WAR: mvn clean package
3. Развернуть полученный target/currency-exchange-1.0-SNAPSHOT.war в Tomcat
4. Запустить Tomcat и открыть в браузере: http://localhost:8080/currency-exchange-1.0-SNAPSHOT/

## 🌐 Демо
Проект будет доступен до 13.03.2026 по адресу: http://85.198.68.173:8080/currency-exchange-1.0-SNAPSHOT/

## 📄 Лицензия

MIT

