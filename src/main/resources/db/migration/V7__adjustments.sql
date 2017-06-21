CREATE TYPE mst.ADJUSTMENT_STATUS AS ENUM ('pending', 'captured', 'cancelled');

-- adjustments
ALTER TABLE mst.invoice_event_stat
  ADD COLUMN payment_adjustment_id CHARACTER VARYING;
ALTER TABLE mst.invoice_event_stat
  ADD COLUMN payment_adjustment_status mst.ADJUSTMENT_STATUS;
ALTER TABLE mst.invoice_event_stat
  ADD COLUMN payment_adjustment_status_at TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE mst.invoice_event_stat
  ADD COLUMN payment_adjustment_created_at TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE mst.invoice_event_stat
  ADD COLUMN payment_adjustment_reason CHARACTER VARYING;
ALTER TABLE mst.invoice_event_stat
  ADD COLUMN payment_adjustment_amount BIGINT;
ALTER TABLE mst.invoice_event_stat
  ADD COLUMN payment_adjustment_fee BIGINT;
ALTER TABLE mst.invoice_event_stat
  ADD COLUMN payment_adjustment_provider_fee BIGINT;
ALTER TABLE mst.invoice_event_stat
  ADD COLUMN payment_adjustment_external_fee BIGINT;