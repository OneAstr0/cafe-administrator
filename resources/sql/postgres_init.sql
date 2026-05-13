DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS dishes CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS tables CASCADE;
DROP TABLE IF EXISTS waiters CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- =====================================================
-- PostgreSQL INIT
-- =====================================================

-- 1. Пользователи
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    login VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('admin', 'waiter'))
);

-- 2. Официанты
CREATE TABLE waiters (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    shift VARCHAR(20) CHECK (shift IN ('morning', 'evening', 'full')),
    user_id INTEGER REFERENCES users(id) ON DELETE SET NULL
);

-- 3. Столики
CREATE TABLE tables (
    id SERIAL PRIMARY KEY,
    table_number INTEGER NOT NULL UNIQUE,
    seats INTEGER NOT NULL CHECK (seats > 0),
    status VARCHAR(20) DEFAULT 'free'
        CHECK (status IN ('free', 'occupied'))
);

-- 4. Категории блюд
CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    parent_id INTEGER REFERENCES categories(id) ON DELETE SET NULL
);

-- 5. Блюда
CREATE TABLE dishes (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category_id INTEGER REFERENCES categories(id),
    price NUMERIC(10,2) NOT NULL CHECK (price > 0),
    weight_volume VARCHAR(50)
);

-- 6. Заказы
CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    table_id INTEGER REFERENCES tables(id),
    waiter_id INTEGER REFERENCES waiters(id),
    opened_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    closed_at TIMESTAMP,
    total_sum NUMERIC(10,2) DEFAULT 0,
    status VARCHAR(20) DEFAULT 'open'
        CHECK (status IN ('open', 'closed', 'paid'))
);

-- 7. Состав заказа
CREATE TABLE order_items (
    order_id INTEGER REFERENCES orders(id) ON DELETE CASCADE,
    dish_id INTEGER REFERENCES dishes(id),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    price_at_order NUMERIC(10,2) NOT NULL CHECK (price_at_order > 0),
    PRIMARY KEY (order_id, dish_id)
);

-- =====================================================
-- ТЕСТОВЫЕ ДАННЫЕ
-- =====================================================

-- Пользователи
INSERT INTO users (login, password, role) VALUES
('admin1', 'admin123', 'admin'),
('evgeniy', 'pass123', 'waiter'),
('maria', 'pass456', 'waiter'),
('alexandr', 'pass789', 'waiter'),
('ramzes', 'pass111', 'waiter'),
('veronika', 'pass222', 'waiter');

-- Официанты
INSERT INTO waiters (full_name, phone, shift, user_id) VALUES
('Евгений Федосов', '+7-999-111-2233', 'morning', 2),
('Мария Горина', '+7-999-222-3344', 'evening', 3),
('Александр Лебедев', '+7-999-333-4455', 'full', 4),
('Джулдиев Рамзес', '+7-999-444-5566', 'morning', 5),
('Свистунова Вероника', '+7-999-555-6677', 'evening', 6);

-- Столики
INSERT INTO tables (table_number, seats, status) VALUES
(1, 2, 'free'),
(2, 2, 'occupied'),
(3, 4, 'free'),
(4, 4, 'free'),
(5, 6, 'free'),
(6, 6, 'occupied'),
(7, 8, 'free'),
(8, 2, 'free');

-- Категории
INSERT INTO categories (name, parent_id) VALUES
('Супы', NULL),
('Салаты', NULL),
('Горячие блюда', NULL),
('Напитки', NULL),
('Десерты', NULL),
('Паста', 3),
('Мясные блюда', 3),
('Алкогольные', 4),
('Безалкогольные', 4);

-- Блюда
INSERT INTO dishes (name, category_id, price, weight_volume) VALUES
('Суп Том Ям', 1, 350.00, '350 мл'),
('Борщ со сметаной', 1, 280.00, '300 мл'),
('Цезарь с курицей', 2, 420.00, '250 г'),
('Греческий салат', 2, 360.00, '220 г'),
('Карбонара', 6, 450.00, '320 г'),
('Болоньезе', 6, 420.00, '300 г'),
('Стейк из говядины', 7, 890.00, '250 г'),
('Эспрессо', 9, 120.00, '50 мл'),
('Капучино', 9, 180.00, '200 мл'),
('Чай чёрный', 9, 150.00, '300 мл'),
('Чизкейк', 5, 320.00, '180 г'),
('Тирамису', 5, 350.00, '160 г'),
('Сок апельсиновый', 9, 200.00, '250 мл'),
('Пиво Hoppy', 8, 280.00, '500 мл'),
('Вино красное', 8, 350.00, '150 мл');

-- Заказы
INSERT INTO orders (table_id, waiter_id, opened_at, closed_at, total_sum, status) VALUES
(1, 1, '2025-05-10 10:30:00', '2025-05-10 11:15:00', 950.00, 'paid'),
(2, 2, '2025-05-10 11:00:00', '2025-05-10 12:00:00', 1240.00, 'paid'),
(3, 1, '2025-05-10 12:00:00', '2025-05-10 12:45:00', 800.00, 'closed'),
(4, 3, '2025-05-10 13:00:00', NULL, 0.00, 'open'),
(5, 4, '2025-05-10 14:30:00', NULL, 0.00, 'open'),
(6, 2, '2025-05-09 19:00:00', '2025-05-09 20:30:00', 2100.00, 'paid'),
(7, 5, '2025-05-09 20:00:00', '2025-05-09 21:15:00', 1850.00, 'paid'),
(8, 1, '2025-05-11 09:00:00', '2025-05-11 09:40:00', 520.00, 'paid'),
(2, 2, '2025-05-11 10:00:00', NULL, 0.00, 'open');

-- Состав заказов
INSERT INTO order_items (order_id, dish_id, quantity, price_at_order) VALUES
(1, 1, 1, 350.00),
(1, 3, 1, 420.00),
(1, 8, 1, 120.00),
(2, 5, 1, 450.00),
(2, 9, 1, 180.00),
(2, 11, 1, 320.00),
(3, 2, 1, 280.00),
(3, 4, 1, 360.00),
(4, 7, 1, 890.00),
(4, 10, 1, 150.00),
(5, 6, 1, 420.00),
(5, 14, 2, 280.00),
(6, 7, 2, 890.00),
(6, 15, 2, 350.00),
(6, 12, 1, 350.00),
(7, 5, 2, 450.00),
(7, 3, 1, 420.00),
(7, 13, 2, 200.00),
(8, 8, 1, 120.00),
(8, 11, 1, 320.00),
(9, 1, 1, 350.00),
(9, 14, 1, 280.00);