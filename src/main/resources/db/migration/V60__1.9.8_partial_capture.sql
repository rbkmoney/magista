ALTER TABLE mst.payment_event
  ADD COLUMN payment_amount BIGINT;
ALTER TABLE mst.payment_event
  ADD COLUMN payment_currency_code CHARACTER VARYING;

UPDATE mst.payment_event
SET payment_amount = payment_data.payment_amount,
    payment_currency_code = payment_data.payment_currency_code
FROM mst.payment_data
WHERE payment_event.invoice_id = payment_data.invoice_id
  AND payment_event.payment_id = payment_data.payment_id;

ALTER TABLE mst.payment_event
  ALTER COLUMN payment_amount SET NOT NULL;
ALTER TABLE mst.payment_event
  ALTER COLUMN payment_currency_code SET NOT NULL;

ALTER TABLE mst.payment_data rename COLUMN payment_amount TO payment_origin_amount;
