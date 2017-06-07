--Invoices
INSERT INTO mst.invoice (id, event_id, merchant_id, shop_id, status, amount, currency_code, product, description, created_at, due, changed_at) VALUES ('l6KYVmwvgW', 3, '74480e4f-1a36-4edd-8175-7a9e984313b0', '1', 'unpaid', 1775000, 'RUB', '55', '1x phone, 3x Дакимакура', '2016-10-25 15:16:05.977236', '2016-10-26T12:16:05', '2016-10-25 15:16:05.977236');
INSERT INTO mst.invoice (id, event_id, merchant_id, shop_id, status, amount, currency_code, product, description, created_at, due, changed_at) VALUES ('l6MKIV4tZQ', 4, '74480e4f-1a36-4edd-8175-7a9e984313b0', '1', 'paid', 2366000, 'RUB', '56', '1x phone, 4x Дакимакура', '2016-10-25 15:40:52.714380', '2016-10-26T12:40:52', '2016-10-25 15:42:22.738495');
INSERT INTO mst.invoice (id, event_id, merchant_id, shop_id, status, amount, currency_code, product, description, created_at, due, changed_at) VALUES ('l6MdTNgPku', 13, '74480e4f-1a36-4edd-8175-7a9e984313b0', '1', 'paid', 444000, 'RUB', '57', '1x Пикачу шляпа', '2016-10-25 15:45:12.382847', '2016-10-26T12:45:12', '2016-10-25 15:45:36.171399');
INSERT INTO mst.invoice (id, event_id, merchant_id, shop_id, status, amount, currency_code, product, description, created_at, due, changed_at) VALUES ('l6MpE5OUvg', 22, '74480e4f-1a36-4edd-8175-7a9e984313b0', '1', 'paid', 540100, 'RUB', '58', '1x Переноска для картошки (белорусская)', '2016-10-25 15:47:51.543270', '2016-10-26T12:47:51', '2016-10-25 15:48:47.650962');
INSERT INTO mst.invoice (id, event_id, merchant_id, shop_id, status, amount, currency_code, product, description, created_at, due, changed_at) VALUES ('l6Nhd36Msq', 34, '74480e4f-1a36-4edd-8175-7a9e984313b0', '1', 'paid', 2500100, 'RUB', '59', '1x Телескопический полностью автоматический пулемет род C-03', '2016-10-25 16:00:08.284518', '2016-10-26T13:00:08', '2016-10-25 16:00:43.948338');
INSERT INTO mst.invoice (id, event_id, merchant_id, shop_id, status, amount, currency_code, product, description, created_at, due, changed_at) VALUES ('l6QVpaDFtw', 55, '74480e4f-1a36-4edd-8175-7a9e984313b0', '1', 'paid', 591000, 'RUB', '61', '1x Дакимакура', '2016-10-25 16:39:27.379576', '2016-10-26T13:39:27', '2016-10-25 16:39:48.293729');
INSERT INTO mst.invoice (id, event_id, merchant_id, shop_id, status, amount, currency_code, product, description, created_at, due, changed_at) VALUES ('l6Qb6YPVRY', 63, '74480e4f-1a36-4edd-8175-7a9e984313b0', '1', 'unpaid', 591000, 'RUB', '62', '1x Дакимакура', '2016-10-25 16:40:38.791082', '2016-10-26T13:40:38', '2016-10-25 16:40:38.791082');
INSERT INTO mst.invoice (id, event_id, merchant_id, shop_id, status, amount, currency_code, product, description, created_at, due, changed_at) VALUES ('l6Op9zzxtw', 43, '74480e4f-1a36-4edd-8175-7a9e984313b0', '1', 'paid', 450000, 'RUB', '60', '1x Дубинка резиновая', '2016-10-25 16:15:49.884332', '2016-10-26T13:15:49', '2016-12-20 18:04:49.050000');

--Payments
INSERT INTO mst.payment (payment_id, event_id, invoice_id, merchant_id, shop_id, customer_id, masked_pan, status, amount, fee, currency_code, token, bin, session_id, email, payment_system, country_id, city_id, ip, created_at, changed_at) VALUES ('1', 5, 'l6MKIV4tZQ', '74480e4f-1a36-4edd-8175-7a9e984313b0', '1', 'c647166641620c768f03977aea4e79f6', '0021', 'captured', 2366000, 106470, 'RUB', '62QQ0GffRaVvrb1XgHf571', '220138', '32ylS9JXIbMkLAVfjydyx7', '', 'nspkmir', -1, -1, '172.17.0.11', '2016-10-25 15:42:19.002604', '2016-10-25 15:42:22.738494');
INSERT INTO mst.payment (payment_id, event_id, invoice_id, merchant_id, shop_id, customer_id, masked_pan, status, amount, fee, currency_code, token, bin, session_id, email, payment_system, country_id, city_id, ip, created_at, changed_at) VALUES ('1', 14, 'l6MdTNgPku', '74480e4f-1a36-4edd-8175-7a9e984313b0', '1', '90b3bd52129ff2a40277445e02b85df3', '0021', 'captured', 444000, 19980, 'RUB', '62QQ0GffRaVvrb1XgHf571', '220138', '3TuZfSUwMCIdaZGlE2tSs0', 'i.galeev@rbkmoney.com', 'nspkmir', -1, -1, '172.17.0.11', '2016-10-25 15:45:32.456564', '2016-10-25 15:45:36.171399');
INSERT INTO mst.payment (payment_id, event_id, invoice_id, merchant_id, shop_id, customer_id, masked_pan, status, failure_code, failure_description, amount, fee, currency_code, token, bin, session_id, email, payment_system, country_id, city_id, ip, created_at, changed_at) VALUES ('1', 23, 'l6MpE5OUvg', '74480e4f-1a36-4edd-8175-7a9e984313b0', '1', '90b3bd52129ff2a40277445e02b85df3', '0021', 'failed', '302', 'External decline', 540100, 24304, 'RUB', '62QQ0GffRaVvrb1XgHf571', '220138', '2iKExJIq4a1ukJvhqIAD2d', '', 'nspkmir', -1, -1, '172.17.0.11', '2016-10-25 15:48:10.277519', '2016-10-25 15:48:11.894150');
INSERT INTO mst.payment (payment_id, event_id, invoice_id, merchant_id, shop_id, customer_id, masked_pan, status, amount, fee, currency_code, token, bin, session_id, email, payment_system, country_id, city_id, ip, created_at, changed_at) VALUES ('2', 26, 'l6MpE5OUvg', '74480e4f-1a36-4edd-8175-7a9e984313b0', '2', '90b3bd52129ff2a40277445e02b85df3', '0021', 'captured', 540100, 24304, 'RUB', '62QQ0GffRaVvrb1XgHf571', '220138', '6A9ZRgyyKyKyzf3h7ZEYcq', '', 'nspkmir', -1, -1, '172.17.0.11', '2016-10-25 15:48:44.234858', '2016-10-25 15:48:47.650962');
INSERT INTO mst.payment (payment_id, event_id, invoice_id, merchant_id, shop_id, customer_id, masked_pan, status, amount, fee, currency_code, token, bin, session_id, email, payment_system, country_id, city_id, ip, created_at, changed_at) VALUES ('1', 35, 'l6Nhd36Msq', '74480e4f-1a36-4edd-8175-7a9e984313b0', '2', '90b3bd52129ff2a40277445e02b85df3', '0021', 'captured', 2500100, 112504, 'RUB', '62QQ0GffRaVvrb1XgHf571', '220138', '6FJaPnLBSkCsGyFagqExrY', '', 'nspkmir', -1, -1, '172.17.0.11', '2016-10-25 16:00:40.533016', '2016-10-25 16:00:43.948338');
INSERT INTO mst.payment (payment_id, event_id, invoice_id, merchant_id, shop_id, customer_id, masked_pan, status, amount, fee, currency_code, token, bin, session_id, email, payment_system, country_id, city_id, ip, created_at, changed_at) VALUES ('1', 44, 'l6Op9zzxtw', '74480e4f-1a36-4edd-8175-7a9e984313b0', '3', '90b3bd52129ff2a40277445e02b85df3', '0777', 'captured', 450000, 20250, 'RUB', 'CmeArO8tUlk6TErU9ejEB', '430000', '3k3dbSXMn20UdnU7qhggDg', '', 'visa', -1, -1, '172.17.0.11', '2016-10-25 16:16:25.022311', '2016-12-20 18:04:49.050000');
INSERT INTO mst.payment (payment_id, event_id, invoice_id, merchant_id, shop_id, customer_id, masked_pan, status, amount, fee, currency_code, token, bin, session_id, email, payment_system, country_id, city_id, ip, created_at, changed_at) VALUES ('1', 56, 'l6QVpaDFtw', '74480e4f-1a36-4edd-8175-7a9e984313b0', '2', 'd0ad26fa4879e688b591633ac80ecd85', '0777', 'captured', 591000, 26595, 'RUB', 'CmeArO8tUlk6TErU9ejEB', '430000', '67h8ouSQ5XJpFQE5mnmuVf', '', 'visa', -1, -1, '172.17.0.11', '2016-10-25 16:39:45.810125', '2016-10-25 16:39:48.293728');

--Customers
INSERT INTO mst.customer (id, shop_id, merchant_id, created_at) VALUES ('c647166641620c768f03977aea4e79f6', '1', '74480e4f-1a36-4edd-8175-7a9e984313b0', '2016-10-25 15:42:19.002604');
INSERT INTO mst.customer (id, shop_id, merchant_id, created_at) VALUES ('90b3bd52129ff2a40277445e02b85df3', '1', '74480e4f-1a36-4edd-8175-7a9e984313b0', '2016-10-25 15:45:32.456564');
INSERT INTO mst.customer (id, shop_id, merchant_id, created_at) VALUES ('d0ad26fa4879e688b591633ac80ecd85', '1', '74480e4f-1a36-4edd-8175-7a9e984313b0', '2016-10-25 16:39:45.810125');

-- Invoice event stat
INSERT INTO mst.invoice_event_stat (event_id, event_category, event_type, event_created_at, party_id, party_email, party_shop_id, party_shop_name, party_shop_description, party_shop_url, party_shop_category_id, party_shop_payout_tool_id, party_contract_id, party_contract_registered_number, party_contract_inn, invoice_id, invoice_status, invoice_status_details, invoice_product, invoice_description, invoice_amount, invoice_currency_code, invoice_due, invoice_created_at, invoice_context, payment_id, payment_status, payment_status_failure_code, payment_status_failure_description, payment_amount, payment_currency_code, payment_fee, payment_tool, payment_masked_pan, payment_bin, payment_token, payment_system, payment_session_id, payment_country_id, payment_city_id, payment_ip, payment_phone_number, payment_email, payment_fingerprint, payment_created_at, payment_context) VALUES (3, 'INVOICE', 'INVOICE_CREATED', '2016-10-25 15:16:05.977236', '74480e4f-1a36-4edd-8175-7a9e984313b0', null, 1, null, null, null, null, null, null, null, null, 'l6KYVmwvgW', 'unpaid', null, '55', '1x phone, 3x Дакимакура', 1775000, 'RUB', '2016-10-26 12:16:05.000000', '2016-10-25 15:16:05.977236', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO mst.invoice_event_stat (event_id, event_category, event_type, event_created_at, party_id, party_email, party_shop_id, party_shop_name, party_shop_description, party_shop_url, party_shop_category_id, party_shop_payout_tool_id, party_contract_id, party_contract_registered_number, party_contract_inn, invoice_id, invoice_status, invoice_status_details, invoice_product, invoice_description, invoice_amount, invoice_currency_code, invoice_due, invoice_created_at, invoice_context, payment_id, payment_status, payment_status_failure_code, payment_status_failure_description, payment_amount, payment_currency_code, payment_fee, payment_tool, payment_masked_pan, payment_bin, payment_token, payment_system, payment_session_id, payment_country_id, payment_city_id, payment_ip, payment_phone_number, payment_email, payment_fingerprint, payment_created_at, payment_context) VALUES (4, 'INVOICE', 'INVOICE_CREATED', '2016-10-25 15:42:22.738495', '74480e4f-1a36-4edd-8175-7a9e984313b0', null, 1, null, null, null, null, null, null, null, null, 'l6MKIV4tZQ', 'paid', null, '56', '1x phone, 4x Дакимакура', 2366000, 'RUB', '2016-10-26 12:40:52.000000', '2016-10-25 15:40:52.714380', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO mst.invoice_event_stat (event_id, event_category, event_type, event_created_at, party_id, party_email, party_shop_id, party_shop_name, party_shop_description, party_shop_url, party_shop_category_id, party_shop_payout_tool_id, party_contract_id, party_contract_registered_number, party_contract_inn, invoice_id, invoice_status, invoice_status_details, invoice_product, invoice_description, invoice_amount, invoice_currency_code, invoice_due, invoice_created_at, invoice_context, payment_id, payment_status, payment_status_failure_code, payment_status_failure_description, payment_amount, payment_currency_code, payment_fee, payment_tool, payment_masked_pan, payment_bin, payment_token, payment_system, payment_session_id, payment_country_id, payment_city_id, payment_ip, payment_phone_number, payment_email, payment_fingerprint, payment_created_at, payment_context) VALUES (13, 'INVOICE', 'INVOICE_CREATED', '2016-10-25 15:45:36.171399', '74480e4f-1a36-4edd-8175-7a9e984313b0', null, 1, null, null, null, null, null, null, null, null, 'l6MdTNgPku', 'paid', null, '57', '1x Пикачу шляпа', 444000, 'RUB', '2016-10-26 12:45:12.000000', '2016-10-25 15:45:12.382847', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO mst.invoice_event_stat (event_id, event_category, event_type, event_created_at, party_id, party_email, party_shop_id, party_shop_name, party_shop_description, party_shop_url, party_shop_category_id, party_shop_payout_tool_id, party_contract_id, party_contract_registered_number, party_contract_inn, invoice_id, invoice_status, invoice_status_details, invoice_product, invoice_description, invoice_amount, invoice_currency_code, invoice_due, invoice_created_at, invoice_context, payment_id, payment_status, payment_status_failure_code, payment_status_failure_description, payment_amount, payment_currency_code, payment_fee, payment_tool, payment_masked_pan, payment_bin, payment_token, payment_system, payment_session_id, payment_country_id, payment_city_id, payment_ip, payment_phone_number, payment_email, payment_fingerprint, payment_created_at, payment_context) VALUES (22, 'INVOICE', 'INVOICE_CREATED', '2016-10-25 15:48:47.650962', '74480e4f-1a36-4edd-8175-7a9e984313b0', null, 1, null, null, null, null, null, null, null, null, 'l6MpE5OUvg', 'paid', null, '58', '1x Переноска для картошки (белорусская)', 540100, 'RUB', '2016-10-26 12:47:51.000000', '2016-10-25 15:47:51.543270', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO mst.invoice_event_stat (event_id, event_category, event_type, event_created_at, party_id, party_email, party_shop_id, party_shop_name, party_shop_description, party_shop_url, party_shop_category_id, party_shop_payout_tool_id, party_contract_id, party_contract_registered_number, party_contract_inn, invoice_id, invoice_status, invoice_status_details, invoice_product, invoice_description, invoice_amount, invoice_currency_code, invoice_due, invoice_created_at, invoice_context, payment_id, payment_status, payment_status_failure_code, payment_status_failure_description, payment_amount, payment_currency_code, payment_fee, payment_tool, payment_masked_pan, payment_bin, payment_token, payment_system, payment_session_id, payment_country_id, payment_city_id, payment_ip, payment_phone_number, payment_email, payment_fingerprint, payment_created_at, payment_context) VALUES (34, 'INVOICE', 'INVOICE_CREATED', '2016-10-25 16:00:43.948338', '74480e4f-1a36-4edd-8175-7a9e984313b0', null, 1, null, null, null, null, null, null, null, null, 'l6Nhd36Msq', 'paid', null, '59', '1x Телескопический полностью автоматический пулемет род C-03', 2500100, 'RUB', '2016-10-26 13:00:08.000000', '2016-10-25 16:00:08.284518', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO mst.invoice_event_stat (event_id, event_category, event_type, event_created_at, party_id, party_email, party_shop_id, party_shop_name, party_shop_description, party_shop_url, party_shop_category_id, party_shop_payout_tool_id, party_contract_id, party_contract_registered_number, party_contract_inn, invoice_id, invoice_status, invoice_status_details, invoice_product, invoice_description, invoice_amount, invoice_currency_code, invoice_due, invoice_created_at, invoice_context, payment_id, payment_status, payment_status_failure_code, payment_status_failure_description, payment_amount, payment_currency_code, payment_fee, payment_tool, payment_masked_pan, payment_bin, payment_token, payment_system, payment_session_id, payment_country_id, payment_city_id, payment_ip, payment_phone_number, payment_email, payment_fingerprint, payment_created_at, payment_context) VALUES (55, 'INVOICE', 'INVOICE_CREATED', '2016-10-25 16:39:48.293729', '74480e4f-1a36-4edd-8175-7a9e984313b0', null, 1, null, null, null, null, null, null, null, null, 'l6QVpaDFtw', 'paid', null, '61', '1x Дакимакура', 591000, 'RUB', '2016-10-26 13:39:27.000000', '2016-10-25 16:39:27.379576', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO mst.invoice_event_stat (event_id, event_category, event_type, event_created_at, party_id, party_email, party_shop_id, party_shop_name, party_shop_description, party_shop_url, party_shop_category_id, party_shop_payout_tool_id, party_contract_id, party_contract_registered_number, party_contract_inn, invoice_id, invoice_status, invoice_status_details, invoice_product, invoice_description, invoice_amount, invoice_currency_code, invoice_due, invoice_created_at, invoice_context, payment_id, payment_status, payment_status_failure_code, payment_status_failure_description, payment_amount, payment_currency_code, payment_fee, payment_tool, payment_masked_pan, payment_bin, payment_token, payment_system, payment_session_id, payment_country_id, payment_city_id, payment_ip, payment_phone_number, payment_email, payment_fingerprint, payment_created_at, payment_context) VALUES (63, 'INVOICE', 'INVOICE_CREATED', '2016-10-25 16:40:38.791082', '74480e4f-1a36-4edd-8175-7a9e984313b0', null, 1, null, null, null, null, null, null, null, null, 'l6Qb6YPVRY', 'unpaid', null, '62', '1x Дакимакура', 591000, 'RUB', '2016-10-26 13:40:38.000000', '2016-10-25 16:40:38.791082', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO mst.invoice_event_stat (event_id, event_category, event_type, event_created_at, party_id, party_email, party_shop_id, party_shop_name, party_shop_description, party_shop_url, party_shop_category_id, party_shop_payout_tool_id, party_contract_id, party_contract_registered_number, party_contract_inn, invoice_id, invoice_status, invoice_status_details, invoice_product, invoice_description, invoice_amount, invoice_currency_code, invoice_due, invoice_created_at, invoice_context, payment_id, payment_status, payment_status_failure_code, payment_status_failure_description, payment_amount, payment_currency_code, payment_fee, payment_tool, payment_masked_pan, payment_bin, payment_token, payment_system, payment_session_id, payment_country_id, payment_city_id, payment_ip, payment_phone_number, payment_email, payment_fingerprint, payment_created_at, payment_context) VALUES (43, 'INVOICE', 'INVOICE_CREATED', '2016-12-20 18:04:49.050000', '74480e4f-1a36-4edd-8175-7a9e984313b0', null, 1, null, null, null, null, null, null, null, null, 'l6Op9zzxtw', 'paid', null, '60', '1x Дубинка резиновая', 450000, 'RUB', '2016-10-26 13:15:49.000000', '2016-10-25 16:15:49.884332', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

INSERT INTO mst.invoice_event_stat (event_id, event_category, event_type, event_created_at, party_id, party_email, party_shop_id, party_shop_name, party_shop_description, party_shop_url, party_shop_category_id, party_shop_payout_tool_id, party_contract_id, party_contract_registered_number, party_contract_inn, invoice_id, invoice_status, invoice_status_details, invoice_product, invoice_description, invoice_amount, invoice_currency_code, invoice_due, invoice_created_at, invoice_context, payment_id, payment_status, payment_status_failure_code, payment_status_failure_description, payment_amount, payment_currency_code, payment_fee, payment_tool, payment_masked_pan, payment_bin, payment_token, payment_system, payment_session_id, payment_country_id, payment_city_id, payment_ip, payment_phone_number, payment_email, payment_fingerprint, payment_created_at, payment_context) VALUES (5, 'PAYMENT', 'INVOICE_PAYMENT_STARTED', '2016-10-25 15:42:22.738495', '74480e4f-1a36-4edd-8175-7a9e984313b0', null, 1, null, null, null, null, null, null, null, null, 'l6MKIV4tZQ', 'paid', null, '56', '1x phone, 4x Дакимакура', 2366000, 'RUB', '2016-10-26 12:40:52.000000', '2016-10-25 15:40:52.714380', null, '1', 'captured', null, null, 2366000, 'RUB', 106470, 'bank_card', '0021', '220138', '62QQ0GffRaVvrb1XgHf571', 'nspkmir', '32ylS9JXIbMkLAVfjydyx7', -1, -1, '172.17.0.11', null, '', 'c647166641620c768f03977aea4e79f6', '2016-10-25 15:42:19.002604', null);
INSERT INTO mst.invoice_event_stat (event_id, event_category, event_type, event_created_at, party_id, party_email, party_shop_id, party_shop_name, party_shop_description, party_shop_url, party_shop_category_id, party_shop_payout_tool_id, party_contract_id, party_contract_registered_number, party_contract_inn, invoice_id, invoice_status, invoice_status_details, invoice_product, invoice_description, invoice_amount, invoice_currency_code, invoice_due, invoice_created_at, invoice_context, payment_id, payment_status, payment_status_failure_code, payment_status_failure_description, payment_amount, payment_currency_code, payment_fee, payment_tool, payment_masked_pan, payment_bin, payment_token, payment_system, payment_session_id, payment_country_id, payment_city_id, payment_ip, payment_phone_number, payment_email, payment_fingerprint, payment_created_at, payment_context) VALUES (14, 'PAYMENT', 'INVOICE_PAYMENT_STARTED', '2016-10-25 15:45:36.171399', '74480e4f-1a36-4edd-8175-7a9e984313b0', null, 1, null, null, null, null, null, null, null, null, 'l6MdTNgPku', 'paid', null, '57', '1x Пикачу шляпа', 444000, 'RUB', '2016-10-26 12:45:12.000000', '2016-10-25 15:45:12.382847', null, '1', 'captured', null, null, 444000, 'RUB', 19980, 'bank_card', '0021', '220138', '62QQ0GffRaVvrb1XgHf571', 'nspkmir', '3TuZfSUwMCIdaZGlE2tSs0', -1, -1, '172.17.0.11', null, 'i.galeev@rbkmoney.com', '90b3bd52129ff2a40277445e02b85df3', '2016-10-25 15:45:32.456564', null);
INSERT INTO mst.invoice_event_stat (event_id, event_category, event_type, event_created_at, party_id, party_email, party_shop_id, party_shop_name, party_shop_description, party_shop_url, party_shop_category_id, party_shop_payout_tool_id, party_contract_id, party_contract_registered_number, party_contract_inn, invoice_id, invoice_status, invoice_status_details, invoice_product, invoice_description, invoice_amount, invoice_currency_code, invoice_due, invoice_created_at, invoice_context, payment_id, payment_status, payment_status_failure_code, payment_status_failure_description, payment_amount, payment_currency_code, payment_fee, payment_tool, payment_masked_pan, payment_bin, payment_token, payment_system, payment_session_id, payment_country_id, payment_city_id, payment_ip, payment_phone_number, payment_email, payment_fingerprint, payment_created_at, payment_context) VALUES (23, 'PAYMENT', 'INVOICE_PAYMENT_STARTED', '2016-10-25 15:48:47.650962', '74480e4f-1a36-4edd-8175-7a9e984313b0', null, 1, null, null, null, null, null, null, null, null, 'l6MpE5OUvg', 'paid', null, '58', '1x Переноска для картошки (белорусская)', 540100, 'RUB', '2016-10-26 12:47:51.000000', '2016-10-25 15:47:51.543270', null, '1', 'failed', '302', 'External decline', 540100, 'RUB', 24304, 'bank_card', '0021', '220138', '62QQ0GffRaVvrb1XgHf571', 'nspkmir', '2iKExJIq4a1ukJvhqIAD2d', -1, -1, '172.17.0.11', null, '', '90b3bd52129ff2a40277445e02b85df3', '2016-10-25 15:48:10.277519', null);
INSERT INTO mst.invoice_event_stat (event_id, event_category, event_type, event_created_at, party_id, party_email, party_shop_id, party_shop_name, party_shop_description, party_shop_url, party_shop_category_id, party_shop_payout_tool_id, party_contract_id, party_contract_registered_number, party_contract_inn, invoice_id, invoice_status, invoice_status_details, invoice_product, invoice_description, invoice_amount, invoice_currency_code, invoice_due, invoice_created_at, invoice_context, payment_id, payment_status, payment_status_failure_code, payment_status_failure_description, payment_amount, payment_currency_code, payment_fee, payment_tool, payment_masked_pan, payment_bin, payment_token, payment_system, payment_session_id, payment_country_id, payment_city_id, payment_ip, payment_phone_number, payment_email, payment_fingerprint, payment_created_at, payment_context) VALUES (26, 'PAYMENT', 'INVOICE_PAYMENT_STARTED', '2016-10-25 15:48:47.650962', '74480e4f-1a36-4edd-8175-7a9e984313b0', null, 2, null, null, null, null, null, null, null, null, 'l6MpE5OUvg', 'paid', null, '58', '1x Переноска для картошки (белорусская)', 540100, 'RUB', '2016-10-26 12:47:51.000000', '2016-10-25 15:47:51.543270', null, '2', 'captured', null, null, 540100, 'RUB', 24304, 'bank_card', '0021', '220138', '62QQ0GffRaVvrb1XgHf571', 'nspkmir', '6A9ZRgyyKyKyzf3h7ZEYcq', -1, -1, '172.17.0.11', null, '', '90b3bd52129ff2a40277445e02b85df3', '2016-10-25 15:48:44.234858', null);
INSERT INTO mst.invoice_event_stat (event_id, event_category, event_type, event_created_at, party_id, party_email, party_shop_id, party_shop_name, party_shop_description, party_shop_url, party_shop_category_id, party_shop_payout_tool_id, party_contract_id, party_contract_registered_number, party_contract_inn, invoice_id, invoice_status, invoice_status_details, invoice_product, invoice_description, invoice_amount, invoice_currency_code, invoice_due, invoice_created_at, invoice_context, payment_id, payment_status, payment_status_failure_code, payment_status_failure_description, payment_amount, payment_currency_code, payment_fee, payment_tool, payment_masked_pan, payment_bin, payment_token, payment_system, payment_session_id, payment_country_id, payment_city_id, payment_ip, payment_phone_number, payment_email, payment_fingerprint, payment_created_at, payment_context) VALUES (35, 'PAYMENT', 'INVOICE_PAYMENT_STARTED', '2016-10-25 16:00:43.948338', '74480e4f-1a36-4edd-8175-7a9e984313b0', null, 2, null, null, null, null, null, null, null, null, 'l6Nhd36Msq', 'paid', null, '59', '1x Телескопический полностью автоматический пулемет род C-03', 2500100, 'RUB', '2016-10-26 13:00:08.000000', '2016-10-25 16:00:08.284518', null, '1', 'captured', null, null, 2500100, 'RUB', 112504, 'bank_card', '0021', '220138', '62QQ0GffRaVvrb1XgHf571', 'nspkmir', '6FJaPnLBSkCsGyFagqExrY', -1, -1, '172.17.0.11', null, '', '90b3bd52129ff2a40277445e02b85df3', '2016-10-25 16:00:40.533016', null);
INSERT INTO mst.invoice_event_stat (event_id, event_category, event_type, event_created_at, party_id, party_email, party_shop_id, party_shop_name, party_shop_description, party_shop_url, party_shop_category_id, party_shop_payout_tool_id, party_contract_id, party_contract_registered_number, party_contract_inn, invoice_id, invoice_status, invoice_status_details, invoice_product, invoice_description, invoice_amount, invoice_currency_code, invoice_due, invoice_created_at, invoice_context, payment_id, payment_status, payment_status_failure_code, payment_status_failure_description, payment_amount, payment_currency_code, payment_fee, payment_tool, payment_masked_pan, payment_bin, payment_token, payment_system, payment_session_id, payment_country_id, payment_city_id, payment_ip, payment_phone_number, payment_email, payment_fingerprint, payment_created_at, payment_context) VALUES (44, 'PAYMENT', 'INVOICE_PAYMENT_STARTED', '2016-12-20 18:04:49.050000', '74480e4f-1a36-4edd-8175-7a9e984313b0', null, 3, null, null, null, null, null, null, null, null, 'l6Op9zzxtw', 'paid', null, '60', '1x Дубинка резиновая', 450000, 'RUB', '2016-10-26 13:15:49.000000', '2016-10-25 16:15:49.884332', null, '1', 'captured', null, null, 450000, 'RUB', 20250, 'bank_card', '0777', '430000', 'CmeArO8tUlk6TErU9ejEB', 'visa', '3k3dbSXMn20UdnU7qhggDg', -1, -1, '172.17.0.11', null, '', '90b3bd52129ff2a40277445e02b85df3', '2016-10-25 16:16:25.022311', null);
INSERT INTO mst.invoice_event_stat (event_id, event_category, event_type, event_created_at, party_id, party_email, party_shop_id, party_shop_name, party_shop_description, party_shop_url, party_shop_category_id, party_shop_payout_tool_id, party_contract_id, party_contract_registered_number, party_contract_inn, invoice_id, invoice_status, invoice_status_details, invoice_product, invoice_description, invoice_amount, invoice_currency_code, invoice_due, invoice_created_at, invoice_context, payment_id, payment_status, payment_status_failure_code, payment_status_failure_description, payment_amount, payment_currency_code, payment_fee, payment_tool, payment_masked_pan, payment_bin, payment_token, payment_system, payment_session_id, payment_country_id, payment_city_id, payment_ip, payment_phone_number, payment_email, payment_fingerprint, payment_created_at, payment_context) VALUES (56, 'PAYMENT', 'INVOICE_PAYMENT_STARTED', '2016-10-25 16:39:48.293729', '74480e4f-1a36-4edd-8175-7a9e984313b0', null, 2, null, null, null, null, null, null, null, null, 'l6QVpaDFtw', 'paid', null, '61', '1x Дакимакура', 591000, 'RUB', '2016-10-26 13:39:27.000000', '2016-10-25 16:39:27.379576', null, '1', 'captured', null, null, 591000, 'RUB', 26595, 'bank_card', '0777', '430000', 'CmeArO8tUlk6TErU9ejEB', 'visa', '67h8ouSQ5XJpFQE5mnmuVf', -1, -1, '172.17.0.11', null, '', 'd0ad26fa4879e688b591633ac80ecd85', '2016-10-25 16:39:45.810125', null);
