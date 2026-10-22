create table notifications (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    created_at timestamp(6) not null,
    deleted_at timestamp(6) null,
    updated_at timestamp(6) null,
    created_by int8 not null,
    updated_by int8,
    user_id int8 not null,
    budget_id int8 null,
    type varchar(40) not null,
    message varchar(255) not null,
    status varchar(20) not null default 'PENDING',
    attempts int4 not null default 0,
    sent_at timestamp(6) null,
    read_at timestamp(6) null,
    next_attempt_at timestamp(6) null,
    constraint notifications_pkey primary key (id),
    constraint fk_notification_user foreign key (user_id) references users(id),
    constraint fk_notification_budget foreign key (budget_id) references budgets(id),
    constraint notifications_status_check check (status in ('PENDING','SENT','FAILED')),
    constraint notifications_type_check check (type in ('BUDGET_THRESHOLD_REACHED'))
);

create index idx_notifications_dispatch on notifications (status, next_attempt_at);
create index idx_notifications_user on notifications (user_id, created_at desc);

update budgets set is_alert_sent = false where is_alert_sent is null;
alter table budgets alter column is_alert_sent set not null;