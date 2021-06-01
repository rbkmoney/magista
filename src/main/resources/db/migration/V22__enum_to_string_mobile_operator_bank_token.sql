ALTER TABLE mst.payment_data
    RENAME COLUMN payment_bank_card_token_provider TO payment_bank_card_token_provider_legacy;
ALTER TABLE mst.payment_data
    RENAME COLUMN payment_mobile_operator TO payment_mobile_operator_legacy;

ALTER TABLE mst.payment_data
    ADD COLUMN payment_bank_card_token_provider CHARACTER VARYING;
ALTER TABLE mst.payment_data
    ADD COLUMN payment_mobile_operator CHARACTER VARYING;
