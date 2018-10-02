-- fulfilled invoice
insert into mst.invoice_data (id, party_id, party_shop_id, party_contract_id, invoice_id, invoice_product, invoice_description, invoice_amount, invoice_currency_code, invoice_due, invoice_created_at)
values (1, 'DB79AD6C-A507-43ED-9ECF-3BBD88475B32', 'SHOP_ID', 'CONTRACT_ID', 'INVOICE_ID_1', 'INVOICE_PRODUCT', 'INVOICE_DESCRIPTION', 5, 'RUB', now(), now());

insert into mst.invoice_event (event_id, event_created_at, event_type, invoice_id, invoice_status) VALUES (1, now() + interval '30 minute', 'INVOICE_CREATED', 'INVOICE_ID_1', 'unpaid');

insert into mst.payment_data (id, invoice_id, payment_id, party_id, party_shop_id, party_contract_id, payment_currency_code, payment_amount, payment_payer_type, payment_tool, payment_bank_card_masked_pan, payment_bank_card_bin, payment_bank_card_token, payment_bank_card_system, payment_flow, payment_hold_on_expiration, payment_hold_until, payment_session_id, payment_fingerprint, payment_ip, payment_phone_number, payment_email, payment_created_at)
    VALUES (1, 'INVOICE_ID_1', 'PAYMENT_ID_1', 'DB79AD6C-A507-43ED-9ECF-3BBD88475B32', 'SHOP_ID', 'CONTRACT_ID', 'RUB', 50000, 'payment_resource', 'bank_card', '4242', '424242', 'eriogmiorwgeiroameriovmerio', 'visa', 'hold', 'cancel', now(), 'payment_session_1', 'qweqweldasksldfmdslkfm', '34.23.44.33', '88005553535', 'qwe@mail.ru', now() + interval '2 seconds');

insert into mst.payment_event (event_id, event_created_at, event_type, invoice_id, payment_id, payment_status, payment_fee, payment_provider_fee, payment_external_fee, payment_domain_revision)
    VALUES (2, now(), 'INVOICE_PAYMENT_STARTED', 'INVOICE_ID_1', 'PAYMENT_ID_1', 'pending', 35, 5, 0, 1);
insert into mst.payment_event (event_id, event_created_at, event_type, invoice_id, payment_id, payment_status, payment_fee, payment_provider_fee, payment_external_fee, payment_domain_revision)
VALUES (3, now(), 'INVOICE_PAYMENT_STATUS_CHANGED', 'INVOICE_ID_1', 'PAYMENT_ID_1', 'captured', 35, 5, 0, 1);

insert into mst.invoice_event (event_id, event_created_at, event_type, invoice_id, invoice_status) VALUES (4, now() + interval '1 hour', 'INVOICE_STATUS_CHANGED', 'INVOICE_ID_1', 'paid');
insert into mst.invoice_event (event_id, event_created_at, event_type, invoice_id, invoice_status, invoice_status_details) VALUES (5, now() + interval '2 hour', 'INVOICE_STATUS_CHANGED', 'INVOICE_ID_1', 'fulfilled', 'status details');

-- cancelled invoice
insert into mst.invoice_data (id, party_id, party_shop_id, party_contract_id, invoice_id, invoice_product, invoice_description, invoice_amount, invoice_currency_code, invoice_due, invoice_created_at)
values (2, 'DB79AD6C-A507-43ED-9ECF-3BBD88475B32', 'SHOP_ID', 'CONTRACT_ID', 'INVOICE_ID_2', 'INVOICE_PRODUCT', 'INVOICE_DESCRIPTION', 10, 'RUB', now(), now() + interval '1 second');

insert into mst.payment_data (id, invoice_id, payment_id, party_id, party_shop_id, party_contract_id, payment_currency_code, payment_amount, payment_payer_type, payment_tool, payment_bank_card_masked_pan, payment_bank_card_bin, payment_bank_card_token, payment_bank_card_system, payment_flow, payment_hold_on_expiration, payment_hold_until, payment_session_id, payment_fingerprint, payment_ip, payment_phone_number, payment_email, payment_created_at)
VALUES (2, 'INVOICE_ID_2', 'PAYMENT_ID_1', 'DB79AD6C-A507-43ED-9ECF-3BBD88475B32', 'SHOP_ID', 'CONTRACT_ID', 'RUB', 50000, 'payment_resource', 'bank_card', '4242', '424242', 'eriogmiorwgeiroameriovmerio', 'visa', 'hold', 'cancel', now(), 'payment_session_1', 'qweqweldasksldfmdslkfm', '34.23.44.33', '88005553535', 'qwe@mail.ru', now() + interval '5 seconds');

insert into mst.payment_event (event_id, event_created_at, event_type, invoice_id, payment_id, payment_status, payment_fee, payment_provider_fee, payment_external_fee, payment_domain_revision)
VALUES (6, now(), 'INVOICE_PAYMENT_STARTED', 'INVOICE_ID_2', 'PAYMENT_ID_1', 'pending', 55, 5, 0, 1);
insert into mst.payment_event (event_id, event_created_at, event_type, invoice_id, payment_id, payment_status, payment_fee, payment_provider_fee, payment_external_fee, payment_domain_revision, payment_operation_failure_class)
VALUES (7, now(), 'INVOICE_PAYMENT_STATUS_CHANGED', 'INVOICE_ID_2', 'PAYMENT_ID_1', 'failed', 55, 5, 0, 1, 'operation_timeout');

insert into mst.invoice_event (event_id, event_created_at, event_type, invoice_id, invoice_status)
VALUES (8, now() + interval '30 minute', 'INVOICE_CREATED', 'INVOICE_ID_2', 'unpaid');

insert into mst.invoice_event (event_id, event_created_at, event_type, invoice_id, invoice_status, invoice_status_details)
VALUES (9, now() + interval '1 hour', 'INVOICE_STATUS_CHANGED', 'INVOICE_ID_2', 'cancelled', 'invoice details');

-- invoice with another merchant
insert into mst.invoice_data (id, party_id, party_shop_id, party_contract_id, invoice_id, invoice_product, invoice_description, invoice_amount, invoice_currency_code, invoice_due, invoice_created_at)
values (3, 'A25B27EE-BE91-4977-9DB5-CCF52CC83741', 'SHOP_ID', 'CONTRACT_ID', 'INVOICE_NEW_ID_1', 'INVOICE_PRODUCT', 'INVOICE_DESCRIPTION', 10, 'RUB', now(), now() + interval '2 second');
insert into mst.invoice_event (event_id, event_created_at, event_type, invoice_id, invoice_status)
VALUES (10, now() + interval '30 minute', 'INVOICE_CREATED', 'INVOICE_NEW_ID_1', 'unpaid');

insert into mst.payment_data (id, invoice_id, payment_id, party_id, party_shop_id, party_contract_id, payment_currency_code, payment_amount, payment_payer_type, payment_tool, payment_bank_card_masked_pan, payment_bank_card_bin, payment_bank_card_token, payment_bank_card_system, payment_flow, payment_hold_on_expiration, payment_hold_until, payment_session_id, payment_fingerprint, payment_ip, payment_phone_number, payment_email, payment_created_at)
VALUES (3, 'INVOICE_NEW_ID_1', 'PAYMENT_ID_1', 'A25B27EE-BE91-4977-9DB5-CCF52CC83741', 'SHOP_ID', 'CONTRACT_ID', 'RUB', 50000, 'payment_resource', 'bank_card', '4242', '424242', 'eriogmiorwgeiroameriovmerio', 'visa', 'hold', 'cancel', now(), 'payment_session_1', 'qweqweldasksldfmdslkfm', '34.23.44.33', '88005553535', 'qwe@mail.ru', now() + interval '5 seconds');

insert into mst.payment_event (event_id, event_created_at, event_type, invoice_id, payment_id, payment_status, payment_fee, payment_provider_fee, payment_external_fee, payment_domain_revision)
VALUES (11, now(), 'INVOICE_PAYMENT_STARTED', 'INVOICE_NEW_ID_1', 'PAYMENT_ID_1', 'pending', 55, 5, 0, 1);