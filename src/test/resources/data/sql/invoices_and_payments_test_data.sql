INSERT INTO mst.payout_event_stat (id, event_id, event_category, event_type, event_created_at, party_id, party_shop_id, payout_id, payout_created_at, payout_status, payout_account_type, payout_amount, payout_fee, payout_currency_code, payout_type, payout_card_token, payout_card_masked_pan, payout_card_bin, payout_card_payment_system, payout_account_bank_id, payout_account_bank_corr_id, payout_account_bank_local_code, payout_account_bank_name, payout_account_inn, payout_account_legal_agreement_id, payout_account_legal_agreement_signed_at, payout_account_purpose, payout_cancel_details)
VALUES (1, 1000, 'PAYOUT', 'PAYOUT_CREATED', '2017-10-09 13:25:22.881000', '281220eb-a4ef-4d03-b666-bdec4b26c5f7', '1507555501740', '1000', '2017-10-09 13:25:22.845000', 'unpaid', 'RUSSIAN_PAYOUT_ACCOUNT', 186500, null, 'RUB', 'bank_account', null, null, null, null, '40703810432060000034', '30101810600000000786', '044030786', 'Сбер', '7840290139', null, null, 'Перевод согласно договора номер 006815/07 от 17.06.2015.  Без НДС', null);

INSERT INTO mst.refund (event_id, event_created_at, event_type, invoice_id, payment_id, refund_id, party_id, party_shop_id, refund_status, refund_created_at, refund_reason, refund_currency_code, refund_amount, refund_fee, refund_provider_fee, refund_external_fee, refund_domain_revision)
VALUES (1001, '2017-08-24 16:13:23', 'INVOICE_PAYMENT_REFUND_CREATED', 'uAykKfsktM', '1', '1', 'test_party_1', 'test_shop_1', 'succeeded', '2017-08-23 13:06:46', 'kek', 'RUB', 1000, 0, 0, 0, 1);

INSERT INTO mst.adjustment(event_id, event_created_at, event_type, invoice_id, payment_id, adjustment_id, party_id, party_shop_id, adjustment_status, adjustment_status_created_at, adjustment_created_at, adjustment_reason, adjustment_fee)
values (2226238, '2017-08-24 16:13:24', 'INVOICE_PAYMENT_ADJUSTMENT_STATUS_CHANGED', 'uAykKfsktM', '1', '1', 'test_party_1', 'test_shop_1', 'captured', '2021-08-23 12:12:52', '2021-08-23 12:12:52', 'kek', 23);

INSERT INTO mst.payout_event_stat(id, event_id, event_category, event_type, event_created_at, party_id, party_shop_id, payout_id, payout_created_at, payout_status, payout_account_type, payout_amount, payout_currency_code, payout_type)
VALUES (2, 1013, 'PAYOUT', 'PAYOUT_STATUS_CHANGED', '2017-08-28 13:06:46', 'test_party_1', 'test_shop_1', 1014, '2017-08-28 06:00:01', 'paid', 'RUSSIAN_PAYOUT_ACCOUNT', 950, 'RUB', 'bank_account');

INSERT INTO mst.payout_event_stat(id, event_id, event_category, event_type, event_created_at, party_id, party_shop_id, payout_id, payout_created_at, payout_status, payout_account_type, payout_amount, payout_currency_code, payout_type)
VALUES (3, 1015, 'PAYOUT', 'PAYOUT_STATUS_CHANGED', '2017-09-04 13:06:46', 'test_party_1', 'test_shop_1', 1016, '2017-09-04 06:00:01', 'paid', 'RUSSIAN_PAYOUT_ACCOUNT', 975, 'RUB', 'bank_account');

INSERT INTO mst.refund (event_id, event_created_at, event_type, invoice_id, payment_id, refund_id, party_id, party_shop_id, refund_status, refund_created_at, refund_reason, refund_currency_code, refund_amount, refund_fee, refund_provider_fee, refund_external_fee, refund_domain_revision)
VALUES (1002, '2017-09-24 16:13:23', 'INVOICE_PAYMENT_REFUND_CREATED', 'qAyoGtbktM', '1', '1', 'test_party_1', 'test_shop_1', 'succeeded', '2017-08-23 13:06:46', 'kek', 'RUB', 2000, 0, 0, 0, 1);

INSERT INTO mst.payout_event_stat(id, event_id, event_category, event_type, event_created_at, party_id, party_shop_id, payout_id, payout_created_at, payout_status, payout_account_type, payout_amount, payout_currency_code, payout_type)
VALUES (4, 1017, 'PAYOUT', 'PAYOUT_STATUS_CHANGED', '2017-09-27 13:06:46', 'test_party_1', 'test_shop_1', 1018, '2017-09-27 06:00:01', 'paid', 'RUSSIAN_PAYOUT_ACCOUNT', 1900, 'RUB', 'bank_account');

INSERT INTO mst.payout_event_stat(id, event_id, event_category, event_type, event_created_at, party_id, party_shop_id, payout_id, payout_created_at, payout_status, payout_account_type, payout_amount, payout_currency_code, payout_type)
VALUES (5, 1018, 'PAYOUT', 'PAYOUT_STATUS_CHANGED', '2017-09-27 13:06:46', 'test_party_1', 'test_shop_1', 1019, '2017-09-27 06:00:01', 'unpaid', 'RUSSIAN_PAYOUT_ACCOUNT', 300, 'RUB', 'bank_account');

INSERT INTO mst.payout_event_stat(id, event_id, event_category, event_type, event_created_at, party_id, party_shop_id, payout_id, payout_created_at, payout_status, payout_account_type, payout_amount, payout_currency_code, payout_type, payout_summary)
VALUES (6, 1019, 'PAYOUT', 'PAYOUT_STATUS_CHANGED', '2017-09-27 13:06:46', 'test_party_1', 'test_shop_1', 1020, '2017-09-27 06:00:01', 'cancelled', 'RUSSIAN_PAYOUT_ACCOUNT', 300, 'RUB', 'bank_account', '[{"amount":300000,"fee":13500,"currency_symbolic_code":"RUB","from_time":"2018-03-13T12:34:31.655114Z","to_time":"2018-03-13T12:34:40.266264Z","operation_type":"payment","count":3},{"amount":100000,"fee":0,"currency_symbolic_code":"RUB","from_time":"2018-03-13T12:34:38.728332Z","to_time":"2018-03-13T12:34:38.728332Z","operation_type":"refund","count":1}]');

-- Refunds
insert into mst.refund (event_id, event_created_at, event_type, invoice_id, payment_id, refund_id, party_id, party_shop_id, refund_status, refund_created_at, refund_reason, refund_currency_code, refund_amount, refund_fee, refund_provider_fee, refund_external_fee, refund_domain_revision)
    VALUES (1003, '2017-09-27 13:06:46', 'INVOICE_PAYMENT_REFUND_CREATED', 'test_invoice', 'test_payment', 'test_refund', 'test_party', 'test_shop', 'pending', '2017-09-27 13:06:46', 'kek', 'RUB', 15000, 5000, 232, 345, 1);
insert into mst.refund (event_id, event_created_at, event_type, invoice_id, payment_id, refund_id, party_id, party_shop_id, refund_status, refund_created_at, refund_reason, refund_currency_code, refund_amount, refund_fee, refund_provider_fee, refund_external_fee, refund_domain_revision)
VALUES (1004, '2017-09-27 13:06:46', 'INVOICE_PAYMENT_REFUND_CREATED', 'test_invoice_2', 'test_payment_2', 'test_refund_2', 'test_party', 'test_shop', 'pending', '2017-09-27 13:06:46', 'kek', 'RUB', 17200, 5600, 436, 335, 1);
