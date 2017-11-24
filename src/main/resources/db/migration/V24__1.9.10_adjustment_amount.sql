ALTER TABLE mst.invoice_event_stat
  ADD COLUMN payment_adjustment_amount BIGINT;

UPDATE mst.invoice_event_stat SET payment_adjustment_amount = invoice_amount WHERE payment_adjustment_id IS NOT NULL;