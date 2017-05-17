CREATE SCHEMA IF NOT EXISTS mst;

CREATE TABLE mst.invoice_event (
  event_id              BIGINT            NOT NULL,
  merchant_id           CHARACTER VARYING NOT NULL,
  shop_id               INT               NOT NULL,
  invoice_id            CHARACTER VARYING NOT NULL,
  event_type            CHARACTER VARYING NOT NULL,
  event_created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  invoice_status        CHARACTER VARYING NOT NULL,
  invoice_amount        BIGINT,
  invoice_currency_code CHARACTER VARYING,
  invoice_created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  payment_id            CHARACTER VARYING,
  payment_status        CHARACTER VARYING,
  payment_amount        BIGINT,
  payment_fee           BIGINT,
  payment_system        CHARACTER VARYING,
  payment_country_id    INT,
  payment_city_id       INT,
  payment_ip            CHARACTER VARYING,
  payment_phone_number  CHARACTER VARYING,
  payment_masked_pan    CHARACTER VARYING,
  payment_email         CHARACTER VARYING,
  payment_fingerprint   CHARACTER VARYING,
  payment_created_at    TIMESTAMP WITHOUT TIME ZONE,
  CONSTRAINT invoice_event_pkey PRIMARY KEY (event_id)
);

CREATE TABLE mst.invoice (
  event_id      BIGINT                      NOT NULL,
  invoice_id    CHARACTER VARYING           NOT NULL,
  merchant_id   CHARACTER VARYING           NOT NULL,
  shop_id       BIGINT                      NOT NULL,
  status        CHARACTER VARYING           NOT NULL,
  amount        BIGINT                      NOT NULL,
  currency_code CHARACTER VARYING           NOT NULL,
  created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  changed_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  model         BYTEA                       NOT NULL,
  CONSTRAINT invoice_pkey PRIMARY KEY (event_id)
);

CREATE INDEX invoice_cms_key
  ON mst.invoice (created_at, merchant_id, shop_id);

CREATE TABLE mst.payment (
  event_id       BIGINT                      NOT NULL,
  payment_id     CHARACTER VARYING           NOT NULL,
  invoice_id     CHARACTER VARYING           NOT NULL,
  merchant_id    CHARACTER VARYING           NOT NULL,
  shop_id        INT                         NOT NULL,
  fingerprint    CHARACTER VARYING,
  masked_pan     CHARACTER VARYING           NOT NULL,
  status         CHARACTER VARYING           NOT NULL,
  amount         BIGINT                      NOT NULL,
  fee            BIGINT                      NOT NULL,
  currency_code  CHARACTER VARYING           NOT NULL,
  payment_system CHARACTER VARYING           NOT NULL,
  country_id     INT                         NOT NULL,
  city_id        INT                         NOT NULL,
  ip             CHARACTER VARYING,
  phone_number   CHARACTER VARYING,
  email          CHARACTER VARYING,
  created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  changed_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  model          BYTEA                       NOT NULL,
  CONSTRAINT payment_pkey PRIMARY KEY (event_id)
);

CREATE INDEX payment_ms_key
  ON mst.payment (merchant_id, shop_id);