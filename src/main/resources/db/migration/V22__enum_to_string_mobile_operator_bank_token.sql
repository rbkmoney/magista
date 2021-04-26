ALTER TABLE mst.payment_data
    ALTER COLUMN payment_bank_card_token_provider TYPE VARCHAR USING payment_bank_card_token_provider::VARCHAR;
ALTER TABLE mst.payment_data
    ALTER COLUMN payment_mobile_operator TYPE VARCHAR USING payment_mobile_operator::VARCHAR;

DROP TYPE mst.mobile_operator_type;
DROP TYPE mst.bank_card_token_provider;
