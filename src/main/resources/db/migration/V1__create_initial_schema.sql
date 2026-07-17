create table users (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    created_at timestamp(6) not null,
    deleted_at timestamp(6) null,
    updated_at timestamp(6) null,
	created_by INT8 NOT NULL,
	updated_by INT8,
    email varchar(255) null,
    first_name varchar(255) null,
    last_name varchar(255) null,
    password varchar(255) null,
    role varchar(255) not null,
    constraint users_pkey primary key (id),
    constraint users_role_check CHECK (role IN ('ROLE_CLIENT', 'ROLE_SUPPORT', 'ROLE_ADMIN'))
);

create unique index uq_user_email_active on users (email) where (deleted_at is null);


create table accounts (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    created_at timestamp(6) not null,
    deleted_at timestamp(6) null,
    updated_at timestamp(6) null,
	created_by INT8 NOT NULL,
	updated_by INT8,
    account_type varchar(255) not null,
    balance numeric(12, 2) not null,
    color varchar(7) null,
    currency varchar(3) null,
    is_active bool null DEFAULT TRUE,
    name varchar(100) not null,
    "version" int8 not null,
    user_id int8 not null,
    constraint accounts_pkey primary key (id),
    constraint accounts_account_type_check check (account_type IN ('BANK', 'CREDIT', 'SAVINGS')),
    constraint fk_account_user foreign key (user_id) references users(id)
);


create table categories (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    created_at timestamp(6) not null,
    deleted_at timestamp(6) null,
    updated_at timestamp(6) null,
	created_by INT8 NOT NULL,
	updated_by INT8,
    category_type varchar(255) not null,
    color varchar(7) null,
    name varchar(100) not null,
    user_id int8 not null,
    constraint categories_pkey primary key (id),
    constraint categories_category_type_check check (category_type IN ('INCOME', 'EXPENSE')),
    constraint fk_category_id foreign key (user_id) references users(id)
);

create unique index uq_category_user_active on categories (user_id, name) where (deleted_at is null);


create table budgets (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    created_at timestamp(6) not null,
    deleted_at timestamp(6) null,
    updated_at timestamp(6) null,
	created_by INT8 NOT NULL,
	updated_by INT8,
    alert_threshold int4 null,
    amount numeric(12, 2) not null,
    is_alert_sent bool null DEFAULT FALSE,
    month date not null,
    notes varchar(100) null,
    category_id int8 not null,
    user_id int8 not null,
    constraint budgets_pkey primary key (id),
    constraint budgets_alert_threshold_check check ((alert_threshold >= 1 and alert_threshold <= 100)),
    constraint fk_budget_user foreign key (user_id) references users(id),
    constraint fk_budget_category foreign key (category_id) references categories(id)
);

create unique index uq_budget_month_category_active on budgets (user_id, category_id, month) where (deleted_at is null);


create table transfers (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    created_at timestamp(6) not null,
    deleted_at timestamp(6) null,
    updated_at timestamp(6) null,
	created_by INT8 NOT NULL,
	updated_by INT8,
    amount numeric(12, 2) not null,
    "date" date not null,
    description varchar(100) null,
    from_account_id int8 not null,
    to_account_id int8 not null,
    user_id int8 not null,
    constraint transfers_pkey primary key (id),
    constraint fk_transfer_to_account foreign key (to_account_id) references accounts(id),
    constraint fk_transfer_user foreign key (user_id) references users(id),
    constraint fk_transfer_from_account foreign key (from_account_id) references accounts(id)
);


create table transactions (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    created_at timestamp(6) not null,
    deleted_at timestamp(6) null,
    updated_at timestamp(6) null,
	created_by INT8 NOT NULL,
	updated_by INT8,
    amount numeric(12, 2) not null,
    "date" timestamp(6) not null, 
    description varchar(100) null,
    transaction_type varchar(255) not null,
    account_id int8 not null,
    category_id int8 not null,
    user_id int8 not null,
    constraint transactions_pkey primary key (id),
    constraint transactions_transaction_type_check check (transaction_type IN ('INCOME', 'EXPENSE')),
    constraint fk_transaction_account foreign key (account_id) references accounts(id),
    constraint fk_transaction_user foreign key (user_id) references users(id),
    constraint fk_transaction_category foreign key (category_id) references categories(id)
);


create table tokens (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    created_at timestamp(6) null,
    expires_at timestamp(6) null,
    revoked bool not null,
    "token" varchar(500) not null,
    validated_at timestamp(6) null,
    user_id int8 not null,
    constraint tokens_pkey primary key (id),
    constraint uq_token unique (token),
    constraint fk_token_user foreign key (user_id) references users(id)
);


create index idx_transfer_from_account on transfers using btree (from_account_id);
create index idx_transfer_to_account on transfers using btree (to_account_id);
create index idx_transfer_user_date on transfers using btree (user_id, date);
create index idx_user_date on transactions using btree (user_id, date);
create index idx_user_transaction_type on transactions using btree (user_id, transaction_type);