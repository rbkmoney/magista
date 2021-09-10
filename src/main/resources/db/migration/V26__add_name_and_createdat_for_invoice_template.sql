ALTER TABLE mst.invoice_template
    ADD COLUMN name CHARACTER VARYING;
ALTER TABLE mst.invoice_template
    ADD COLUMN invoice_template_created_at TIMESTAMP WITHOUT TIME ZONE;
