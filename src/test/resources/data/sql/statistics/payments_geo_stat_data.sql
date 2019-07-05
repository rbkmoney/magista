insert into mst.invoice_data (event_id, event_created_at, event_type, party_id, party_shop_id, invoice_id, invoice_status, invoice_product, invoice_description, invoice_amount, invoice_currency_code, invoice_due, invoice_created_at)
values (5, now() + interval '2 hour', 'INVOICE_STATUS_CHANGED', 'DB79AD6C-A507-43ED-9ECF-3BBD88475B32', 'SHOP_ID', 'INVOICE_ID_1', 'fulfilled', 'INVOICE_PRODUCT', 'INVOICE_DESCRIPTION', 5, 'RUB', now(), now());

insert into mst.payment_data (event_id, event_created_at, event_type, invoice_id, payment_id, payment_status, party_id, party_shop_id, payment_currency_code, payment_origin_amount, payment_amount, payment_fee, payment_provider_fee, payment_external_fee, payment_domain_revision, payment_payer_type, payment_tool, payment_bank_card_last4, payment_bank_card_first6, payment_bank_card_token, payment_bank_card_system, payment_flow, payment_hold_on_expiration, payment_hold_until, payment_session_id, payment_fingerprint, payment_ip, payment_country_id, payment_city_id, payment_phone_number, payment_email, payment_created_at)
VALUES (3, now(), 'INVOICE_PAYMENT_STATUS_CHANGED', 'INVOICE_ID_1', 'PAYMENT_ID_1', 'captured', 'DB79AD6C-A507-43ED-9ECF-3BBD88475B32', 'SHOP_ID', 'RUB', 50000, 50000, 35, 5, 0, 1, 'payment_resource', 'bank_card', '4242', '424242', 'eriogmiorwgeiroameriovmerio', 'visa', 'hold', 'cancel', now(), 'payment_session_1', 'qweqweldasksldfmdslkfm', '34.23.44.33', 42, 42, '88005553535', 'qwe@mail.ru', now() + interval '2 seconds');

insert into mst.payment_data (event_id, event_created_at, event_type, invoice_id, payment_id, payment_status, party_id, party_shop_id, payment_currency_code, payment_origin_amount, payment_amount, payment_fee, payment_provider_fee, payment_external_fee, payment_domain_revision, payment_payer_type, payment_tool, payment_bank_card_last4, payment_bank_card_first6, payment_bank_card_token, payment_bank_card_system, payment_flow, payment_hold_on_expiration, payment_hold_until, payment_session_id, payment_fingerprint, payment_ip, payment_country_id, payment_city_id, payment_phone_number, payment_email, payment_created_at)
VALUES (5, now(), 'INVOICE_PAYMENT_STATUS_CHANGED', 'INVOICE_ID_1', 'PAYMENT_ID_2', 'captured', 'DB79AD6C-A507-43ED-9ECF-3BBD88475B32', 'SHOP_ID', 'RUB', 50000, 50000, 35, 5, 0, 1, 'payment_resource', 'bank_card', '4242', '424242', 'eriogmiorwgeiroameriovmerio', 'visa', 'hold', 'cancel', now(), 'payment_session_1', 'qweqweldasksldfmdslkfm', '34.23.44.33', 42, 43, '88005553535', 'qwe@mail.ru', now() + interval '2 seconds');
