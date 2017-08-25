ALTER TABLE mst.invoice_event_stat ADD COLUMN payment_flow CHARACTER VARYING;
ALTER TABLE mst.invoice_event_stat ADD COLUMN payment_hold_on_expiration CHARACTER VARYING;
ALTER TABLE mst.invoice_event_stat ADD COLUMN payment_hold_until TIMESTAMP WITHOUT TIME ZONE;

UPDATE mst.invoice_event_stat SET payment_flow = 'instant' WHERE payment_flow ISNULL;
