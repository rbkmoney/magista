CREATE TABLE mst.refund (
  id                             BIGSERIAL                         NOT NULL,
  event_id                       BIGINT                            NOT NULL,
  event_created_at               TIMESTAMP WITHOUT TIME ZONE       NOT NULL,
  event_type                     mst.INVOICE_EVENT_TYPE            NOT NULL,
  invoice_id                     CHARACTER VARYING                 NOT NULL,
  payment_id                     CHARACTER VARYING                 NOT NULL,
  refund_id                      CHARACTER VARYING                 NOT NULL,
  party_id                       CHARACTER VARYING                 NOT NULL,
  party_shop_id                  CHARACTER VARYING                 NOT NULL,
  refund_status                  mst.INVOICE_PAYMENT_REFUND_STATUS NOT NULL,
  refund_operation_failure_class CHARACTER VARYING,
  refund_external_failure        CHARACTER VARYING,
  refund_external_failure_reason CHARACTER VARYING,
  refund_created_at              TIMESTAMP WITHOUT TIME ZONE       NOT NULL,
  refund_reason                  CHARACTER VARYING,
  refund_currency_code           CHARACTER VARYING                 NOT NULL,
  refund_amount                  BIGINT                            NOT NULL,
  refund_fee                     BIGINT DEFAULT 0                  NOT NULL,
  refund_provider_fee            BIGINT DEFAULT 0                  NOT NULL,
  refund_external_fee            BIGINT DEFAULT 0                  NOT NULL,
  CONSTRAINT refund_pkey PRIMARY KEY (id)
);

INSERT INTO mst.refund
(event_id, event_created_at, event_type, invoice_id, payment_id, refund_id, party_id, party_shop_id, refund_status, refund_created_at, refund_reason, refund_currency_code, refund_amount, refund_fee, refund_provider_fee, refund_external_fee)
  SELECT
    event_id,
    event_created_at,
    event_type,
    invoice_id,
    payment_id,
    payment_refund_id,
    party_id,
    party_shop_id,
    payment_refund_status,
    payment_refund_created_at,
    payment_refund_reason,
    payment_refund_currency_code,
    payment_refund_amount,
    COALESCE(payment_refund_fee, 0),
    COALESCE(payment_refund_provider_fee, 0),
    COALESCE(payment_refund_external_fee, 0)
  FROM
    mst.invoice_event_stat
  WHERE
    event_category
    =
    'REFUND'
  ORDER BY
    event_id;

DELETE FROM mst.invoice_event_stat
WHERE event_category = 'REFUND';

UPDATE mst.refund
SET event_type = 'INVOICE_PAYMENT_REFUND_STATUS_CHANGED'
WHERE event_type = 'INVOICE_PAYMENT_REFUND_CREATED' AND refund_status IN ('succeeded', 'failed');

ALTER TABLE mst.invoice_event_stat
  DROP COLUMN payment_refund_id;
ALTER TABLE mst.invoice_event_stat
  DROP COLUMN payment_refund_status;
ALTER TABLE mst.invoice_event_stat
  DROP COLUMN payment_refund_created_at;
ALTER TABLE mst.invoice_event_stat
  DROP COLUMN payment_refund_reason;
ALTER TABLE mst.invoice_event_stat
  DROP COLUMN payment_refund_currency_code;
ALTER TABLE mst.invoice_event_stat
  DROP COLUMN payment_refund_amount;
ALTER TABLE mst.invoice_event_stat
  DROP COLUMN payment_refund_fee;
ALTER TABLE mst.invoice_event_stat
  DROP COLUMN payment_refund_provider_fee;
ALTER TABLE mst.invoice_event_stat
  DROP COLUMN payment_refund_external_fee;

ALTER TYPE mst.invoice_payment_refund_status RENAME TO refund_status;

