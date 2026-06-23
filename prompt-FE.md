Actúa como un Desarrollador Frontend Senior experto en React, TypeScript, Tailwind CSS y Shadcn UI

Quiero que construyas el Frontend para mi aplicación de gestión de finanzas personales . A continuación, te proporciono el diseño exacto de mi Base de Datos (MySQL) y los contratos de mis Endpoints de la API (Spring Boot) para que entiendas la lógica de negocio y las estructuras de datos que vas a consumir.

### 1. MODELO DE DATOS (BACKEND)
CREATE TABLE users (
  id          INT PRIMARY KEY AUTO_INCREMENT,
  name        VARCHAR(100) NOT NULL,
  email       VARCHAR(255) NOT NULL UNIQUE,
  password    VARCHAR(255) NOT NULL,
  currency    VARCHAR(3) DEFAULT 'USD',
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at  TIMESTAMP NULL
);

CREATE TABLE accounts (
  id          INT PRIMARY KEY AUTO_INCREMENT,
  user_id     INT NOT NULL,
  name        VARCHAR(100) NOT NULL,
  type        ENUM('cash', 'bank', 'credit', 'savings') NOT NULL,
  currency    VARCHAR(3) DEFAULT 'MXN',
  color       VARCHAR(7) DEFAULT '#6B7280',
  is_active   BOOLEAN DEFAULT TRUE,
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE categories (
  id          INT PRIMARY KEY AUTO_INCREMENT,
  user_id     INT NOT NULL,
  name        VARCHAR(100) NOT NULL,
  type        ENUM('income', 'expense') NOT NULL,
  color       VARCHAR(7) DEFAULT '#6B7280',
  icon        VARCHAR(50) DEFAULT 'tag',
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  UNIQUE KEY uq_category_user (user_id, name)
);

CREATE TABLE transactions (
  id          INT PRIMARY KEY AUTO_INCREMENT,
  user_id     INT NOT NULL,
  category_id INT NOT NULL,
  account_id  INT NOT NULL,
  type        ENUM('income', 'expense') NOT NULL,
  amount      DECIMAL(12,2) NOT NULL CHECK (amount > 0),
  description VARCHAR(500),
  date        DATE NOT NULL,
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id)     REFERENCES users(id)       ON DELETE CASCADE,
  FOREIGN KEY (category_id) REFERENCES categories(id)  ON DELETE RESTRICT,
  FOREIGN KEY (account_id)  REFERENCES accounts(id)    ON DELETE RESTRICT,
  INDEX idx_user_date (user_id, date),
  INDEX idx_user_type (user_id, type)
);

CREATE TABLE budgets (
  id               INT PRIMARY KEY AUTO_INCREMENT,
  user_id          INT NOT NULL,
  category_id      INT NOT NULL,
  month            DATE NOT NULL,
  amount           DECIMAL(12,2) NOT NULL CHECK (amount > 0),
  alert_threshold  INT DEFAULT 80,
  notes            VARCHAR(500),
  created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id)     REFERENCES users(id)      ON DELETE CASCADE,
  FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
  UNIQUE KEY uq_budget_month_category (user_id, category_id, month)
);

CREATE TABLE transfers (
  id               INT PRIMARY KEY AUTO_INCREMENT,
  user_id          INT NOT NULL,
  from_account_id  INT NOT NULL,
  to_account_id    INT NOT NULL,
  amount           DECIMAL(12,2) NOT NULL CHECK (amount > 0),
  description      VARCHAR(500),
  date             DATE NOT NULL,
  created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id)          REFERENCES users(id)    ON DELETE CASCADE,
  FOREIGN KEY (from_account_id)  REFERENCES accounts(id) ON DELETE RESTRICT,
  FOREIGN KEY (to_account_id)    REFERENCES accounts(id) ON DELETE RESTRICT
);


### 2. CONTRATOS DE LA API (ENDPOINTS)
Toda la aplicación es multiusuario. Excepto por el flujo de autenticación, todos los endpoints requieren un Bearer Token JWT en las cabeceras. El backend extrae el usuario autenticado automáticamente del token, por lo que el frontend NO debe enviar IDs de usuario en las URLs ni en los cuerpos de los JSON.


| Método | Endpoint | Auth | Descripción |
|--- | --- | --- | --- |
| POST | `/api/auth/register` | No | Registrar usuario |
| POST | `/api/auth/login` | No | Iniciar sesión |
| POST | `/api/auth/refresh` | No | Refrescar token |
| POST | `/api/auth/logout` | Sí | Invalidar token |
| GET | `/api/users/me` | Sí | Perfil del usuario |
| PATCH | `/api/users/me` | Sí | Actualizar perfil |
| GET | `/api/accounts` | Sí | Listar cuentas |
| POST | `/api/accounts` | Sí | Crear cuenta |
| PATCH | `/api/accounts/:id` | Sí | Editar cuenta |
| DELETE | `/api/accounts/:id` | Sí | Desactivar cuenta |
| GET | `/api/accounts/:id/balance` | Sí | Balance de cuenta |
| GET | `/api/categories` | Sí | Listar categorías |
| POST | `/api/categories` | Sí | Crear categoría |
| PATCH | `/api/categories/:id` | Sí | Editar categoría |
| DELETE | `/api/categories/:id` | Sí | Eliminar categoría |
| GET | `/api/transactions` | Sí | Listar con filtros |
| POST | `/api/transactions` | Sí | Crear transacción |
| GET | `/api/transactions/:id` | Sí | Detalle |
| PATCH | `/api/transactions/:id` | Sí | Editar |
| DELETE | `/api/transactions/:id` | Sí | Eliminar |
| GET | `/api/transfers` | Sí | Listar transferencias |
| POST | `/api/transfers` | Sí | Crear transferencia |
| DELETE | `/api/transfers/:id` | Sí | Eliminar transferencia |
| GET | `/api/budgets` | Sí | Listar presupuestos |
| POST | `/api/budgets` | Sí | Crear presupuesto |
| DELETE | `/api/budgets/:id` | Sí | Eliminar presupuesto |
| GET | `/api/budgets/:id/summary` | Sí | Budget + spent + remaining |
| GET | `/api/dashboard/summary` | Sí | Balance general del mes |
| GET | `/api/dashboard/by-category` | Sí | Gastos por categoría |
| GET | `/api/dashboard/by-account` | Sí | Balance por cuenta |
| GET | `/api/dashboard/trends` | Sí | Tendencias 12 meses |



### 3. FORMATOS DE RESPUESTA CLAVE (EJEMPLOS)
Para la paginación y las entidades core, el backend responde exactamente con estas estructuras:

- Respuesta Paginada (Ej. GET /api/categories):
{
  "content": [{"id":1, "name":"Comida", "categoryType":"EXPENSE", "color":"#6B7280", "created_at":"2026-06-11T14:30:00"}],
  "number": 0, "size": 10, "total": 1, "totalPages": 1, "first": true, "last": true
}

- Balance de Cuentas (GET /api/accounts/:id/balance):
{ "accountId": 1, "accountName": "Nómina", "balance": 14250.75, "currency": "MXN", "updatedAt": "2026-06-11T11:55:53" }

### 4. REQUERIMIENTOS DEL FRONTEND A GENERAR
Quiero que estructures el proyecto utilizando las mejores prácticas (Clean Architecture en Frontend, componentes reutilizables, hooks personalizados para fetch de datos o React Query):

1. Autenticación: Vistas de Login, Register con manejo de estado para el Access Token y Refresh Token.
2. Estado Global / Contexto: Un AuthContext que almacene el token y los datos del perfil extraídos de `/api/users/me`.
3. Módulo de Categorías: 
   - Vista de listado usando la estructura de paginación proporcionada.
   - Modales/Formularios para Crear (POST) y Editar (PATCH) con validación de nombres (mínimo 4 caracteres) y selector de color hexadecimal.
4. Módulo de Cuentas: Listado de cuentas con sus balances reales y botón para desactivar cuentas (DELETE que ejecuta el soft delete).
5. Flujos Financieros (Especial cuidado con la lógica de negocio):
   - Formulario de Transacción: Debe permitir elegir Cuenta, Categoría, Tipo (Income/Expense) y Monto (BigDecimal).
   - Formulario de Transferencia: Debe obligar a seleccionar una Cuenta Origen (from_account_id) y una Cuenta Destino (to_account_id) diferentes, manejando el monto.
6. Dashboard: Componentes gráficos para renderizar los datos analíticos provistos por los endpoints de `/dashboard/*` (Resumen del mes, gastos por categoría con gráficos de dona/pastel, tendencias de 12 meses con gráficos de líneas).

Por favor, comienza generando la estructura de carpetas sugerida y el código para login, register, CRUD CATEGORY, CRUD ACCOUNT. Dame código limpio, tipado estricto con TypeScript y manejo de errores visible para el usuario si la API devuelve un código de error.

NOTA: No uses la base de datos interna de Supabase para la lógica. Toda la data debe ser consumida de mi API externa de Spring Boot mediante Fetch usando el Bearer Token. No implementes ni crees ninguna llamada a ninguna API, solo crea las pages / vistas pero no crees servicios  que usen fecth ni hagan llamadas a ninguna API.

SOLO CREA LAS VISTAS