# MVP finanzas personales full stack

**Session ID:** ses_15c4f872bffe3P1LNR23iZQ32u
**Created:** 6/7/2026, 2:05:12 PM
**Updated:** 6/7/2026, 2:06:03 PM

---

## User

Actúa como un Product Manager y Software Architect senior.

Quiero desarrollar una aplicación full stack para gestionar mis finanzas personales utilizando JavaScript en todo el proyecto.

Stack tecnológico:

Frontend: React
Backend: Express.js
Base de datos: MYSQL
Autenticación: JWT

Mi objetivo es aprender desarrollo full stack mientras construyo una aplicación real que pueda utilizar diariamente.

Ayúdame a diseñar un MVP incluyendo:

Definición del producto y problema que resuelve.
Historias de usuario principales.
Funcionalidades imprescindibles para el MVP.
Funcionalidades que deberían quedar fuera del MVP y desarrollarse en futuras versiones.
Modelo de base de datos detallado (tablas, campos, relaciones y restricciones).
Diseño de la API REST (endpoints, métodos HTTP y ejemplos de respuestas).
Pantallas principales de React y su propósito.
Estructura de carpetas recomendada para frontend y backend.
Arquitectura de la aplicación.
Roadmap de desarrollo dividido en entregas pequeñas.
Consideraciones de seguridad y validación de datos.
Posibles mejoras futuras para convertir el proyecto en un portafolio profesional. 

---

## Assistant (Plan · Big Pickle · 9.4s)

_Thinking:_

The user wants me to act as a Product Manager and Senior Software Architect to design a full-stack personal finance management application. They want a comprehensive plan covering all the aspects they listed. Let me first explore the current state of the workspace to understand what exists, then provide a thorough architectural plan.

Let me check the current workspace first.

**Tool: read**

**Input:**
```json
{
  "filePath": "D:\\web\\Java\\Bitly-Clone"
}
```

**Output:**
```
<path>D:\web\Java\Bitly-Clone</path>
<type>directory</type>
<entries>
.git/
.gitattributes
.gitignore
.mvn/
.vscode/
HELP.md
mvnw
mvnw.cmd
pom.xml
src/
target/

(11 entries)
</entries>
```

---

## Assistant (Plan · Big Pickle · 42.1s)

_Thinking:_

The workspace currently contains a Java/Maven project (Bitly-Clone). The user wants to build a new personal finance management app with JavaScript (React + Express.js + MySQL). I should note this is an existing project directory, and we'll need to either work in a subdirectory or a new location.

Let me provide a comprehensive architectural plan. Since I'm in plan mode, I'll present the full design to the user for discussion and approval before any implementation begins.

Veo que el workspace actual tiene un proyecto Java existente (**Bitly-Clone**). Esta nueva aplicación de finanzas personales será un proyecto independiente. Podemos crearlo en una subcarpeta o en una nueva ubicación — lo discutiremos al final.

A continuación, te presento el diseño completo del **MVP**:

---

## 1. Definición del producto

**Problema:** Llevar las finanzas personales en Excel, papel o apps genéricas es tedioso, disperso y no ofrece visión clara de la salud financiera. Las apps existentes son genéricas, caras o almacenan datos en la nube de terceros.

**Solución:** Una app web full-stack auto-alojada (localhost o tu propio servidor) para registrar ingresos, gastos y presupuestos, con dashboard visual, control total de tus datos y 100% gratuita.

---

## 2. Historias de usuario (MVP)

| ID | Historia |
|---|---|
| **HU-01** | Como usuario, quiero registrarme e iniciar sesión para acceder a mis datos de forma segura. |
| **HU-02** | Como usuario, quiero agregar transacciones (ingreso/gasto) con categoría, monto, fecha y descripción. |
| **HU-03** | Como usuario, quiero ver la lista de transacciones ordenadas por fecha. |
| **HU-04** | Como usuario, quiero editar y eliminar transacciones existentes. |
| **HU-05** | Como usuario, quiero ver un dashboard con el balance total, ingresos del mes, gastos del mes. |
| **HU-06** | Como usuario, quiero definir un presupuesto mensual por categoría. |
| **HU-07** | Como usuario, quiero ver gráficos de gastos por categoría. |

---

## 3. Funcionalidades MVP (imprescindibles)

1. **Autenticación JWT** (registro, login, logout, refresh token)
2. **CRUD de transacciones** (monto, tipo [ingreso/gasto], categoría, fecha, descripción, etiquetas opcionales)
3. **CRUD de categorías** (nombre, tipo [ingreso/gasto], color, icono)
4. **Presupuestos mensuales por categoría**
5. **Dashboard** con:
   - Balance total (ingresos - gastos)
   - Ingresos y gastos del mes actual
   - Gráfico de donut: gastos por categoría (mes actual)
   - Top 5 categorías donde más gastas
6. **Filtros** por rango de fechas, tipo y categoría

---

## 4. Fuera del MVP (futuras versiones)

- Cuentas múltiples (efectivo, tarjetas, ahorros)
- Transferencias entre cuentas
- Reportes PDF/CSV exportables
- Metas de ahorro
- Recordatorios de facturas recurrentes
- Modo oscuro
- Multi-idioma
- Subida de comprobantes
- Sincronización bancaria (API externa)
- Mobile app (React Native)
- Roles y permisos multi-usuario
- Backup automático a la nube

---

## 5. Modelo de base de datos

```sql
-- Tabla: users
CREATE TABLE users (
  id          INT PRIMARY KEY AUTO_INCREMENT,
  name        VARCHAR(100) NOT NULL,
  email       VARCHAR(255) NOT NULL UNIQUE,
  password    VARCHAR(255) NOT NULL,  -- bcrypt hash
  currency    VARCHAR(3) DEFAULT 'USD',
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at  TIMESTAMP NULL          -- soft delete
);

-- Tabla: categories
CREATE TABLE categories (
  id          INT PRIMARY KEY AUTO_INCREMENT,
  user_id     INT NOT NULL,
  name        VARCHAR(100) NOT NULL,
  type        ENUM('income', 'expense') NOT NULL,
  color       VARCHAR(7) DEFAULT '#6B7280',  -- hex color
  icon        VARCHAR(50) DEFAULT 'tag',     -- icon name
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  UNIQUE KEY uq_category_user (user_id, name)
);

-- Tabla: transactions
CREATE TABLE transactions (
  id          INT PRIMARY KEY AUTO_INCREMENT,
  user_id     INT NOT NULL,
  category_id INT NOT NULL,
  type        ENUM('income', 'expense') NOT NULL,
  amount      DECIMAL(12,2) NOT NULL CHECK (amount > 0),
  description VARCHAR(500),
  date        DATE NOT NULL,
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT,
  INDEX idx_user_date (user_id, date),
  INDEX idx_user_type (user_id, type)
);

-- Tabla: budgets
CREATE TABLE budgets (
  id          INT PRIMARY KEY AUTO_INCREMENT,
  user_id     INT NOT NULL,
  category_id INT NOT NULL,
  month       DATE NOT NULL,               -- primer día del mes (2025-01-01)
  amount      DECIMAL(12,2) NOT NULL CHECK (amount > 0),
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
  UNIQUE KEY uq_budget_month_category (user_id, category_id, month)
);

CREATE TABLE accounts (
  id          INT PRIMARY KEY AUTO_INCREMENT,
  user_id     INT NOT NULL,
  name        VARCHAR(100) NOT NULL,  -- "BBVA", "Efectivo", "Débito"
  type        ENUM('cash', 'bank', 'credit', 'savings') NOT NULL,
  balance     DECIMAL(12,2) DEFAULT 0,
  currency    VARCHAR(3) DEFAULT 'MXN',
  color       VARCHAR(7) DEFAULT '#6B7280',
  icon        VARCHAR(50) DEFAULT 'wallet',
  is_active   BOOLEAN DEFAULT TRUE,
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE transfers (
  id                INT PRIMARY KEY AUTO_INCREMENT,
  user_id           INT NOT NULL,
  from_account_id   INT NOT NULL,
  to_account_id     INT NOT NULL,
  amount            DECIMAL(12,2) NOT NULL CHECK (amount > 0),
  description       VARCHAR(500),
  date              DATE NOT NULL,
  created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (from_account_id) REFERENCES accounts(id),
  FOREIGN KEY (to_account_id) REFERENCES accounts(id)
);
```

**Relaciones:**

- `users` 1:N `categories`, `transactions`, `budgets`
- `categories` 1:N `transactions`, `budgets`
- `transactions` siempre referencia una categoría del mismo `user_id`

---

## 6. Diseño de API REST

### Endpoints

| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| POST | `/api/auth/register` | No | Registrar usuario |
| POST | `/api/auth/login` | No | Iniciar sesión → devuelve JWT |
| POST | `/api/auth/refresh` | No | Refrescar token |
| POST | `/api/auth/logout` | Sí | Invalidar refresh token |
| GET | `/api/users/me` | Sí | Perfil del usuario actual |
| PATCH | `/api/users/me` | Sí | Actualizar perfil |
| GET | `/api/categories` | Sí | Listar categorías del usuario |
| POST | `/api/categories` | Sí | Crear categoría |
| PATCH | `/api/categories/:id` | Sí | Editar categoría |
| DELETE | `/api/categories/:id` | Sí | Eliminar categoría |
| GET | `/api/transactions` | Sí | Listar transacciones (con filtros: `?startDate=&endDate=&type=&categoryId=&page=&limit=`) |
| POST | `/api/transactions` | Sí | Crear transacción |
| GET | `/api/transactions/:id` | Sí | Detalle de transacción |
| PATCH | `/api/transactions/:id` | Sí | Editar transacción |
| DELETE | `/api/transactions/:id` | Sí | Eliminar transacción |
| GET | `/api/budgets` | Sí | Listar presupuestos (filtro `?month=`) |
| POST | `/api/budgets` | Sí | Crear/actualizar presupuesto |
| DELETE | `/api/budgets/:id` | Sí | Eliminar presupuesto |
| GET | `/api/dashboard/summary` | Sí | Balance, ingresos/gastos del mes |
| GET | `/api/dashboard/by-category` | Sí | Gastos agrupados por categoría (rango de fechas) |
| GET | `/api/dashboard/trends` | Sí | Ingresos y gastos mensuales (últimos 12 meses) |

### Ejemplos de respuestas

```jsonc
// POST /api/auth/login
{ "token": "eyJ...", "refreshToken": "eyJ...", "user": { "id": 1, "name": "Juan", "email": "juan@mail.com" } }

// GET /api/transactions?page=1&limit=10
{ "data": [
    { "id": 42, "type": "expense", "amount": 25.50, "description": "Uber", "date": "2025-06-07",
      "category": { "id": 3, "name": "Transporte", "color": "#F59E0B" } }
  ], "pagination": { "page": 1, "limit": 10, "total": 87 } }

// GET /api/dashboard/summary
{ "balance": 1250.00, "incomeMonth": 3200.00, "expenseMonth": 1950.00,
  "budgetUsed": 65.5, // porcentaje del presupuesto total usado
  "topCategories": [ { "name": "Comida", "amount": 450.00, "percentage": 23.1 } ] }
```

---

## 7. Pantallas de React (MVP)

| Ruta | Componente | Propósito |
|---|---|---|
| `/login` | `LoginPage` | Inicio de sesión |
| `/register` | `RegisterPage` | Registro de nuevo usuario |
| `/dashboard` | `DashboardPage` | Resumen con cards, gráfico donut, top categorías |
| `/transactions` | `TransactionsPage` | Lista paginada de transacciones con filtros |
| `/transactions/new` | `TransactionFormPage` | Formulario para nueva transacción |
| `/transactions/:id/edit` | `TransactionFormPage` | Editar transacción existente |
| `/categories` | `CategoriesPage` | CRUD de categorías (modal inline) |
| `/budgets` | `BudgetsPage` | Presupuestos por mes y categoría con barra de progreso |
| `/profile` | `ProfilePage` | Editar perfil, cambio contraseña |

**Layout:** Sidebar con navegación + Header con avatar y moneda. Un `<Outlet />` de React Router para el contenido.

---

## 8. Estructura de carpetas

```
finanzas-personales/
├── client/                        # React (Vite)
│   ├── public/
│   │   └── favicon.ico
│   ├── src/
│   │   ├── components/            # Componentes reutilizables
│   │   │   ├── ui/                #   Button, Input, Modal, Card, Spinner
│   │   │   ├── layout/            #   Sidebar, Header, AppLayout
│   │   │   ├── charts/            #   DonutChart, BarChart, TrendLine
│   │   │   └── guards/            #   ProtectedRoute, GuestRoute
│   │   ├── pages/                 # Páginas del router (1 por ruta)
│   │   │   ├── LoginPage.jsx
│   │   │   ├── RegisterPage.jsx
│   │   │   ├── DashboardPage.jsx
│   │   │   ├── TransactionsPage.jsx
│   │   │   ├── TransactionFormPage.jsx
│   │   │   ├── CategoriesPage.jsx
│   │   │   ├── BudgetsPage.jsx
│   │   │   └── ProfilePage.jsx
│   │   ├── hooks/                 # Custom hooks (useAuth, useTransactions, etc.)
│   │   ├── services/              # Llamadas a la API (axios)
│   │   │   ├── api.js             #   Instancia de axios con interceptor JWT
│   │   │   ├── authService.js
│   │   │   ├── transactionService.js
│   │   │   ├── categoryService.js
│   │   │   ├── budgetService.js
│   │   │   └── dashboardService.js
│   │   ├── context/               # React Context (AuthContext)
│   │   ├── utils/                 # formatters, validators, constants
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── index.html
│   ├── vite.config.js
│   └── package.json
│
├── server/                        # Express.js
│   ├── src/
│   │   ├── config/                # db.js (mysql2 pool), env.js
│   │   ├── middleware/
│   │   │   ├── auth.js            #   Verificar JWT
│   │   │   ├── validate.js        #   Validación con Joi/Zod
│   │   │   └── errorHandler.js    #   Manejador global de errores
│   │   ├── routes/
│   │   │   ├── auth.routes.js
│   │   │   ├── user.routes.js
│   │   │   ├── category.routes.js
│   │   │   ├── transaction.routes.js
│   │   │   ├── budget.routes.js
│   │   │   └── dashboard.routes.js
│   │   ├── controllers/           # Lógica de cada endpoint
│   │   ├── models/                # Queries SQL (raw con mysql2/promise)
│   │   ├── validators/            # Schemas Zod/Joi
│   │   ├── utils/                 # jwt.js, helpers
│   │   └── app.js                 # Express app setup
│   ├── db/
│   │   ├── schema.sql             # DDL completo
│   │   └── seeds.sql              # Datos iniciales (categorías por defecto)
│   ├── .env.example
│   ├── package.json
│   └── nodemon.json
│
└── README.md                      # NO crear sin solicitud explícita
```

---

## 9. Arquitectura de la aplicación

```
┌─────────────────────────────────────────────────────────┐
│                    CLIENT (React + Vite)                 │
│  ┌──────────┐  ┌──────────┐  ┌───────────────────────┐  │
│  │ AuthCtx  │  │   Hooks  │  │   React Router (SPA)  │  │
│  │ (JWT)    │  │  (data)  │  │   /dashboard          │  │
│  └────┬─────┘  └────┬─────┘  │   /transactions       │  │
│       │              │        │   /categories         │  │
│  ┌────▼──────────────▼──────┐ │   /budgets            │  │
│  │   services/api.js        │ │                       │  │
│  │   (axios + interceptors) │ └───────────────────────┘  │
│  └────────────┬─────────────┘                            │
└───────────────┼─────────────────────────────────────────┘
                │ HTTP (JSON)
                │ JWT en Authorization: Bearer <token>
┌───────────────┼─────────────────────────────────────────┐
│    SERVER (Express.js)                                   │
│  ┌────────────┴────────────┐                             │
│  │    Middleware Chain      │                             │
│  │  cors → json → auth     │                             │
│  │  → validate → controller│                             │
│  └────────────┬────────────┘                             │
│  ┌────────────▼────────────┐                             │
│  │   Controllers           │                             │
│  │   (req/res handling)    │                             │
│  └────────────┬────────────┘                             │
│  ┌────────────▼────────────┐                             │
│  │   Models (SQL queries)   │                             │
│  │   mysql2/promise pool    │                             │
│  └────────────┬────────────┘                             │
└───────────────┼─────────────────────────────────────────┘
                │ TCP (MySQL protocol)
┌───────────────┼─────────────────────────────────────────┐
│   ┌───────────▼───────────┐                              │
│   │      MySQL 8.x        │                              │
│   │  finanzas_db schema   │                              │
│   └───────────────────────┘                              │
└──────────────────────────────────────────────────────────┘
```

**Principios:**
- **Backend:** Arquitectura en capas (routes → middleware → controllers → models)
- **Frontend:** Component-based con separación de responsabilidades (pages → components → hooks → services)
- **Comunicación:** REST sobre HTTP, JSON, stateless con JWT
- **Estado global:** Solo autenticación via React Context. Datos de negocio via hooks locales + servicios

---

## 10. Roadmap de desarrollo

| Entrega | Semana | Alcance |
|---|---|---|
| **E1** | 1 | Scaffolding: `client` (Vite+React), `server` (Express), conexión MySQL, `schema.sql`, `.env`. |
| **E2** | 1 | Auth completo: `POST /auth/register`, `/login`, JWT, AuthContext, LoginPage, RegisterPage, ProtectedRoute. |
| **E3** | 2 | CRUD Categorías: endpoints + CategoriesPage con modal create/edit, colores e íconos. |
| **E4** | 2-3 | CRUD Transacciones: endpoints con filtros + TransactionsPage, TransactionFormPage, paginación. |
| **E5** | 3 | Dashboard: endpoints (`/summary`, `/by-category`) + DashboardPage con donut chart y cards. |
| **E6** | 3-4 | Presupuestos: endpoints + BudgetsPage con barras de progreso. |
| **E7** | 4 | Perfil, refinamiento UI, manejo de errores global, testing manual. |

**Total estimado:** 4 semanas (trabajando evenings/weekends).

---

## 11. Consideraciones de seguridad y validación

- **Passwords:** `bcrypt` con salt rounds = 12 en servidor
- **JWT:** Access token 15 min (corto), refresh token 7 días (httpOnly cookie o almacenado en DB)
- **Validación:** Zod schemas en backend para cada input. Sanitización en frontend.
- **SQL Injection:** Usar `mysql2` con parametrized queries (nunca concatenar strings)
- **CORS:** Solo permitir origen del frontend en producción
- **Headers:** Helmet.js para seguridad HTTP
- **Rate limiting:** `express-rate-limit` en rutas de auth (5 intentos/min)
- **Soft delete:** `deleted_at` en usuarios, no destruir datos
- **XSS:** React escapa por defecto. No usar `dangerouslySetInnerHTML`.
- **Input validation:** Zod verifica tipos, longitudes, formatos. Rechazar `amount <= 0`, `date` inválida.
- **Authorization:** Cada endpoint valida que `resource.user_id === req.user.id`

---

## 12. Mejoras futuras para portafolio profesional

| Mejora | Impacto |
|---|---|
| Tests automatizados (Jest + React Testing Library + Supertest) | Demuestra calidad |
| CI/CD con GitHub Actions (lint, test, build, deploy) | DevOps |
| Docker Compose (app + mysql + adminer) | Infraestructura como código |
| TypeScript en frontend y backend | Tipado estático |
| TanStack Query (React Query) + Zustand | Estado y caching avanzado |
| Storybook para componentes UI | Documentación visual |
| Despliegue en Vercel (frontend) + Railway/Render (backend) | Producción real |
| Monorepo con Turborepo o Nx | Escalabilidad |
| PWA (offline support con Service Workers) | Experiencia mobile |
| Internacionalización (react-i18next) | Alcance global |
| GraphQL (Apollo) como alternativa a REST | Stack moderno |
| e2e con Playwright | Cobertura total |

---

## 📌 Preguntas para definir antes de empezar

1. **Ubicación del proyecto:** ¿Creamos el proyecto en una subcarpeta tipo `finanzas-app/` dentro del workspace actual (junto al proyecto Java) o prefieres otra ubicación?
2. **Package manager:** ¿npm o yarn?
3. **Íconos y UI:** ¿Usamos alguna librería de componentes? Recomiendo **shadcn/ui** (basado en Tailwind) por ser moderna y didáctica. ¿O prefieres Material UI, Chakra, o CSS puro?
4. **Gráficos:** Recomiendo **Recharts** (nativo React). ¿Te parece bien?
5. **Validación:** Zod en backend. En frontend, ¿quíeres Zod también o algo más simple como `react-hook-form`?
6. **¿Quieres que comencemos con la Entrega 1 (scaffolding) cuando estés listo?**

---

