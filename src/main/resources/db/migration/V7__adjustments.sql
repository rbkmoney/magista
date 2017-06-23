-- add values in enum (https://github.com/flyway/flyway/issues/350)
-- rename the old enum
alter type mst.INVOICE_EVENT_TYPE rename to OLD_INVOICE_EVENT_TYPE;
-- create the new enum
create type mst.INVOICE_EVENT_TYPE as enum ('INVOICE_CREATED', 'INVOICE_STATUS_CHANGED',
  'INVOICE_PAYMENT_STARTED', 'INVOICE_PAYMENT_STATUS_CHANGED', 'INVOICE_PAYMENT_ADJUSTMENT_CREATED',
  'INVOICE_PAYMENT_ADJUSTMENT_STATUS_CHANGED');

alter table mst.invoice_event_stat
  alter column event_type type mst.INVOICE_EVENT_TYPE using event_type::text::mst.INVOICE_EVENT_TYPE;

-- drop the old enum
drop type mst.OLD_INVOICE_EVENT_TYPE;

-- adjustments
CREATE TYPE mst.ADJUSTMENT_STATUS AS ENUM ('pending', 'captured', 'cancelled');

ALTER TABLE mst.invoice_event_stat
  ADD COLUMN payment_adjustment_id CHARACTER VARYING;
ALTER TABLE mst.invoice_event_stat
  ADD COLUMN payment_adjustment_status mst.ADJUSTMENT_STATUS;
ALTER TABLE mst.invoice_event_stat
  ADD COLUMN payment_adjustment_status_created_at TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE mst.invoice_event_stat
  ADD COLUMN payment_adjustment_created_at TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE mst.invoice_event_stat
  ADD COLUMN payment_adjustment_reason CHARACTER VARYING;
ALTER TABLE mst.invoice_event_stat
  ADD COLUMN payment_adjustment_fee BIGINT;
ALTER TABLE mst.invoice_event_stat
  ADD COLUMN payment_adjustment_provider_fee BIGINT;
ALTER TABLE mst.invoice_event_stat
  ADD COLUMN payment_adjustment_external_fee BIGINT;
