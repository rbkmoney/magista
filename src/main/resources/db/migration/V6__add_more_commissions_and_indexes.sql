-- commissions
ALTER TABLE mst.invoice_event_stat ADD COLUMN payment_provider_fee BIGINT;
ALTER TABLE mst.invoice_event_stat ADD COLUMN payment_external_fee BIGINT;

-- indexes
create index event_invoice_ms_key on mst.invoice_event_stat (invoice_id, payment_id);
create index event_party_ms_key on mst.invoice_event_stat (party_id, party_shop_id);
