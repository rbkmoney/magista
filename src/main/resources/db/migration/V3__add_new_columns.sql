-- Invoice
ALTER TABLE mst.invoice ADD COLUMN product character varying not null;
ALTER TABLE mst.invoice ADD COLUMN description character varying;
ALTER TABLE mst.invoice ADD COLUMN status_details character varying;
ALTER TABLE mst.invoice ADD COLUMN due timestamp without time zone not null;
ALTER TABLE mst.invoice ADD COLUMN context BYTEA;
ALTER TABLE mst.invoice DROP COLUMN data;
ALTER TABLE mst.invoice DROP COLUMN model;

-- Payment
ALTER TABLE mst.payment ADD COLUMN token character varying not null;
ALTER TABLE mst.payment ADD COLUMN session_id character varying not null;
ALTER TABLE mst.payment ADD COLUMN bin character varying not null;
ALTER TABLE mst.payment ADD COLUMN payment_tool character varying not null default 'bank_card';
ALTER TABLE mst.payment ADD COLUMN failure_code character varying;
ALTER TABLE mst.payment ADD COLUMN failure_description character varying;
ALTER TABLE mst.payment ADD COLUMN context BYTEA;
ALTER TABLE mst.payment DROP COLUMN data;
ALTER TABLE mst.payment DROP COLUMN model;
