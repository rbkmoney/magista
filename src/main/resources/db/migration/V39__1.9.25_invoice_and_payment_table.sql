ALTER INDEX payment_event_pkey
RENAME TO payout_event_pkey;
ALTER INDEX invoice_event_pkey
RENAME TO invoice_event_stat_pkey;

CREATE TYPE mst.PAYMENT_TOOL AS ENUM ('bank_card', 'payment_terminal', 'digital_wallet');
CREATE TYPE mst.PAYMENT_FLOW AS ENUM ('instant', 'hold');
CREATE TYPE mst.ON_HOLD_EXPIRATION AS ENUM ('cancel', 'capture');

create table mst.invoice_data (
  id                     BIGSERIAL                   NOT NULL,
  party_id               UUID                        NOT NULL,
  party_shop_id          CHARACTER VARYING           NOT NULL,
  party_contract_id      CHARACTER VARYING           NOT NULL,
  invoice_id             CHARACTER VARYING           NOT NULL,
  invoice_product        CHARACTER VARYING           NOT NULL,
  invoice_description    CHARACTER VARYING,
  invoice_amount         BIGINT                      NOT NULL,
  invoice_currency_code  CHARACTER VARYING           NOT NULL,
  invoice_due            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  invoice_created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  invoice_party_revision BIGINT,
  invoice_template_id    CHARACTER VARYING,
  invoice_cart_json      JSONB,
  invoice_context_type   CHARACTER VARYING,
  invoice_context        bytea,
  CONSTRAINT invoice_data_pkey PRIMARY KEY (invoice_id)
);

CREATE INDEX invoice_data_invoice_created_at_idx
  ON mst.invoice_data
  using btree (invoice_created_at);

create table invoice_event (
  id                     BIGSERIAL                   NOT NULL,
  event_id               BIGINT                      NOT NULL,
  event_created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  event_type             mst.INVOICE_EVENT_TYPE      NOT NULL,
  invoice_id             CHARACTER VARYING           NOT NULL REFERENCES mst.invoice_data (invoice_id),
  invoice_status         mst.INVOICE_STATUS          NOT NULL,
  invoice_status_details CHARACTER VARYING,
  CONSTRAINT invoice_event_pkey PRIMARY KEY (id)
);

CREATE INDEX invoice_event_invoice_id_event_created_at_idx
  ON mst.invoice_event
  using btree (invoice_id, event_created_at);

insert into mst.invoice_data (
  party_id,
  party_shop_id,
  party_contract_id,
  invoice_id,
  invoice_product,
  invoice_description,
  invoice_amount,
  invoice_currency_code,
  invoice_due,
  invoice_created_at,
  invoice_template_id,
  invoice_cart_json,
  invoice_context_type,
  invoice_context
) SELECT
    party_id :: uuid,
    party_shop_id,
    party_contract_id,
    invoice_id,
    invoice_product,
    invoice_description,
    invoice_amount,
    invoice_currency_code,
    invoice_due,
    invoice_created_at,
    invoice_template_id,
    invoice_cart :: jsonb,
    invoice_context_type,
    invoice_context
  FROM mst.invoice_event_stat
  WHERE event_category = 'INVOICE'
        AND event_type = 'INVOICE_CREATED'
  ORDER BY id;

insert into mst.invoice_event (
  event_id,
  event_created_at,
  event_type,
  invoice_id,
  invoice_status,
  invoice_status_details
) SELECT
    event_id,
    event_created_at,
    event_type,
    invoice_id,
    invoice_status,
    invoice_status_details
  FROM mst.invoice_event_stat
  WHERE event_category = 'INVOICE'
  ORDER BY id;

create table mst.payment_data (
  id                               BIGSERIAL                   NOT NULL,
  invoice_id                       CHARACTER VARYING           NOT NULL  REFERENCES mst.invoice_data (invoice_id),
  payment_id                       CHARACTER VARYING           NOT NULL,
  party_id                         UUID                        NOT NULL,
  party_shop_id                    CHARACTER VARYING           NOT NULL,
  party_contract_id                CHARACTER VARYING           NOT NULL,
  payment_currency_code            CHARACTER VARYING           NOT NULL,
  payment_amount                   BIGINT                      NOT NULL,
  payment_customer_id              CHARACTER VARYING,
  payment_tool                     mst.PAYMENT_TOOL            NOT NULL,
  payment_bank_card_masked_pan     CHARACTER VARYING,
  payment_bank_card_bin            CHARACTER VARYING,
  payment_bank_card_token          CHARACTER VARYING,
  payment_bank_card_system         CHARACTER VARYING,
  payment_bank_card_token_provider mst.bank_card_token_provider,
  payment_terminal_provider        CHARACTER VARYING,
  payment_digital_wallet_id        CHARACTER VARYING,
  payment_digital_wallet_provider  CHARACTER VARYING,
  payment_provider_id              INTEGER                     NOT NULL,
  payment_terminal_id              INTEGER                     NOT NULL,
  payment_flow                     mst.PAYMENT_FLOW            NOT NULL,
  payment_hold_on_expiration       mst.ON_HOLD_EXPIRATION,
  payment_hold_until               TIMESTAMP WITHOUT TIME ZONE,
  payment_session_id               CHARACTER VARYING,
  payment_fingerprint              CHARACTER VARYING,
  payment_ip                       CHARACTER VARYING,
  payment_phone_number             CHARACTER VARYING,
  payment_email                    CHARACTER VARYING,
  payment_created_at               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  payment_party_revision           BIGINT,
  payment_context_type             CHARACTER VARYING,
  payment_context                  bytea,
  CONSTRAINT payment_data_pkey PRIMARY KEY (invoice_id, payment_id)
);

CREATE INDEX payment_data_payment_created_at_idx
  ON mst.payment_data
  using btree (payment_created_at);

create table mst.payment_event (
  id                              BIGSERIAL                   NOT NULL,
  event_id                        BIGINT                      NOT NULL,
  event_created_at                TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  event_type                      mst.INVOICE_EVENT_TYPE      NOT NULL,
  invoice_id                      CHARACTER VARYING           NOT NULL,
  payment_id                      CHARACTER VARYING           NOT NULL,
  payment_status                  mst.INVOICE_PAYMENT_STATUS  NOT NULL,
  payment_operation_failure_class mst.FAILURE_CLASS,
  payment_external_failure        CHARACTER VARYING,
  payment_external_failure_reason CHARACTER VARYING,
  payment_fee                     BIGINT                      NOT NULL,
  payment_provider_fee            BIGINT                      NOT NULL,
  payment_external_fee            BIGINT                      NOT NULL,
  payment_domain_revision         BIGINT                      NOT NULL,
  CONSTRAINT payment_event_pkey PRIMARY KEY (id),
  FOREIGN KEY (invoice_id, payment_id) REFERENCES payment_data (invoice_id, payment_id)
);

CREATE INDEX payment_event_invoice_id_payment_id_event_created_at_idx
  ON mst.payment_event
  using btree (invoice_id, payment_id, event_created_at);

insert into mst.payment_data (
  party_id,
  party_shop_id,
  party_contract_id,
  invoice_id,
  payment_id,
  payment_currency_code,
  payment_amount,
  payment_customer_id,
  payment_tool,
  payment_bank_card_masked_pan,
  payment_bank_card_bin,
  payment_bank_card_token,
  payment_bank_card_system,
  payment_bank_card_token_provider,
  payment_terminal_provider,
  payment_digital_wallet_id,
  payment_digital_wallet_provider,
  payment_provider_id,
  payment_terminal_id,
  payment_flow,
  payment_hold_on_expiration,
  payment_hold_until,
  payment_session_id,
  payment_fingerprint,
  payment_ip,
  payment_phone_number,
  payment_email,
  payment_created_at,
  payment_party_revision,
  payment_context_type,
  payment_context
) SELECT
    party_id :: uuid,
    party_shop_id,
    party_contract_id,
    invoice_id,
    payment_id,
    payment_currency_code,
    payment_amount,
    payment_customer_id,
    payment_tool :: mst.PAYMENT_TOOL,
    payment_masked_pan,
    payment_bin,
    payment_token,
    payment_system,
    payment_bank_card_token_provider :: mst.bank_card_token_provider,
    payment_terminal_provider,
    payment_digital_wallet_id,
    payment_digital_wallet_provider,
    payment_provider_id,
    payment_terminal_id,
    payment_flow :: mst.PAYMENT_FLOW,
    payment_hold_on_expiration :: mst.ON_HOLD_EXPIRATION,
    payment_hold_until,
    payment_session_id,
    payment_fingerprint,
    payment_ip,
    payment_phone_number,
    payment_email,
    payment_created_at,
    payment_party_revision,
    payment_context_type,
    payment_context
  FROM mst.invoice_event_stat
  WHERE event_category = 'PAYMENT'
        AND event_type = 'INVOICE_PAYMENT_STARTED'
  ORDER BY id;

insert into mst.payment_event (
  event_id,
  event_created_at,
  event_type,
  invoice_id,
  payment_id,
  payment_status,
  payment_operation_failure_class,
  payment_external_failure,
  payment_external_failure_reason,
  payment_fee,
  payment_provider_fee,
  payment_external_fee,
  payment_domain_revision
) select
    event_id,
    event_created_at,
    event_type,
    invoice_id,
    payment_id,
    payment_status,
    payment_operation_failure_class,
    payment_external_failure,
    payment_external_failure_reason,
    coalesce(payment_fee, 0),
    coalesce(payment_provider_fee, 0),
    coalesce(payment_external_fee, 0),
    payment_domain_revision
  FROM mst.invoice_event_stat
  WHERE event_category = 'PAYMENT'
  ORDER BY id;


