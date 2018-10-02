-- terminal provider
insert into mst.invoice_data (party_id, party_shop_id, party_contract_id, invoice_id, invoice_product, invoice_description, invoice_amount, invoice_currency_code, invoice_due, invoice_created_at)
values ('DB79AD6C-A507-43ED-9ECF-3BBD88475B32', 'SHOP_ID', 'CONTRACT_ID', 'INVOICE_ID_1', 'INVOICE_PRODUCT', 'INVOICE_DESCRIPTION', 5, 'RUB', now(), now());

insert into mst.invoice_event (event_id, event_created_at, event_type, invoice_id, invoice_status)
VALUES (1, now() + interval '30 minute', 'INVOICE_CREATED', 'INVOICE_ID_1', 'unpaid');

insert into mst.payment_data (invoice_id, payment_id, party_id, party_shop_id, party_contract_id, payment_currency_code, payment_amount, payment_payer_type, payment_tool, payment_terminal_provider, payment_flow, payment_session_id, payment_fingerprint, payment_ip, payment_phone_number, payment_email, payment_created_at)
VALUES ('INVOICE_ID_1', 'PAYMENT_ID_1', 'DB79AD6C-A507-43ED-9ECF-3BBD88475B32', 'SHOP_ID', 'CONTRACT_ID', 'RUB', 50000, 'payment_resource', 'payment_terminal', 'euroset', 'instant', 'payment_session_1', 'qweqweldasksldfmdslkfm', '34.23.44.33', '88005553535', 'qwe@mail.ru', now());

insert into mst.payment_event (event_id, event_created_at, event_type, invoice_id, payment_id, payment_status, payment_fee, payment_provider_fee, payment_external_fee, payment_domain_revision)
VALUES (2, now(), 'INVOICE_PAYMENT_STATUS_CHANGED', 'INVOICE_ID_1', 'PAYMENT_ID_1', 'pending', 55, 5, 0, 1);
