CREATE TABLE mst.adjustment (
  id                           BIGSERIAL                         NOT NULL,
  event_id                     BIGINT                            NOT NULL,
  event_created_at             TIMESTAMP WITHOUT TIME ZONE       NOT NULL,
  event_type                   mst.INVOICE_EVENT_TYPE            NOT NULL,
  invoice_id                   CHARACTER VARYING                 NOT NULL,
  payment_id                   CHARACTER VARYING                 NOT NULL,
  adjustment_id                CHARACTER VARYING                 NOT NULL,
  party_id                     CHARACTER VARYING                 NOT NULL,
  party_shop_id                CHARACTER VARYING                 NOT NULL,
  party_contract_id            CHARACTER VARYING                 NOT NULL,
  adjustment_status            mst.ADJUSTMENT_STATUS             NOT NULL,
  adjustment_status_created_at TIMESTAMP WITHOUT TIME ZONE,
  adjustment_created_at        TIMESTAMP WITHOUT TIME ZONE       NOT NULL,
  adjustment_reason            CHARACTER VARYING                 NOT NULL,
  adjustment_fee               BIGINT DEFAULT 0                  NOT NULL,
  adjustment_provider_fee      BIGINT DEFAULT 0                  NOT NULL,
  adjustment_external_fee      BIGINT DEFAULT 0                  NOT NULL,
  CONSTRAINT adjustment_pkey PRIMARY KEY (id)
);

INSERT INTO mst.adjustment
(event_id, event_created_at, event_type, invoice_id, payment_id, adjustment_id, party_id, party_shop_id, party_contract_id, adjustment_status, adjustment_status_created_at, adjustment_created_at, adjustment_reason, adjustment_fee, adjustment_provider_fee, adjustment_external_fee)
  SELECT
    event_id,
    event_created_at,
    event_type,
    invoice_id,
    payment_id,
    payment_adjustment_id,
    party_id,
    party_shop_id,
    party_contract_id,
    payment_adjustment_status,
    payment_adjustment_status_created_at,
    payment_adjustment_created_at,
    payment_adjustment_reason,
    COALESCE(payment_adjustment_fee, 0),
    COALESCE(payment_adjustment_provider_fee, 0),
    COALESCE(payment_adjustment_external_fee, 0)
  FROM
    mst.invoice_event_stat
  WHERE
    event_category
    =
    'ADJUSTMENT'
  ORDER BY
    event_id;

DELETE FROM mst.invoice_event_stat
WHERE event_category = 'ADJUSTMENT';

ALTER TABLE mst.invoice_event_stat
  DROP COLUMN payment_adjustment_id;
ALTER TABLE mst.invoice_event_stat
  DROP COLUMN payment_adjustment_status;
ALTER TABLE mst.invoice_event_stat
  DROP COLUMN payment_adjustment_status_created_at;
ALTER TABLE mst.invoice_event_stat
  DROP COLUMN payment_adjustment_created_at;
ALTER TABLE mst.invoice_event_stat
  DROP COLUMN payment_adjustment_reason;
ALTER TABLE mst.invoice_event_stat
  DROP COLUMN payment_adjustment_fee;
ALTER TABLE mst.invoice_event_stat
  DROP COLUMN payment_adjustment_provider_fee;
ALTER TABLE mst.invoice_event_stat
  DROP COLUMN payment_adjustment_external_fee;