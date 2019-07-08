ALTER TABLE mst.payment_data RENAME COLUMN payment_bank_card_bin TO payment_bank_card_first6;

ALTER TABLE mst.payment_data RENAME COLUMN payment_bank_card_masked_pan TO payment_bank_card_last4;

CREATE INDEX payment_data_payment_first6_idx ON payment_data (payment_bank_card_first6);
CREATE INDEX payment_data_payment_last4_idx ON payment_data (payment_bank_card_last4);
