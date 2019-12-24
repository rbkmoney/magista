/** Add new indexes for the payments table */
CREATE INDEX CONCURRENTLY IF NOT EXISTS payment_data_status_by_created_date_idx
    ON mst.payment_data USING BTREE (party_id, payment_created_at, payment_status);

CREATE INDEX CONCURRENTLY IF NOT EXISTS payment_data_email_by_created_date_idx
    ON mst.payment_data USING BTREE (party_id, payment_created_at, payment_email)
    WHERE payment_email IS NOT NULL;

CREATE INDEX CONCURRENTLY IF NOT EXISTS payment_data_fingerprint_by_created_date_idx
    ON mst.payment_data USING BTREE (party_id, payment_created_at, payment_fingerprint)
    WHERE payment_fingerprint IS NOT NULL;

CREATE INDEX CONCURRENTLY IF NOT EXISTS payment_data_payment_ip_by_created_date_idx
    ON mst.payment_data USING BTREE (party_id, payment_created_at, payment_ip)
    WHERE payment_ip IS NOT NULL;

CREATE INDEX CONCURRENTLY IF NOT EXISTS payment_data_payment_customer_id_by_created_date_idx
    ON mst.payment_data USING BTREE (party_id, payment_created_at, payment_customer_id)
    WHERE payment_customer_id IS NOT NULL;

CREATE INDEX CONCURRENTLY IF NOT EXISTS payment_data_payment_amount_by_created_date_idx
    ON mst.payment_data USING BTREE (party_id, payment_created_at, payment_amount);

CREATE INDEX CONCURRENTLY IF NOT EXISTS payment_data_flow_by_created_date_idx
    ON mst.payment_data USING BTREE (party_id, payment_created_at, payment_flow);

CREATE INDEX CONCURRENTLY IF NOT EXISTS payment_data_payment_tool_card_system_by_created_date_idx
    ON mst.payment_data USING BTREE (party_id, payment_created_at, payment_tool, payment_bank_card_system);

CREATE INDEX CONCURRENTLY IF NOT EXISTS payment_data_payment_tool_card_token_prov_by_created_date_idx
    ON mst.payment_data USING BTREE (party_id, payment_created_at, payment_tool, payment_bank_card_token_provider)
    WHERE payment_tool = 'bank_card' AND payment_bank_card_token_provider IS NOT NULL;

CREATE INDEX CONCURRENTLY IF NOT EXISTS payment_data_payment_tool_terminal_prov_by_created_date_idx
    ON mst.payment_data USING BTREE (party_id, payment_created_at, payment_tool, payment_terminal_provider)
    WHERE payment_tool = 'payment_terminal' AND payment_terminal_provider IS NOT NULL;
