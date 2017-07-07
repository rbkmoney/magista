CREATE TYPE mst.INVOICE_EVENT_CATEGORY AS ENUM ('INVOICE', 'PAYMENT');

CREATE TYPE mst.INVOICE_EVENT_TYPE AS ENUM ('INVOICE_CREATED', 'INVOICE_STATUS_CHANGED',
  'INVOICE_PAYMENT_STARTED', 'INVOICE_PAYMENT_STATUS_CHANGED', 'INVOICE_PAYMENT_ADJUSTMENT_CREATED',
  'INVOICE_PAYMENT_ADJUSTMENT_STATUS_CHANGED');

CREATE TYPE mst.INVOICE_PAYMENT_STATUS AS ENUM ('pending', 'processed',
  'captured', 'cancelled', 'failed');

CREATE TYPE mst.INVOICE_STATUS AS ENUM ('unpaid', 'paid',
  'cancelled', 'fulfilled');

-- adjustments
CREATE TYPE mst.ADJUSTMENT_STATUS AS ENUM ('pending', 'captured', 'cancelled');


CREATE TABLE mst.invoice_event_stat (
  id                                   BIGSERIAL                   NOT NULL,
  event_id                             BIGINT                      NOT NULL,
  event_category                       mst.INVOICE_EVENT_CATEGORY  NOT NULL,
  event_type                           mst.INVOICE_EVENT_TYPE      NOT NULL,
  event_created_at                     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  party_id                             CHARACTER VARYING           NOT NULL,
  party_email                          CHARACTER VARYING,
  party_shop_id                        CHARACTER VARYING           NOT NULL,
  party_shop_name                      CHARACTER VARYING,
  party_shop_description               CHARACTER VARYING,
  party_shop_url                       CHARACTER VARYING,
  party_shop_category_id               INT,
  party_shop_payout_tool_id            INT,
  party_contract_id                    INT,
  party_contract_registered_number     CHARACTER VARYING,
  party_contract_inn                   CHARACTER VARYING,
  invoice_id                           CHARACTER VARYING           NOT NULL,
  invoice_status                       mst.INVOICE_STATUS          NOT NULL,
  invoice_status_details               CHARACTER VARYING,
  invoice_product                      CHARACTER VARYING           NOT NULL,
  invoice_description                  CHARACTER VARYING,
  invoice_amount                       BIGINT                      NOT NULL,
  invoice_currency_code                CHARACTER VARYING           NOT NULL,
  invoice_due                          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  invoice_created_at                   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  invoice_context                      BYTEA,
  payment_id                           CHARACTER VARYING,
  payment_status                       mst.INVOICE_PAYMENT_STATUS,
  payment_status_failure_code          CHARACTER VARYING,
  payment_status_failure_description   CHARACTER VARYING,
  payment_amount                       BIGINT,
  payment_currency_code                CHARACTER VARYING,
  payment_fee                          BIGINT,
  payment_provider_fee                 BIGINT,
  payment_external_fee                 BIGINT,
  payment_tool                         CHARACTER VARYING,
  payment_masked_pan                   CHARACTER VARYING,
  payment_bin                          CHARACTER VARYING,
  payment_token                        CHARACTER VARYING,
  payment_system                       CHARACTER VARYING,
  payment_session_id                   CHARACTER VARYING,
  payment_country_id                   INT,
  payment_city_id                      INT,
  payment_ip                           CHARACTER VARYING,
  payment_phone_number                 CHARACTER VARYING,
  payment_email                        CHARACTER VARYING,
  payment_fingerprint                  CHARACTER VARYING,
  payment_created_at                   TIMESTAMP WITHOUT TIME ZONE,
  payment_context                      BYTEA,
  payment_adjustment_id                CHARACTER VARYING,
  payment_adjustment_status            mst.ADJUSTMENT_STATUS,
  payment_adjustment_status_created_at TIMESTAMP WITHOUT TIME ZONE,
  payment_adjustment_created_at        TIMESTAMP WITHOUT TIME ZONE,
  payment_adjustment_reason            CHARACTER VARYING,
  payment_adjustment_fee               BIGINT,
  payment_adjustment_provider_fee      BIGINT,
  payment_adjustment_external_fee      BIGINT,
  CONSTRAINT invoice_event_pkey PRIMARY KEY (id)
);

-- indexes
CREATE INDEX event_invoice_ms_key
  ON mst.invoice_event_stat
  USING btree
  (invoice_id, payment_id);
CREATE INDEX event_party_ms_key
  ON mst.invoice_event_stat
  USING btree
  (party_id, party_shop_id);
