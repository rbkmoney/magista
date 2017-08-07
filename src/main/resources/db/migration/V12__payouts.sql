CREATE TYPE mst.PAYOUT_EVENT_CATEGORY AS ENUM ('PAYOUT');

CREATE TYPE mst.PAYOUT_EVENT_TYPE AS ENUM ('PAYOUT_CREATED', 'PAYOUT_STATUS_CHANGED');

CREATE TYPE mst.PAYOUT_STATUS AS ENUM ('unpaid', 'paid', 'cancelled', 'confirmed');

CREATE TYPE mst.PAYOUT_TYPE AS ENUM ('card_payout', 'account_payout');

CREATE TABLE mst.payout_event_stat (
  id                          BIGSERIAL                   NOT NULL,
  event_id                    BIGINT                      NOT NULL,
  event_category              mst.PAYOUT_EVENT_CATEGORY   NOT NULL,
  event_type                  mst.PAYOUT_EVENT_TYPE       NOT NULL,
  event_created_at            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  party_id                    CHARACTER VARYING           NOT NULL,
  party_shop_id               CHARACTER VARYING           NOT NULL,
  payout_id                   CHARACTER VARYING           NOT NULL,
  payout_created_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  payout_status               mst.PAYOUT_STATUS           NOT NULL,
  payout_amount               BIGINT                      NOT NULL,
  payout_fee                  BIGINT,
  payout_type                 mst.PAYOUT_TYPE             NOT NULL,
  payout_paid_details_type    CHARACTER VARYING,
  payout_card_request_id      CHARACTER VARYING,
  payout_card_mask_pan        CHARACTER VARYING,
  payout_card_provider_name   CHARACTER VARYING,
  payout_card_transaction_id  CHARACTER VARYING,
  payout_account_id           CHARACTER VARYING,
  payout_account_bank_corr_id CHARACTER VARYING,
  payout_account_bank_bik     CHARACTER VARYING,
  payout_account_bank_inn     CHARACTER VARYING,
  payout_account_purpose      CHARACTER VARYING,
  payout_cancel_details       CHARACTER VARYING,
  CONSTRAINT payment_event_pkey PRIMARY KEY (id)
);