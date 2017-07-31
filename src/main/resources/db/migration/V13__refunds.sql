-- add values in enum (https://github.com/flyway/flyway/issues/350)
-- rename the old enum
alter type mst.INVOICE_EVENT_TYPE rename to OLD_INVOICE_EVENT_TYPE;
-- create the new enum
create type mst.INVOICE_EVENT_TYPE as enum ('INVOICE_CREATED', 'INVOICE_STATUS_CHANGED',
  'INVOICE_PAYMENT_STARTED', 'INVOICE_PAYMENT_STATUS_CHANGED', 'INVOICE_PAYMENT_ADJUSTMENT_CREATED',
  'INVOICE_PAYMENT_ADJUSTMENT_STATUS_CHANGED', 'INVOICE_PAYMENT_REFUND_CREATED', 'INVOICE_PAYMENT_REFUND_STATUS_CHANGED');

alter table mst.invoice_event_stat
  alter column event_type type mst.INVOICE_EVENT_TYPE using event_type::text::mst.INVOICE_EVENT_TYPE;

-- drop the old enum
drop type mst.OLD_INVOICE_EVENT_TYPE;

CREATE TYPE mst.INVOICE_PAYMENT_REFUND_STATUS AS ENUM ('pending', 'succeeded',
  'failed');

ALTER TABLE mst.invoice_event_stat
  ADD COLUMN payment_refund_id CHARACTER VARYING;
ALTER TABLE mst.invoice_event_stat
  ADD COLUMN payment_refund_status mst.INVOICE_PAYMENT_REFUND_STATUS;
ALTER TABLE mst.invoice_event_stat
  ADD COLUMN payment_refund_created_at TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE mst.invoice_event_stat
  ADD COLUMN payment_refund_reason CHARACTER VARYING;
ALTER TABLE mst.invoice_event_stat ADD COLUMN payment_refund_fee BIGINT;
ALTER TABLE mst.invoice_event_stat ADD COLUMN payment_refund_provider_fee BIGINT;
ALTER TABLE mst.invoice_event_stat ADD COLUMN payment_refund_external_fee BIGINT;