ALTER TABLE mst.payment_event
  ADD COLUMN payment_provider_id INT;
ALTER TABLE mst.payment_event
  ADD COLUMN payment_terminal_id INT;

UPDATE mst.payment_event
SET payment_provider_id = payment_data.payment_provider_id,
  payment_terminal_id   = payment_data.payment_terminal_id
FROM mst.payment_data
WHERE payment_event.invoice_id = payment_data.invoice_id
      AND payment_event.payment_id = payment_data.payment_id;

ALTER TABLE mst.payment_data
  DROP COLUMN payment_provider_id;
ALTER TABLE mst.payment_data
  DROP COLUMN payment_terminal_id;
