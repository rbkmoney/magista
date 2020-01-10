/** Add new indexes for the invoices table */
CREATE INDEX IF NOT EXISTS invoice_data_status_by_created_date_idx
    ON mst.invoice_data USING BTREE (party_id, invoice_status, invoice_created_at);

CREATE INDEX IF NOT EXISTS invoice_event_invoice_amount_event_created_at_idx
    ON mst.invoice_data USING BTREE (party_id, invoice_amount, invoice_created_at);
