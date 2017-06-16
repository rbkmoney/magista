-- commissions
ALTER TABLE mst.invoice_event_stat ADD COLUMN payment_provider_fee BIGINT;
ALTER TABLE mst.invoice_event_stat ADD COLUMN payment_external_fee BIGINT;