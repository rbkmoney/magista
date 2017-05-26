CREATE TYPE INVOICE_EVENT_CATEGORY AS ENUM ('INVOICE', 'PAYMENT');

CREATE TYPE INVOICE_EVENT_TYPE AS ENUM ('INVOICE_CREATED', 'INVOICE_STATUS_CHANGED',
  'INVOICE_PAYMENT_STARTED', 'INVOICE_PAYMENT_STATUS_CHANGED');

CREATE TYPE INVOICE_PAYMENT_STATUS AS ENUM ('pending', 'processed',
  'captured', 'cancelled', 'failed');

CREATE TYPE INVOICE_STATUS AS ENUM ('unpaid', 'paid',
  'cancelled', 'fulfilled');


CREATE TABLE mst.invoice_event_stat (
  event_id                           BIGINT                      NOT NULL,
  event_category                     INVOICE_EVENT_CATEGORY      NOT NULL,
  event_type                         INVOICE_EVENT_TYPE          NOT NULL,
  event_created_at                   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  party_id                           CHARACTER VARYING           NOT NULL,
  party_email                        CHARACTER VARYING           NOT NULL,
  party_shop_id                      INT                         NOT NULL,
  party_shop_name                    CHARACTER VARYING           NOT NULL,
  party_shop_description             CHARACTER VARYING,
  party_shop_url                     CHARACTER VARYING,
  party_shop_category_id             INT                         NOT NULL,
  party_shop_payout_tool_id          INT,
  party_contract_id                  INT                         NOT NULL,
  party_contract_registered_number   CHARACTER VARYING           NOT NULL,
  party_contract_inn                 CHARACTER VARYING           NOT NULL,
  invoice_id                         CHARACTER VARYING           NOT NULL,
  invoice_status                     INVOICE_STATUS              NOT NULL,
  invoice_status_details             CHARACTER VARYING,
  invoice_product                    CHARACTER VARYING           NOT NULL,
  invoice_description                CHARACTER VARYING,
  invoice_amount                     BIGINT                      NOT NULL,
  invoice_currency_code              CHARACTER VARYING           NOT NULL,
  invoice_due                        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  invoice_created_at                 TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  invoice_context                    CHARACTER VARYING,
  payment_id                         CHARACTER VARYING,
  payment_status                     INVOICE_PAYMENT_STATUS,
  payment_status_failure_code        CHARACTER VARYING,
  payment_status_failure_description CHARACTER VARYING,
  payment_amount                     BIGINT,
  payment_currency_code              CHARACTER VARYING,
  payment_fee                        BIGINT,
  payment_tool                       CHARACTER VARYING,
  payment_masked_pan                 CHARACTER VARYING,
  payment_bin                        CHARACTER VARYING,
  payment_system                     CHARACTER VARYING,
  payment_session_id                 CHARACTER VARYING,
  payment_country_id                 INT,
  payment_city_id                    INT,
  payment_ip                         CHARACTER VARYING,
  payment_phone_number               CHARACTER VARYING,
  payment_email                      CHARACTER VARYING,
  payment_fingerprint                CHARACTER VARYING,
  payment_created_at                 TIMESTAMP WITHOUT TIME ZONE,
  payment_context                    CHARACTER VARYING,
  CONSTRAINT invoice_event_pkey PRIMARY KEY (event_id)
);