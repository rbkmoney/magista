insert into mst.refund_data (event_id, event_created_at, event_type, party_id, party_shop_id, invoice_id, payment_id, refund_id, refund_status,  refund_amount, refund_currency_code, refund_created_at)
values (1, now(), 'INVOICE_PAYMENT_REFUND_CREATED', 'PARTY_ID_1', 'SHOP_ID_1', 'INVOICE_ID_1', '1', '1', 'pending', 5, 'RUB', now());

insert into mst.refund_data (event_id, event_created_at, event_type, party_id, party_shop_id, invoice_id, payment_id, refund_id, refund_status,  refund_amount, refund_currency_code, refund_created_at)
values (2, now(), 'INVOICE_PAYMENT_REFUND_CREATED', 'PARTY_ID_1', 'SHOP_ID_1', 'INVOICE_ID_2', '1', '1', 'pending', 5, 'RUB', now());

insert into mst.refund_data (event_id, event_created_at, event_type, party_id, party_shop_id, invoice_id, payment_id, refund_id, refund_status,  refund_amount, refund_currency_code, refund_created_at)
values (3, now(), 'INVOICE_PAYMENT_REFUND_STATUS_CHANGED', 'PARTY_ID_2', 'SHOP_ID_2', 'INVOICE_ID_3', '1', '1', 'succeeded', 5, 'RUB', now());

insert into mst.refund_data (event_id, event_created_at, event_type, party_id, party_shop_id, invoice_id, payment_id, refund_id, refund_status, refund_operation_failure_class, refund_external_failure, refund_external_failure_reason, refund_amount, refund_currency_code, refund_created_at)
values (4, now(), 'INVOICE_PAYMENT_REFUND_STATUS_CHANGED', 'PARTY_ID_1', 'SHOP_ID_1', 'INVOICE_ID_4', '1', '1', 'failed', 'failure', 'external_failure', 'external_failure_reason', 5, 'RUB', now());
