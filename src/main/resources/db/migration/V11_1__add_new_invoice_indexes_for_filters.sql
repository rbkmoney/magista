/** Add new indexes for the invoices table */
CREATE INDEX CONCURRENTLY IF NOT EXISTS invoice_data_status_by_created_date_idx
    ON mst.invoice_data USING BTREE (party_id, invoice_created_at, invoice_status);

CREATE INDEX CONCURRENTLY IF NOT EXISTS invoice_event_invoice_amount_event_created_at_idx
    ON mst.invoice_data USING BTREE (party_id, invoice_amount, event_created_at);

CREATE INDEX CONCURRENTLY IF NOT EXISTS invoice_event_invoice_id_event_created_at_idx
    ON mst.invoice_data USING BTREE (invoice_id, event_created_at);
