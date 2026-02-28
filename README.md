# Currency Exchange Service

Веб-приложение для конвертации валют и управления обменными курсами.

## 🚀 Технологии

- **Backend:** Java 21, Jakarta Servlets, Gson, Lombok, SQLite
- **Frontend:** HTML, CSS (Bootstrap), JavaScript (jQuery)
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

## 🔧 Запуск локально

1. Клонировать репозиторий: git clone https://github.com/j0797/currency-exchange.git
2. Перейти в папку проекта и собрать WAR: mvn clean package
3. Развернуть полученный target/currency-exchange-1.0-SNAPSHOT.war в Tomcat
4. Запустить Tomcat и открыть в браузере: http://localhost:8080/currency-exchange-1.0-SNAPSHOT/

## 🌐 Демо
Проект доступен по адресу: http://85.198.68.173:8080/currency-exchange-1.0-SNAPSHOT/

## 📄 Лицензия

MIT

