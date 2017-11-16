ALTER TABLE mst.invoice_event_stat
  ADD COLUMN invoice_context_type CHARACTER VARYING;
ALTER TABLE mst.invoice_event_stat
  ADD COLUMN payment_context_type CHARACTER VARYING;

UPDATE mst.invoice_event_stat SET invoice_context_type = 'application/json' WHERE event_category = 'INVOICE'::mst.INVOICE_EVENT_CATEGORY AND invoice_context IS NOT NULL;
UPDATE mst.invoice_event_stat SET payment_context_type = 'application/json' WHERE event_category = 'PAYMENT'::mst.INVOICE_EVENT_CATEGORY AND payment_context IS NOT NULL;

