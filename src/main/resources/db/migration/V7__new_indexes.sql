CREATE INDEX IF NOT EXISTS invoice_data_party_id_shop_id_invoice_created_at_idx ON mst.invoice_data USING BTREE (party_id, party_shop_id, invoice_created_at);
CREATE INDEX IF NOT EXISTS payment_data_party_id_shop_id_payment_created_at_idx ON mst.payment_data USING BTREE (party_id, party_shop_id, payment_created_at);
DROP INDEX IF EXISTS payment_data_payment_first6_idx;
CREATE INDEX IF NOT EXISTS payment_data_payment_bank_card_first6_party_shop_id_payment_cre ON mst.payment_data USING BTREE (payment_bank_card_first6, party_shop_id, payment_created_at)
CREATE STATISTICS IF NOT EXISTS payment_data_party_id_party_shop_id (dependencies) ON party_id, party_shop_id FROM mst.payment_data;
CREATE STATISTICS IF NOT EXISTS invoice_data_party_id_party_shop_id (dependencies) ON party_id, party_shop_id FROM mst.invoice_data;
