ALTER TABLE mst.payment_event
  ALTER COLUMN payment_amount SET NOT NULL;
ALTER TABLE mst.payment_event
  ALTER COLUMN payment_currency_code SET NOT NULL;