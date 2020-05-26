ALTER TABLE invoice_data ADD external_id CHARACTER VARYING;
ALTER TABLE payment_data ADD external_id CHARACTER VARYING;
ALTER TABLE refund_data ADD external_id CHARACTER VARYING;
ALTER TABLE chargeback_data ADD external_id CHARACTER VARYING;

CREATE INDEX IF NOT EXISTS invoice_data_external_id_idx ON mst.invoice_data(external_id) WHERE external_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS payment_data_external_id_idx ON mst.payment_data(external_id) WHERE external_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS refund_data_external_id_idx ON mst.refund_data(external_id) WHERE external_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS chargeback_data_external_id_idx ON mst.chargeback_data(external_id) WHERE external_id IS NOT NULL;