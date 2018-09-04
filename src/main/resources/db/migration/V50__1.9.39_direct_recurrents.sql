CREATE TYPE mst.RECURRENT_TOKEN_SOURCE_TYPE AS ENUM ('payment');

ALTER TABLE mst.payment_data
  ADD COLUMN payment_recurrent_flag BOOLEAN;
ALTER TABLE mst.payment_data
  ADD COLUMN payment_recurrent_token_source_type mst.RECURRENT_TOKEN_SOURCE_TYPE;
ALTER TABLE mst.payment_data
  ADD COLUMN payment_recurrent_token_source_invoice_id CHARACTER VARYING;
ALTER TABLE mst.payment_data
  ADD COLUMN payment_recurrent_token_source_payment_id CHARACTER VARYING;
