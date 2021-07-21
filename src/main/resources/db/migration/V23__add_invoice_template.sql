CREATE TYPE mst.invoice_template_event_type AS ENUM ('INVOICE_TEMPLATE_CREATED', 'INVOICE_TEMPLATE_UPDATED', 'INVOICE_TEMPLATE_DELETED');

CREATE TABLE mst.invoice_template
(
    id                           BIGSERIAL                       NOT NULL,
    event_id                     BIGINT                          NOT NULL,
    event_created_at             TIMESTAMP WITHOUT TIME ZONE     NOT NULL,
    event_type                   mst.invoice_template_event_type NOT NULL,
    invoice_template_id          CHARACTER VARYING               NOT NULL,
    invoice_id                   CHARACTER VARYING               NOT NULL,
    party_id                     CHARACTER VARYING               NOT NULL,
    shop_id                      CHARACTER VARYING               NOT NULL,
    invoice_valid_until          TIMESTAMP WITHOUT TIME ZONE     NOT NULL,
    product                      CHARACTER VARYING               NOT NULL,
    description                  CHARACTER VARYING,
    invoice_details_cart_json    CHARACTER VARYING,
    invoice_details_product_json CHARACTER VARYING,
    invoice_context_type         CHARACTER VARYING,
    invoice_context_data         BYTEA,
    CONSTRAINT invoice_template_pkey PRIMARY KEY (id),
    CONSTRAINT invoice_template_ukey UNIQUE (invoice_id, invoice_template_id)
)
