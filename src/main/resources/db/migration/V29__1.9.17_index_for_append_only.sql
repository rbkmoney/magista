DROP INDEX mst.invoice_stat_party_id_party_shop_id_payment_created_at_idx;
DROP INDEX mst.invoice_stat_party_id_party_shop_id_invoice_created_at_idx;

CREATE INDEX invoice_stat_event_category_event_created_at_idx
  ON mst.invoice_event_stat USING BTREE (event_category, event_created_at);

CREATE INDEX invoice_stat_event_category_party_id_shop_id_event_created_at_idx
  ON mst.invoice_event_stat USING BTREE (event_category, party_id, party_shop_id, event_created_at);
