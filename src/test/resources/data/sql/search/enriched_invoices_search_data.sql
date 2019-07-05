-- fulfilled invoice
insert into mst.invoice_data (event_id, event_created_at, event_type, party_id, party_shop_id, invoice_id, invoice_status, invoice_product, invoice_description, invoice_amount, invoice_currency_code, invoice_due, invoice_created_at)
values (1, now() + interval '30 minute', 'INVOICE_CREATED', 'DB79AD6C-A507-43ED-9ECF-3BBD88475B32', 'SHOP_ID', 'INVOICE_ID_1', 'unpaid', 'INVOICE_PRODUCT', 'INVOICE_DESCRIPTION', 5, 'RUB', now(), now());

insert into mst.payment_data (event_id, event_created_at, event_type, invoice_id, payment_id, payment_status, party_id, party_shop_id, payment_currency_code, payment_origin_amount, payment_amount, payment_fee, payment_provider_fee, payment_external_fee, payment_domain_revision, payment_payer_type, payment_tool, payment_bank_card_last4, payment_bank_card_first6, payment_bank_card_token, payment_bank_card_system, payment_flow, payment_hold_on_expiration, payment_hold_until, payment_session_id, payment_fingerprint, payment_ip, payment_phone_number, payment_email, payment_created_at)
VALUES (2, now(), 'INVOICE_PAYMENT_STARTED', 'INVOICE_ID_1', 'PAYMENT_ID_1', 'pending', 'DB79AD6C-A507-43ED-9ECF-3BBD88475B32', 'SHOP_ID', 'RUB', 50000, 50000, 35, 5, 0, 1, 'payment_resource', 'bank_card', '4242', '424242', 'eriogmiorwgeiroameriovmerio', 'visa', 'hold', 'cancel', now(), 'payment_session_1', 'qweqweldasksldfmdslkfm', '34.23.44.33', '88005553535', 'qwe@mail.ru', now() + interval '2 seconds');

update mst.payment_data set (event_id, event_created_at, event_type, invoice_id, payment_id, payment_status, payment_currency_code, payment_amount, payment_fee, payment_provider_fee, payment_external_fee, payment_domain_revision)
                              = (3, now(), 'INVOICE_PAYMENT_STATUS_CHANGED', 'INVOICE_ID_1', 'PAYMENT_ID_1', 'captured', 'RUB', 50000, 35, 5, 0, 1);

update mst.invoice_data set (event_id, event_created_at, event_type, invoice_id, invoice_status, invoice_status_details) = (5, now() + interval '2 hour', 'INVOICE_STATUS_CHANGED', 'INVOICE_ID_1', 'fulfilled', 'status details');

-- cancelled invoice
insert into mst.invoice_data (event_id, event_created_at, event_type, party_id, party_shop_id, invoice_id, invoice_status, invoice_product, invoice_description, invoice_amount, invoice_currency_code, invoice_due, invoice_created_at)
values (8, now() + interval '30 minute', 'INVOICE_CREATED', 'DB79AD6C-A507-43ED-9ECF-3BBD88475B32', 'SHOP_ID', 'INVOICE_ID_2', 'unpaid', 'INVOICE_PRODUCT', 'INVOICE_DESCRIPTION', 10, 'RUB', now(), now() + interval '1 second');

insert into mst.payment_data (event_id, event_created_at, event_type, invoice_id, payment_id, payment_status, party_id, party_shop_id, payment_currency_code, payment_origin_amount, payment_amount, payment_domain_revision, payment_payer_type, payment_tool, payment_bank_card_last4, payment_bank_card_first6, payment_bank_card_token, payment_bank_card_system, payment_flow, payment_hold_on_expiration, payment_hold_until, payment_session_id, payment_fingerprint, payment_ip, payment_phone_number, payment_email, payment_created_at)
VALUES (8, now() + interval '30 minute', 'INVOICE_PAYMENT_STARTED', 'INVOICE_ID_2', 'PAYMENT_ID_1', 'pending', 'DB79AD6C-A507-43ED-9ECF-3BBD88475B32', 'SHOP_ID', 'RUB', 50000, 50000, 1, 'payment_resource', 'bank_card', '4242', '424242', 'eriogmiorwgeiroameriovmerio', 'visa', 'hold', 'cancel', now(), 'payment_session_1', 'qweqweldasksldfmdslkfm', '34.23.44.33', '88005553535', 'qwe@mail.ru', now() + interval '5 seconds');

update mst.payment_data set (event_id, event_created_at, event_type, invoice_id, payment_id, payment_status, payment_currency_code, payment_amount, payment_fee, payment_provider_fee, payment_external_fee, payment_domain_revision, payment_operation_failure_class)
                              = (7, now(), 'INVOICE_PAYMENT_STATUS_CHANGED', 'INVOICE_ID_2', 'PAYMENT_ID_1', 'failed', 'RUB', 50000, 55, 5, 0, 1, 'operation_timeout') where invoice_id = 'INVOICE_ID_2' and payment_id = 'PAYMENT_ID_1';

update mst.invoice_data set (event_id, event_created_at, event_type, invoice_id, invoice_status)
                              = (8, now() + interval '30 minute', 'INVOICE_CREATED', 'INVOICE_ID_2', 'unpaid') where invoice_id = 'INVOICE_ID_2';

update mst.invoice_data set (event_id, event_created_at, event_type, invoice_id, invoice_status, invoice_status_details)
                              = (9, now() + interval '1 hour', 'INVOICE_STATUS_CHANGED', 'INVOICE_ID_2', 'cancelled', 'invoice details') where invoice_id = 'INVOICE_ID_2';

-- invoice with another merchant
insert into mst.invoice_data (event_id, event_created_at, event_type, party_id, party_shop_id, invoice_id, invoice_status, invoice_product, invoice_description, invoice_amount, invoice_currency_code, invoice_due, invoice_created_at)
values (10, now() + interval '30 minute', 'INVOICE_CREATED', 'A25B27EE-BE91-4977-9DB5-CCF52CC83741', 'SHOP_ID', 'INVOICE_NEW_ID_1', 'unpaid', 'INVOICE_PRODUCT', 'INVOICE_DESCRIPTION', 10, 'RUB', now(), now() + interval '2 second');

insert into mst.payment_data (event_id, event_created_at, event_type, invoice_id, payment_id, payment_status, party_id, party_shop_id, payment_currency_code, payment_origin_amount, payment_amount, payment_fee, payment_provider_fee, payment_external_fee, payment_domain_revision, payment_payer_type, payment_tool, payment_bank_card_last4, payment_bank_card_first6, payment_bank_card_token, payment_bank_card_system, payment_flow, payment_hold_on_expiration, payment_hold_until, payment_session_id, payment_fingerprint, payment_ip, payment_phone_number, payment_email, payment_created_at)
VALUES (11, now(), 'INVOICE_PAYMENT_STARTED', 'INVOICE_NEW_ID_1', 'PAYMENT_ID_1', 'pending', 'A25B27EE-BE91-4977-9DB5-CCF52CC83741', 'SHOP_ID', 'RUB', 50000, 50000, 55, 5, 0, 1, 'payment_resource', 'bank_card', '4242', '424242', 'eriogmiorwgeiroameriovmerio', 'visa', 'hold', 'cancel', now(), 'payment_session_1', 'qweqweldasksldfmdslkfm', '34.23.44.33', '88005553535', 'qwe@mail.ru', now() + interval '5 seconds');


-- refunds
insert into mst.refund_data (event_id, event_created_at, event_type, invoice_id, payment_id, refund_id, party_id, party_shop_id, refund_status, refund_operation_failure_class, refund_external_failure, refund_external_failure_reason, refund_created_at, refund_reason, refund_currency_code, refund_amount, refund_fee, refund_provider_fee, refund_external_fee, refund_domain_revision)
VALUES (12, now(), 'INVOICE_PAYMENT_REFUND_CREATED', 'INVOICE_NEW_ID_1', 'PAYMENT_ID_1', 'REFUND_ID_1', 'A25B27EE-BE91-4977-9DB5-CCF52CC83741', 'SHOP_ID', 'succeeded', null, null, null, now(), 'test', 'RUB', 50000, 50000,  55, 5, 0);

insert into mst.refund_data (event_id, event_created_at, event_type, invoice_id, payment_id, refund_id, party_id, party_shop_id, refund_status, refund_operation_failure_class, refund_external_failure, refund_external_failure_reason, refund_created_at, refund_reason, refund_currency_code, refund_amount, refund_fee, refund_provider_fee, refund_external_fee, refund_domain_revision)
VALUES (13, now(), 'INVOICE_PAYMENT_REFUND_CREATED', 'INVOICE_ID_1', 'PAYMENT_ID_1', 'REFUND_ID_1', 'A25B27EE-BE91-4977-9DB5-CCF52CC83741', 'SHOP_ID', 'failed', 'failure', 'lol', 'kek', now(), 'test', 'RUB', 50000, 50000,  55, 5, 0);

insert into mst.refund_data (event_id, event_created_at, event_type, invoice_id, payment_id, refund_id, party_id, party_shop_id, refund_status, refund_operation_failure_class, refund_external_failure, refund_external_failure_reason, refund_created_at, refund_reason, refund_currency_code, refund_amount, refund_fee, refund_provider_fee, refund_external_fee, refund_domain_revision)
VALUES (14, now(), 'INVOICE_PAYMENT_REFUND_CREATED', 'INVOICE_ID_1', 'PAYMENT_ID_1', 'REFUND_ID_2', 'A25B27EE-BE91-4977-9DB5-CCF52CC83741', 'SHOP_ID', 'succeeded', null, null, null, now(), 'test', 'RUB', 50000, 50000,  55, 5, 0);

