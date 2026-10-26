ALTER TABLE accounts
    DROP CONSTRAINT IF EXISTS accounts_account_type_check;

ALTER TABLE accounts
    ADD CONSTRAINT accounts_account_type_check
    CHECK (account_type IN ('CASH', 'BANK', 'CREDIT', 'SAVINGS'));