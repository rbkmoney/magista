CREATE TYPE INVOICE_EVENT_CATEGORY AS ENUM ('INVOICE', 'PAYMENT');

CREATE TYPE INVOICE_EVENT_TYPE AS ENUM ('INVOICE_CREATED', 'INVOICE_STATUS_CHANGED',
  'INVOICE_PAYMENT_STARTED', 'INVOICE_PAYMENT_STATUS_CHANGED');

CREATE TYPE INVOICE_PAYMENT_STATUS AS ENUM ('pending', 'processed',
  'captured', 'cancelled', 'failed');

CREATE TYPE INVOICE_STATUS AS ENUM ('unpaid', 'paid',
  'cancelled', 'fulfilled');


CREATE TABLE mst.invoice_event (
  event_id              BIGINT                      NOT NULL,
  event_category        INVOICE_EVENT_CATEGORY      NOT NULL,
  event_type            INVOICE_EVENT_TYPE          NOT NULL,
  event_created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  merchant_id           CHARACTER VARYING           NOT NULL,
  shop_id               INT                         NOT NULL,
  invoice_id            CHARACTER VARYING           NOT NULL,
  invoice_status        INVOICE_STATUS              NOT NULL,
  invoice_product       CHARACTER VARYING           NOT NULL,
  invoice_description   CHARACTER VARYING,
  invoice_amount        BIGINT                      NOT NULL,
  invoice_currency_code CHARACTER VARYING           NOT NULL,
  invoice_due           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  invoice_created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  payment_id            CHARACTER VARYING,
  payment_status        INVOICE_PAYMENT_STATUS,
  payment_amount        BIGINT,
  payment_fee           BIGINT,
  payment_tool          CHARACTER VARYING,
  payment_masked_pan    CHARACTER VARYING,
  payment_bin           CHARACTER VARYING,
  payment_system        CHARACTER VARYING,
  payment_session_id    CHARACTER VARYING,
  payment_country_id    INT,
  payment_city_id       INT,
  payment_ip            CHARACTER VARYING,
  payment_phone_number  CHARACTER VARYING,
  payment_email         CHARACTER VARYING,
  payment_fingerprint   CHARACTER VARYING,
  payment_created_at    TIMESTAMP WITHOUT TIME ZONE,
  CONSTRAINT invoice_event_pkey PRIMARY KEY (event_id)
);