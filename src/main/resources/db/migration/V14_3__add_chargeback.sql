CREATE TYPE mst.chargeback_status AS ENUM ('pending', 'accepted', 'rejected', 'cancelled');

CREATE TYPE mst.chargeback_category AS ENUM ('fraud', 'dispute', 'authorisation', 'processing_error');

CREATE TYPE mst.chargeback_stage AS ENUM ('chargeback', 'pre_arbitration', 'arbitration');

CREATE TABLE chargeback_data
(
    id                            BIGSERIAL                   NOT NULL,
    event_id                      BIGINT                      NOT NULL,
    event_created_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    event_type                    mst.invoice_event_type      NOT NULL,
    invoice_id                    CHARACTER VARYING           NOT NULL,
    payment_id                    CHARACTER VARYING           NOT NULL,
    chargeback_id                 CHARACTER VARYING           NOT NULL,
    party_id                      CHARACTER VARYING           NOT NULL,
    party_shop_id                 CHARACTER VARYING           NOT NULL,
    chargeback_status             mst.chargeback_status       NOT NULL,
    chargeback_created_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    chargeback_reason             CHARACTER VARYING           NOT NULL,
    chargeback_reason_category    mst.chargeback_category     NOT NULL,
    chargeback_domain_revision    BIGINT                      NOT NULL,
    chargeback_party_revision     BIGINT,
    chargeback_levy_amount        BIGINT                      NOT NULL,
    chargeback_levy_currency_code CHARACTER VARYING           NOT NULL,
    chargeback_amount             BIGINT                      NOT NULL,
    chargeback_currency_code      CHARACTER VARYING           NOT NULL,
    chargeback_fee                BIGINT,
    chargeback_provider_fee       BIGINT,
    chargeback_external_fee       BIGINT,
    chargeback_stage              mst.chargeback_stage        NOT NULL,
    chargeback_context            BYTEA,

    CONSTRAINT chargeback_data_pkey PRIMARY KEY (id),
    CONSTRAINT chargeback_data_ukey UNIQUE (invoice_id, payment_id, chargeback_id)
);
