ALTER TABLE mst.payment_event
  ADD COLUMN payment_amount BIGINT;
ALTER TABLE mst.payment_event
  ADD COLUMN payment_currency_code CHARACTER VARYING;

ALTER TABLE mst.payment_data rename COLUMN payment_amount TO payment_origin_amount;
