CREATE TYPE mst.bank_card_token_provider AS ENUM ('applepay', 'googlepay', 'samsungpay');

ALTER TABLE mst.invoice_event_stat ADD COLUMN payment_bank_card_token_provider mst.bank_card_token_provider;