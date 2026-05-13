# cafe-administrator

# 🍽️ SELECT * FROM Menu — Система учёта заказов в кафе

**Java-приложение** с графическим интерфейсом для управления заказами, столиками и меню.  
Поддерживает **PostgreSQL** и **MySQL** (переключение через конфиг).  
Работает на **macOS**, **Windows**, **Linux**.

![Java](https://img.shields.io/badge/Java-17-orange)
![Swing](https://img.shields.io/badge/GUI-Swing-blue)
![Database](https://img.shields.io/badge/Database-PostgreSQL%20%7C%20MySQL-green)
![Docker](https://img.shields.io/badge/Docker-Ready-blue)

---

## 📦 Что входит в проект

- **GUI на Swing**: карта зала, список активных заказов, панель статистики
- **Поддержка двух СУБД**: PostgreSQL 15 + MySQL 8
- **Docker Compose** для быстрого поднятия БД
- **DAO + JDBC** (без Hibernate)
- **Тестовые данные**: 7 таблиц, 5+ записей в каждой, реальные официанты и блюда

---

## 🚀 Быстрый старт (для одногруппника / проверяющего)

### 1. Клонируй репозиторий
```bash
git clone https://github.com/OneAstr0/cafe-administrator.git
cd cafe-administrator

**### 2. Запусти базы данных через Docker**
```bash
docker-compose up -d
Будут подняты контейнеры:

PostgreSQL → localhost:5433 (база cafe_db, пользователь postgres, пароль root)

MySQL → localhost:3306 (база cafe_db, пользователь root, пароль root)

Проверить можно командой docker ps.

**### 3. Настрой активную СУБД**
Открой файл resources/config.properties и выбери нужную:

```properties
# Для PostgreSQL
active=postgresql

# Или для MySQL
active=mysql

**### 4. Запусти приложение**
Вариант А — через IDE (IntelliJ IDEA)

Открой проект как File → Open

Запусти main в классе App.java

Вариант Б — из терминала (если нет IDE)

```bash
javac -cp ".;postgresql-42.7.10.jar;mysql-connector-j-8.4.0.jar" src/App.java
java -cp ".;src;postgresql-42.7.10.jar;mysql-connector-j-8.4.0.jar" App

**### 5. Начинай работу**
Зелёный стол → свободен (клик → новый заказ)

Красный стол → занят (клик → редактирование заказа)

Правая панель → список активных заказов + статистика


**🗄️ Структура базы данных (7 таблиц)**
Таблица	 |  Описание
users  	Пользователи системы (admin / waiter)
waiters	  Официанты (ФИО, телефон, смена, связь с user)
tables  	Столики (номер, кол-во мест, статус free/occupied)
categories	  Категории с иерархией (супы, салаты… → паста, мясные)
dishes  	Блюда (название, категория, цена, вес/объём)
orders	  Заказы (столик, официант, время, итог, статус)
order_items	  Состав заказа (блюдо, количество, цена на момент заказа)



**⚙️ Устройство кода**
text
src/
├── App.java                # Точка входа
├── db/DBConnectionManager  # Подключение к БД (поддержка 2 СУБД)
├── model/                  # Entity-классы (Order, Table, Dish...)
├── dao/                    # Data Access Objects (работа с таблицами)
└── ui/                     # GUI: главное окно, карта зала, диалоги

DBConnectionManager сам читает config.properties, подгружает нужный драйвер, создаёт таблицы и тестовые данные при первом запуске

DAO используют PreparedStatement, нет SQL-инъекций

Swing отрисовывает карту зала с кликабельными столами (цвет: зелёный/красный; при наведении — жирная обводка)
