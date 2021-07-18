CREATE TABLE mst.invoice_template
(
    id                  BIGSERIAL                   NOT NULL,
    event_id            BIGINT                      NOT NULL,
    event_created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    invoice_template_id CHARACTER VARYING           NOT NULL,
    invoice_id          CHARACTER VARYING           NOT NULL,
    party_id            CHARACTER VARYING           NOT NULL,
    party_shop_id       CHARACTER VARYING           NOT NULL,
    CONSTRAINT invoice_template_pkey PRIMARY KEY (id),
    CONSTRAINT invoice_template_ukey UNIQUE (invoice_id, invoice_template_id)
)
