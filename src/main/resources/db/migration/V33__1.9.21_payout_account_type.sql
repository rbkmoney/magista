CREATE TYPE mst.PAYOUT_ACCOUNT_TYPE AS ENUM ('RUSSIAN_PAYOUT_ACCOUNT', 'INTERNATIONAL_PAYOUT_ACCOUNT');

ALTER TABLE mst.payout_event_stat ADD COLUMN payout_account_type mst.PAYOUT_ACCOUNT_TYPE;
UPDATE mst.payout_event_stat SET payout_account_type = 'RUSSIAN_PAYOUT_ACCOUNT' WHERE payout_type = 'bank_account';