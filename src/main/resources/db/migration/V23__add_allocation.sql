CREATE TYPE mst.rounding_method AS ENUM ('round_half_towards_zero', 'round_half_away_from_zero');

CREATE TABLE mst.allocation_transaction_data
(
    id                  BIGSERIAL                   NOT NULL,
    event_id            BIGINT                      NOT NULL,
    event_type          mst.invoice_event_type      NOT NULL,
    event_created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    invoice_id          CHARACTER VARYING           NOT NULL,
    allocation_id       CHARACTER VARYING           NOT NULL,
    target_owner_id     CHARACTER VARYING,
    target_shop_id      CHARACTER VARYING,
    amount              BIGINT                      NOT NULL,
    currency            CHARACTER VARYING           NOT NULL,
    fee_target_owner_id CHARACTER VARYING,
    fee_target_shop_id  CHARACTER VARYING,
    fee_amount          BIGINT,
    fee_currency        CHARACTER VARYING,
    fee_rational_p      BIGINT,
    fee_rational_q      BIGINT,
    fee_rounding_method mst.rounding_method,
    total_amount        BIGINT,
    total_currency      CHARACTER VARYING,
    invoice_cart_json   CHARACTER VARYING,
    CONSTRAINT allocation_data_pkey PRIMARY KEY (id),
    CONSTRAINT allocation_data_invoice_id_allocation_id_ukey UNIQUE (invoice_id, allocation_id)
)
