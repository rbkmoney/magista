alter table mst.adjustment
    add column adjustment_fee bigint,
    add column adjustment_provider_fee bigint,
    add column adjustment_external_fee bigint,
    add column payment_status mst.INVOICE_PAYMENT_STATUS,
    add column payment_operation_failure_class mst.FAILURE_CLASS,
    add column payment_external_failure CHARACTER VARYING,
    add column payment_external_failure_reason CHARACTER VARYING;
