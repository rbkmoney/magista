CREATE SCHEMA IF NOT EXISTS mst;

CREATE TYPE mst.INVOICE_EVENT_TYPE AS ENUM ('INVOICE_CREATED', 'INVOICE_STATUS_CHANGED');
CREATE TYPE mst.INVOICE_STATUS AS ENUM ('unpaid', 'paid', 'cancelled', 'fulfilled');

CREATE TABLE mst.invoice_event (
  id                     BIGSERIAL                   NOT NULL,
  event_id               BIGINT                      NOT NULL,
  event_type             mst.INVOICE_EVENT_TYPE      NOT NULL,
  event_created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  party_id               UUID                        NOT NULL,
  party_shop_id          CHARACTER VARYING           NOT NULL,
  invoice_id             CHARACTER VARYING           NOT NULL,
  invoice_status         mst.INVOICE_STATUS          NOT NULL,
  invoice_status_details CHARACTER VARYING,
  invoice_amount         BIGINT                      NOT NULL,
  invoice_currency_code  CHARACTER VARYING           NOT NULL,
  invoice_created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  invoice_due            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  invoice_template_id    CHARACTER VARYING,
  invoice_product        CHARACTER VARYING           NOT NULL,
  invoice_description    CHARACTER VARYING,
  invoice_cart           CHARACTER VARYING,
  invoice_party_revision BIGINT,
  invoice_context_type   CHARACTER VARYING,
  invoice_context        BYTEA,
  CONSTRAINT invoice_event_pkey PRIMARY KEY (id)
);

CREATE TYPE mst.PAYMENT_EVENT_TYPE AS ENUM ('PAYMENT_STARTED', 'PAYMENT_STATUS_CHANGED', 'PAYMENT_ADJUSTMENT_CAPTURED');
CREATE TYPE mst.PAYMENT_STATUS
AS ENUM ('pending', 'processed', 'captured', 'cancelled', 'failed', 'refunded');

CREATE TABLE mst.payment_event (
  id                                   BIGSERIAL                   NOT NULL,
  event_id                             BIGINT                      NOT NULL,
  event_type                           mst.PAYMENT_EVENT_TYPE      NOT NULL,
  event_created_at                     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  party_id                             UUID                        NOT NULL,
  party_shop_id                        CHARACTER VARYING           NOT NULL,
  invoice_id                           CHARACTER VARYING           NOT NULL,
  payment_id                           CHARACTER VARYING           NOT NULL,
  payment_status                       mst.PAYMENT_STATUS          NOT NULL,
  payment_failure_class                CHARACTER VARYING,
  payment_external_failure_code        CHARACTER VARYING,
  payment_external_failure_description CHARACTER VARYING,
  payment_amount                       BIGINT                      NOT NULL,
  payment_currency_code                CHARACTER VARYING           NOT NULL,
  payment_fee                          BIGINT                      NOT NULL,
  payment_provider_fee                 BIGINT                      NOT NULL,
  payment_external_fee                 BIGINT                      NOT NULL,
  payment_flow                         CHARACTER VARYING           NOT NULL,
  payment_hold_on_expiration           CHARACTER VARYING,
  payment_hold_until                   TIMESTAMP WITHOUT TIME ZONE,
  payment_provider_id                  INT                         NOT NULL,
  payment_terminal_id                  INT                         NOT NULL,
  payment_domain_revision              BIGINT,
  payment_tool                         CHARACTER VARYING           NOT NULL,
  payment_session_id                   CHARACTER VARYING,
  payment_phone_number                 CHARACTER VARYING,
  payment_email                        CHARACTER VARYING,
  payment_ip                           CHARACTER VARYING,
  payment_fingerprint                  CHARACTER VARYING,
  payment_bank_card_masked_pan         CHARACTER VARYING,
  payment_bank_card_token              CHARACTER VARYING,
  payment_bank_card_system             CHARACTER VARYING,
  payment_bank_card_bin                CHARACTER VARYING,
  payment_terminal_provider            CHARACTER VARYING,
  payment_digital_wallet_id            CHARACTER VARYING,
  payment_digital_wallet_provider      CHARACTER VARYING,
  payment_customer_id                  CHARACTER VARYING,
  payment_customer_binding_id          CHARACTER VARYING,
  payment_rec_payment_tool_id          CHARACTER VARYING,
  payment_party_revision               BIGINT,
  payment_created_at                   TIMESTAMP                   NOT NULL,
  payment_context_type                 CHARACTER VARYING,
  payment_context                      BYTEA,
  CONSTRAINT payment_event_pkey PRIMARY KEY (id)
);


CREATE TYPE mst.REFUND_EVENT_TYPE AS ENUM ('REFUND_CREATED', 'REFUND_STATUS_CHANGED');
CREATE TYPE mst.REFUND_STATUS AS ENUM ('pending', 'succeeded', 'failed');

CREATE TABLE mst.refund_event (
  id                     BIGSERIAL                   NOT NULL,
  event_id               BIGINT                      NOT NULL,
  event_type             mst.REFUND_EVENT_TYPE       NOT NULL,
  event_created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  party_id               UUID                        NOT NULL,
  party_shop_id          CHARACTER VARYING           NOT NULL,
  invoice_id             CHARACTER VARYING           NOT NULL,
  payment_id             CHARACTER VARYING           NOT NULL,
  refund_id              CHARACTER VARYING           NOT NULL,
  refund_status          mst.REFUND_STATUS           NOT NULL,
  refund_amount          BIGINT                      NOT NULL,
  refund_fee             BIGINT                      NOT NULL,
  refund_provider_fee    BIGINT                      NOT NULL,
  refund_external_fee    BIGINT                      NOT NULL,
  refund_created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  refund_reason          CHARACTER VARYING,
  refund_domain_revision BIGINT                      NOT NULL,
  CONSTRAINT refund_event_pkey PRIMARY KEY (id)
);

CREATE TYPE mst.ADJUSTMENT_EVENT_TYPE AS ENUM ('ADJUSTMENT_CREATED', 'ADJUSTMENT_STATUS_CHANGED');
CREATE TYPE mst.ADJUSTMENT_STATUS AS ENUM ('pending', 'captured', 'cancelled');

CREATE TABLE mst.adjustment_event (
  id                           BIGSERIAL                   NOT NULL,
  event_id                     BIGINT                      NOT NULL,
  event_type                   mst.ADJUSTMENT_EVENT_TYPE   NOT NULL,
  event_created_at             TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  party_id                     UUID                        NOT NULL,
  party_shop_id                CHARACTER VARYING           NOT NULL,
  invoice_id                   CHARACTER VARYING           NOT NULL,
  payment_id                   CHARACTER VARYING           NOT NULL,
  adjustment_id                CHARACTER VARYING           NOT NULL,
  adjustment_status            mst.ADJUSTMENT_STATUS       NOT NULL,
  adjustment_status_created_at TIMESTAMP WITHOUT TIME ZONE,
  adjustment_reason            CHARACTER VARYING           NOT NULL,
  adjustment_amount            BIGINT                      NOT NULL,
  adjustment_fee               BIGINT                      NOT NULL,
  adjustment_provider_fee      BIGINT                      NOT NULL,
  adjustment_external_fee      BIGINT                      NOT NULL,
  adjustment_created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  adjustment_domain_revision   BIGINT                      NOT NULL,
  CONSTRAINT adjustment_event_pkey PRIMARY KEY (id)
);

CREATE TYPE mst.PAYOUT_STATUS AS ENUM ('unpaid', 'paid', 'cancelled', 'confirmed');
CREATE TYPE mst.PAYOUT_TYPE AS ENUM ('bank_card', 'bank_account');
CREATE TYPE mst.PAYOUT_EVENT_TYPE AS ENUM ('PAYOUT_CREATED', 'PAYOUT_STATUS_CHANGED');

CREATE TABLE mst.payout_event
(
  id                                       BIGSERIAL                   NOT NULL,
  event_id                                 BIGINT                      NOT NULL,
  event_type                               mst.PAYOUT_EVENT_TYPE       NOT NULL,
  event_created_at                         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  party_id                                 CHARACTER VARYING           NOT NULL,
  party_shop_id                            CHARACTER VARYING           NOT NULL,
  payout_id                                CHARACTER VARYING           NOT NULL,
  payout_created_at                        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  payout_status                            mst.PAYOUT_STATUS           NOT NULL,
  payout_status_cancel_details             CHARACTER VARYING,
  payout_amount                            BIGINT                      NOT NULL,
  payout_fee                               BIGINT                      NOT NULL,
  payout_currency_code                     CHARACTER VARYING           NOT NULL,
  payout_type                              mst.PAYOUT_TYPE             NOT NULL,
  payout_card_token                        CHARACTER VARYING,
  payout_card_masked_pan                   CHARACTER VARYING,
  payout_card_bin                          CHARACTER VARYING,
  payout_card_payment_system               CHARACTER VARYING,
  payout_account_bank_id                   CHARACTER VARYING,
  payout_account_bank_corr_id              CHARACTER VARYING,
  payout_account_bank_bik                  CHARACTER VARYING,
  payout_account_bank_name                 CHARACTER VARYING,
  payout_account_inn                       CHARACTER VARYING,
  payout_account_legal_agreement_id        CHARACTER VARYING,
  payout_account_legal_agreement_signed_at TIMESTAMP WITHOUT TIME ZONE,
  payout_account_purpose                   CHARACTER VARYING,
  CONSTRAINT payout_event_pkey PRIMARY KEY (id)
);

