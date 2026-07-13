INSERT INTO users ( first_name,last_name, email,password, created_at, updated_at,role)
VALUES 
('Usuario Uno','usuario uno' ,'user1@test.com',12345678 , NOW(), NOW(),'ROLE_CLIENT'),
('Usuario Dos','usuario dos', 'user2@test.com',12345678, NOW(), NOW(),'ROLE_CLIENT');
-- =============================================================================
-- 1. CUENTAS (Accounts)
-- Nota: 'version' inicia en 0 por convención de Hibernate Optimistic Locking.
-- =============================================================================
INSERT INTO accounts (user_id, name, account_type, currency, color, is_active, version, balance, created_at, updated_at, deleted_at)
VALUES 
-- Cuentas para el Usuario 1
(1, 'Cuenta de Débito Principal', 'BANK', 'MXN', '#10B981', true, 0, 15450.00, NOW(), NOW(), NULL),
(1, 'Tarjeta de Crédito Oro', 'CREDIT', 'MXN', '#EF4444', true, 0, 5000.00, NOW(), NOW(), NULL),

-- Cuentas para el Usuario 2
(2, 'Nómina BBVA', 'BANK', 'MXN', '#3B82F6', true, 0, 28000.50, NOW(), NOW(), NULL),
(2, 'Efectivo/Cartera', 'CASH', 'MXN', '#F59E0B', true, 0, 450.00, NOW(), NOW(), NULL);


-- =============================================================================
-- 2. CATEGORÍAS (Categories)
-- Nota: deleted_at se inicializa en NULL ya que usas soft delete.
-- =============================================================================
INSERT INTO categories (user_id, name, category_type, color, deleted_at, created_at, updated_at)
VALUES 
-- Categorías para el Usuario 1
(1, 'Sueldo e Ingresos', 'INCOME', '#10B981', NULL, NOW(), NOW()),
(1, 'Supermercado y Despensa', 'EXPENSE', '#F59E0B', NULL, NOW(), NOW()),
(1, 'Suscripciones y Entretenimiento', 'EXPENSE', '#6366F1', NULL, NOW(), NOW()),

-- Categorías para el Usuario 2
(2, 'Honorarios Freelance', 'INCOME', '#10B981', NULL, NOW(), NOW()),
(2, 'Restaurantes y Comida', 'EXPENSE', '#EF4444', NULL, NOW(), NOW()),
(2, 'Renta y Servicios', 'EXPENSE', '#111827', NULL, NOW(), NOW());


-- =============================================================================
-- 3. PRESUPUESTOS (Budgets)
-- Nota: 'month' mapea a un LocalDate, se usa formato 'YYYY-MM-DD'.
-- Los IDs de categoría se asumen secuenciales (1, 2, 3 para User 1 / 4, 5, 6 para User 2).
-- =============================================================================
INSERT INTO budgets (user_id, category_id, month, amount, alert_threshold, is_alert_sent, notes, deleted_at, created_at, updated_at)
VALUES 
-- Presupuestos para el Usuario 1 (Asumiendo ID Categoría 2 y 3)
(1, 2, '2026-07-01', 4000.00, 80, false, 'Presupuesto mensual para despensa', NULL, NOW(), NOW()),
(1, 3, '2026-07-01', 800.00, 90, false, 'Netflix, Spotify y YouTube Premium', NULL, NOW(), NOW()),

-- Presupuestos para el Usuario 2 (Asumiendo ID Categoría 5 y 6)
(2, 5, '2026-07-01', 3000.00, 75, false, 'Salidas del mes de Julio', NULL, NOW(), NOW()),
(2, 6, '2026-07-01', 12000.00, 100, false, 'Pago fijo de departamento y luz', NULL, NOW(), NOW());


-- =============================================================================
-- 4. TRANSACCIONES (Transactions)
-- Nota: 'date' mapea a LocalDateTime, por lo que usamos formato 'YYYY-MM-DD HH:MM:SS'.
-- Las relaciones dependen de las Cuentas creadas arriba (IDs de cuenta estimados: 1, 2, 3, 4).
-- =============================================================================
INSERT INTO transactions (user_id, category_id, account_id, transaction_type, amount, description, date, deleted_at, created_at, updated_at)
VALUES 
-- Transacciones del Usuario 1
(1, 1, 1, 'INCOME', 18000.00, 'Depósito de quincena', '2026-07-15 09:00:00', NULL, NOW(), NOW()),
(1, 2, 1, 'EXPENSE', 1250.50, 'Compras en Walmart', '2026-07-15 14:30:00', NULL, NOW(), NOW()),
(1, 3, 2, 'EXPENSE', 199.00, 'Cargo mensual Netflix', '2026-07-16 02:00:00', NULL, NOW(), NOW()),

-- Transacciones del Usuario 2
(2, 4, 3, 'INCOME', 15000.00, 'Pago proyecto Landing Page', '2026-07-12 11:15:00', NULL, NOW(), NOW()),
(2, 5, 4, 'EXPENSE', 350.00, 'Cena en Taquería', '2026-07-13 21:00:00', NULL, NOW(), NOW());


-- =============================================================================
-- 5. TRANSFERENCIAS (Transfers)
-- Nota: 'date' mapea a LocalDate ('YYYY-MM-DD'). 
-- Mueve dinero de una cuenta origen (from) a una cuenta destino (to) del mismo usuario.
-- =============================================================================
INSERT INTO transfers (user_id, from_account_id, to_account_id, amount, description, date, deleted_at, created_at, updated_at)
VALUES 
-- Usuario 1 transfiere de Débito (1) a Crédito (2) para pagar saldo
(1, 1, 2, 2500.00, 'Pago parcial de tarjeta', '2026-07-16', NULL, NOW(), NOW()),

-- Usuario 2 saca efectivo del cajero automático (De cuenta BBVA (3) a Carteras (4))
(2, 3, 4, 1000.00, 'Retiro de efectivo cajero', '2026-07-14', NULL, NOW(), NOW());