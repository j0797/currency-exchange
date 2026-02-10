CREATE TABLE IF NOT EXISTS currencies
(
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    code      TEXT NOT NULL UNIQUE CHECK (LENGTH(code) = 3),
    full_name TEXT NOT NULL,
    sign      TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS exchange_rates
(
    id                 INTEGER PRIMARY KEY AUTOINCREMENT,
    base_currency_id   INTEGER        NOT NULL,
    target_currency_id INTEGER        NOT NULL,
    rate               NUMERIC(10, 6) NOT NULL CHECK (rate > 0),
    FOREIGN KEY (base_currency_id) REFERENCES currencies (id),
    FOREIGN KEY (target_currency_id) REFERENCES currencies (id),
    UNIQUE (base_currency_id, target_currency_id)
);

INSERT OR IGNORE INTO currencies (code, full_name, sign)
VALUES ('USD', 'US Dollar', '$'),
       ('EUR', 'Euro', '€'),
       ('GBP', 'British Pound', '£'),
       ('JPY', 'Japanese Yen', '¥'),
       ('RUB', 'Russian Ruble', '₽'),
       ('CNY', 'Chinese Yuan', '¥'),
       ('AUD', 'Australian Dollar', 'A$');