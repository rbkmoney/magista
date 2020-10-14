DROP INDEX CONCURRENTLY mst.event_party_ms_key;
CREATE INDEX CONCURRENTLY invoice_stat_party_id_party_shop_id_payment_created_at_idx ON "mst"."invoice_event_stat" USING BTREE (party_id, party_shop_id, payment_created_at);