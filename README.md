# 🥦 GreenGrocer — Online Market Application

> **CMPE343 – Software Engineering** | Bahçeşehir University

GreenGrocer is a JavaFX desktop application that supports three user roles: Customer, Carrier, and Store Owner. Users can browse products, manage their cart, place orders, generate PDF invoices, and earn loyalty rewards through a coupon system.

---

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Setup & Running](#setup--running)
- [Database](#database)
- [Team & Task Distribution](#team--task-distribution)

---

## ✨ Features

- 🔐 Role-based login: Customer / Carrier / Store Owner
- 🛒 Product listing, cart management, and order placement
- 📦 Shipment tracking and status updates
- 🎫 Coupon system and customer loyalty points
- 🧾 Automatic PDF invoice generation via iText
- 💬 In-app messaging between users
- 🖼️ Product image management

---

## 🛠️ Tech Stack

| Layer      | Technology                        |
|------------|-----------------------------------|
| UI         | JavaFX 23 + FXML + CSS            |
| Backend    | Java (MVC Architecture)           |
| Database   | MySQL                             |
| PDF        | iText 5.5.13                      |
| DB Driver  | MySQL Connector/J 8.0.33          |

---

## 📁 Project Structure

```
CMPE343_GreenGrocer/
├── src/
│   ├── app/            # Application entry point (Launcher.java)
│   ├── model/          # Entity classes (User, Product, Order, ...)
│   ├── view/           # FXML UI files + styles.css
│   ├── controller/     # JavaFX Controller classes
│   ├── dao/            # Data Access Objects (DAO layer)
│   ├── service/        # Business logic services (Invoice, PDF)
│   └── util/           # Utility classes (DBUtil, etc.)
├── libs/               # External libraries (JavaFX SDK, iText, MySQL)
├── resources/          # Static resources
├── sql/                # SQL schema files
└── db.sql              # Base schema
```

---

## ⚙️ Setup & Running

### Prerequisites
- Java 17+
- MySQL 8.0+
- IntelliJ IDEA (or any Java IDE)

### Steps

1. **Clone the repository:**
   ```bash
   git clone https://github.com/<username>/CMPE343_GreenGrocer.git
   cd CMPE343_GreenGrocer
   ```

2. **Set up the database:**
   - Run the schema script (creates the database + tables + sample data automatically):
     ```bash
     mysql -u root -p < schema.sql
     ```

3. **Configure the database connection:**
   - Set your MySQL credentials as environment variables (in your shell or the IDE run configuration):
     ```bash
     export DB_PASSWORD=your_mysql_password
     export DB_USER=root                                   # optional, defaults to root
     export DB_URL=jdbc:mysql://localhost:3306/greengrocer # optional, defaults to local greengrocer DB
     ```

4. **Open the project in your IDE:**
   - Add the `libs/` folder to the project libraries (JavaFX SDK, iText JAR, MySQL Connector JAR).
   - Run `src/app/Launcher.java`.

---

## 🗄️ Database

The project uses a MySQL database. The schema is available in `db.sql`.

### Main Tables
- `users`, `customers`, `owners`, `carriers`
- `products`, `orders`, `order_details`
- `coupons`, `customer_loyalty`
- `messages`, `invoices`, `carrier_ratings`

---

## 👥 Team & Task Distribution

| Name | Role | Responsibilities |
|------|------|-----------------|
| **Elif Gülüm** | Backend | DAO layer, database connection (`DBUtil`), `OrderDAO`, `UserDAO`, `ProductDAO`, business logic services, `CarrierController` |
| **Yağmur Güzeler** | Full Stack | FXML UI files (`owner.fxml`, `customer.fxml`, `cart.fxml`, etc.), `styles.css`, UI design and layout; `CouponDAO` (coupon management), `CustomerLoyaltyDAO` (loyalty point tracking) |
| **Zeynep Duygu Ortancıl** | Full Stack | `CustomerController`, `CartController`, model classes (`Order`, `Product`, `CartItem`), order flow |
| **Ahmet Furkan Gökbulut** | Full Stack | `MessagesController`, `MyOrdersController`, shipment tracking system, messaging module |

---

## 📄 License

This project was developed for academic purposes as part of the CMPE343 – Software Engineering course.
