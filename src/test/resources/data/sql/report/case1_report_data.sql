-- invoice data
INSERT INTO mst.invoice_data (party_id, party_shop_id, invoice_id, invoice_product, invoice_amount, invoice_currency_code, invoice_due, invoice_created_at)
VALUES ('db79ad6c-a507-43ed-9ecf-3bbd88475b32', 'test_shop_1', 'uWIbtnV7h2', 'test_product_1', 1000, 'RUB', '2021-08-23 08:30:33.000000', '2017-08-23 08:30:33.000000');
INSERT INTO mst.invoice_data (party_id, party_shop_id, invoice_id, invoice_product, invoice_amount, invoice_currency_code, invoice_due, invoice_created_at)
VALUES ('db79ad6c-a507-43ed-9ecf-3bbd88475b32', 'test_shop_1', 'uAykKfsktM', 'test_product_1', 1000, 'RUB', '2021-08-23 12:12:52.000000', '2017-08-23 12:12:52.000000');
INSERT INTO mst.invoice_data (party_id, party_shop_id, invoice_id, invoice_product, invoice_amount, invoice_currency_code, invoice_due, invoice_created_at)
VALUES ('db79ad6c-a507-43ed-9ecf-3bbd88475b32', 'test_shop_1', 'uWIOyeV7h3', 'test_product_1', 1000, 'RUB', '2021-08-30 08:30:33.000000', '2017-08-30 08:30:33.000000');
INSERT INTO mst.invoice_data (party_id, party_shop_id, invoice_id, invoice_product, invoice_amount, invoice_currency_code, invoice_due, invoice_created_at)
VALUES ('db79ad6c-a507-43ed-9ecf-3bbd88475b32', 'test_shop_1', 'qWIbtnV7h2', 'test_product_1', 2000, 'RUB', '2021-09-23 08:30:33.000000', '2017-09-23 08:30:33.000000');
INSERT INTO mst.invoice_data (party_id, party_shop_id, invoice_id, invoice_product, invoice_amount, invoice_currency_code, invoice_due, invoice_created_at)
VALUES ('db79ad6c-a507-43ed-9ecf-3bbd88475b32', 'test_shop_1', 'qAyoGtbktM', 'test_product_1', 2000, 'RUB', '2021-09-23 12:12:52.000000', '2017-09-23 12:12:52.000000');
INSERT INTO mst.invoice_data (party_id, party_shop_id, invoice_id, invoice_product, invoice_amount, invoice_currency_code, invoice_due, invoice_created_at)
VALUES ('db79ad6c-a507-43ed-9ecf-3bbd88475b32', 'test_shop_1', 'qWIOyeV7h3', 'test_product_1', 2000, 'RUB', '2021-09-30 08:30:33.000000', '2017-09-30 08:30:33.000000');

-- payment data
INSERT INTO mst.payment_data (invoice_id, payment_id, party_id, party_shop_id, payment_currency_code, payment_origin_amount, payment_payer_type, payment_tool, payment_flow, payment_created_at)
VALUES ('uWIbtnV7h2', '1', 'db79ad6c-a507-43ed-9ecf-3bbd88475b32', 'test_shop_1', 'RUB', 1000, 'payment_resource', 'bank_card', 'instant', '2017-08-23 08:30:34.000000');
INSERT INTO mst.payment_data (invoice_id, payment_id, party_id, party_shop_id, payment_currency_code, payment_origin_amount, payment_payer_type, payment_tool, payment_flow, payment_created_at)
VALUES ('uAykKfsktM', '1', 'db79ad6c-a507-43ed-9ecf-3bbd88475b32', 'test_shop_1', 'RUB', 1000, 'payment_resource', 'bank_card', 'instant', '2017-08-23 12:12:53.000000');
INSERT INTO mst.payment_data (invoice_id, payment_id, party_id, party_shop_id, payment_currency_code, payment_origin_amount, payment_payer_type, payment_tool, payment_flow, payment_created_at)
VALUES ('uWIOyeV7h3', '1', 'db79ad6c-a507-43ed-9ecf-3bbd88475b32', 'test_shop_1', 'RUB', 1000, 'payment_resource', 'bank_card', 'instant', '2017-08-30 08:30:34.000000');
INSERT INTO mst.payment_data (invoice_id, payment_id, party_id, party_shop_id, payment_currency_code, payment_origin_amount, payment_payer_type, payment_tool, payment_flow, payment_created_at)
VALUES ('qWIbtnV7h2', '1', 'db79ad6c-a507-43ed-9ecf-3bbd88475b32', 'test_shop_1', 'RUB', 2000, 'payment_resource', 'bank_card', 'instant', '2017-09-23 08:30:34.000000');
INSERT INTO mst.payment_data (invoice_id, payment_id, party_id, party_shop_id, payment_currency_code, payment_origin_amount, payment_payer_type, payment_tool, payment_flow, payment_created_at)
VALUES ('qAyoGtbktM', '1', 'db79ad6c-a507-43ed-9ecf-3bbd88475b32', 'test_shop_1', 'RUB', 2000, 'payment_resource', 'bank_card', 'instant', '2017-09-23 12:12:53.000000');
INSERT INTO mst.payment_data (invoice_id, payment_id, party_id, party_shop_id, payment_currency_code, payment_origin_amount, payment_payer_type, payment_tool, payment_flow, payment_created_at)
VALUES ('qWIOyeV7h3', '1', 'db79ad6c-a507-43ed-9ecf-3bbd88475b32', 'test_shop_1', 'RUB', 2000, 'payment_resource', 'bank_card', 'instant', '2017-09-30 08:30:34.000000');

-- payment event
INSERT INTO mst.payment_event (event_id, event_created_at, event_type, invoice_id, payment_id, payment_status, payment_currency_code, payment_amount, payment_fee, payment_provider_fee, payment_external_fee, payment_domain_revision)
VALUES (2475599, '2017-08-23 08:30:56.000000', 'INVOICE_PAYMENT_STATUS_CHANGED', 'uWIbtnV7h2', '1', 'captured', 'RUB', 1000, 25, 18, 20, 1);
INSERT INTO mst.payment_event (event_id, event_created_at, event_type, invoice_id, payment_id, payment_status, payment_currency_code, payment_amount, payment_fee, payment_provider_fee, payment_external_fee, payment_domain_revision)
VALUES (2226237, '2017-08-24 16:13:24.000000', 'INVOICE_PAYMENT_STATUS_CHANGED', 'uAykKfsktM', '1', 'captured', 'RUB', 1000, 25, 18, 20, 1);
INSERT INTO mst.payment_event (event_id, event_created_at, event_type, invoice_id, payment_id, payment_status, payment_currency_code, payment_amount, payment_fee, payment_provider_fee, payment_external_fee, payment_domain_revision)
VALUES (2485599, '2017-08-30 08:30:56.000000', 'INVOICE_PAYMENT_STATUS_CHANGED', 'uWIOyeV7h3', '1', 'captured', 'RUB', 1000, 25, 18, 20, 1);
INSERT INTO mst.payment_event (event_id, event_created_at, event_type, invoice_id, payment_id, payment_status, payment_currency_code, payment_amount, payment_fee, payment_provider_fee, payment_external_fee, payment_domain_revision)
VALUES (3475599, '2017-09-23 08:30:56.000000', 'INVOICE_PAYMENT_STATUS_CHANGED', 'qWIbtnV7h2', '1', 'captured', 'RUB', 2000, 50, 36, 20, 1);
INSERT INTO mst.payment_event (event_id, event_created_at, event_type, invoice_id, payment_id, payment_status, payment_currency_code, payment_amount, payment_fee, payment_provider_fee, payment_external_fee, payment_domain_revision)
VALUES (3226237, '2017-09-24 16:13:24.000000', 'INVOICE_PAYMENT_STATUS_CHANGED', 'qAyoGtbktM', '1', 'captured', 'RUB', 2000, 50, 36, 20, 1);
INSERT INTO mst.payment_event (event_id, event_created_at, event_type, invoice_id, payment_id, payment_status, payment_currency_code, payment_amount, payment_fee, payment_provider_fee, payment_external_fee, payment_domain_revision)
VALUES (3485599, '2017-09-30 08:30:56.000000', 'INVOICE_PAYMENT_STATUS_CHANGED', 'qWIOyeV7h3', '1', 'captured', 'RUB', 2000, 50, 36, 20, 1);

-- adjustments
INSERT INTO mst.adjustment(event_id, event_created_at, event_type, invoice_id, payment_id, adjustment_id, party_id, party_shop_id, adjustment_status, adjustment_status_created_at, adjustment_created_at, adjustment_reason, adjustment_fee)
values (2226238, '2017-08-24 16:13:24', 'INVOICE_PAYMENT_ADJUSTMENT_STATUS_CHANGED', 'uAykKfsktM', '1', '1', 'db79ad6c-a507-43ed-9ecf-3bbd88475b32', 'test_shop_1', 'captured', '2021-08-23 12:12:52', '2021-08-23 12:12:52', 'kek', 23);

-- refunds
INSERT INTO mst.refund (event_id, event_created_at, event_type, invoice_id, payment_id, refund_id, party_id, party_shop_id, refund_status, refund_created_at, refund_reason, refund_currency_code, refund_amount, refund_fee, refund_provider_fee, refund_external_fee, refund_domain_revision)
VALUES (1001, '2017-08-24 16:13:23', 'INVOICE_PAYMENT_REFUND_CREATED', 'uAykKfsktM', '1', '1', 'db79ad6c-a507-43ed-9ecf-3bbd88475b32', 'test_shop_1', 'succeeded', '2017-08-23 13:06:46', 'kek', 'RUB', 1000, 0, 0, 0, 1);

INSERT INTO mst.refund (event_id, event_created_at, event_type, invoice_id, payment_id, refund_id, party_id, party_shop_id, refund_status, refund_created_at, refund_reason, refund_currency_code, refund_amount, refund_fee, refund_provider_fee, refund_external_fee, refund_domain_revision)
VALUES (1002, '2017-09-24 16:13:23', 'INVOICE_PAYMENT_REFUND_CREATED', 'qAyoGtbktM', '1', '1', 'db79ad6c-a507-43ed-9ecf-3bbd88475b32', 'test_shop_1', 'succeeded', '2017-08-23 13:06:46', 'kek', 'RUB', 2000, 0, 0, 0, 1);

-- payouts
INSERT INTO mst.payout_event_stat(id, event_id, event_category, event_type, event_created_at, party_id, party_shop_id, payout_id, payout_created_at, payout_status, payout_account_type, payout_amount, payout_currency_code, payout_type)
VALUES (2, 1013, 'PAYOUT', 'PAYOUT_STATUS_CHANGED', '2017-08-28 13:06:46', 'db79ad6c-a507-43ed-9ecf-3bbd88475b32', 'test_shop_1', 1014, '2017-08-28 06:00:01', 'paid', 'RUSSIAN_PAYOUT_ACCOUNT', 950, 'RUB', 'bank_account');

INSERT INTO mst.payout_event_stat(id, event_id, event_category, event_type, event_created_at, party_id, party_shop_id, payout_id, payout_created_at, payout_status, payout_account_type, payout_amount, payout_currency_code, payout_type)
VALUES (3, 1015, 'PAYOUT', 'PAYOUT_STATUS_CHANGED', '2017-09-04 13:06:46', 'db79ad6c-a507-43ed-9ecf-3bbd88475b32', 'test_shop_1', 1016, '2017-09-04 06:00:01', 'paid', 'RUSSIAN_PAYOUT_ACCOUNT', 975, 'RUB', 'bank_account');

INSERT INTO mst.payout_event_stat(id, event_id, event_category, event_type, event_created_at, party_id, party_shop_id, payout_id, payout_created_at, payout_status, payout_account_type, payout_amount, payout_currency_code, payout_type)
VALUES (4, 1017, 'PAYOUT', 'PAYOUT_STATUS_CHANGED', '2017-09-27 13:06:46', 'db79ad6c-a507-43ed-9ecf-3bbd88475b32', 'test_shop_1', 1018, '2017-09-27 06:00:01', 'paid', 'RUSSIAN_PAYOUT_ACCOUNT', 1900, 'RUB', 'bank_account');

INSERT INTO mst.payout_event_stat(id, event_id, event_category, event_type, event_created_at, party_id, party_shop_id, payout_id, payout_created_at, payout_status, payout_account_type, payout_amount, payout_currency_code, payout_type)
VALUES (5, 1018, 'PAYOUT', 'PAYOUT_STATUS_CHANGED', '2017-09-27 13:06:46', 'db79ad6c-a507-43ed-9ecf-3bbd88475b32', 'test_shop_1', 1019, '2017-09-27 06:00:01', 'unpaid', 'RUSSIAN_PAYOUT_ACCOUNT', 300, 'RUB', 'bank_account');

INSERT INTO mst.payout_event_stat(id, event_id, event_category, event_type, event_created_at, party_id, party_shop_id, payout_id, payout_created_at, payout_status, payout_account_type, payout_amount, payout_currency_code, payout_type, payout_summary)
VALUES (6, 1019, 'PAYOUT', 'PAYOUT_STATUS_CHANGED', '2017-09-27 13:06:46', 'db79ad6c-a507-43ed-9ecf-3bbd88475b32', 'test_shop_1', 1020, '2017-09-27 06:00:01', 'cancelled', 'RUSSIAN_PAYOUT_ACCOUNT', 300, 'RUB', 'bank_account', '[{"amount":300000,"fee":13500,"currency_symbolic_code":"RUB","from_time":"2018-03-13T12:34:31.655114Z","to_time":"2018-03-13T12:34:40.266264Z","operation_type":"payment","count":3},{"amount":100000,"fee":0,"currency_symbolic_code":"RUB","from_time":"2018-03-13T12:34:38.728332Z","to_time":"2018-03-13T12:34:38.728332Z","operation_type":"refund","count":1}]');
