DO
$$
    BEGIN
        IF NOT exists(SELECT constraint_name
                      FROM information_schema.table_constraints
                      WHERE table_name = 'refund_data'
                        AND table_schema = 'mst'
                        AND constraint_type = 'PRIMARY KEY') THEN
            CREATE UNIQUE INDEX IF NOT EXISTS refund_data_new_pkey on mst.refund_data (id);
            ALTER TABLE mst.refund_data
                DROP CONSTRAINT IF EXISTS refund_data_pkey;
            ALTER TABLE mst.refund_data
                ADD PRIMARY KEY USING INDEX refund_data_new_pkey;
            ALTER INDEX refund_data_new_pkey RENAME TO refund_data_pkey;
        END IF;
    END
$$;
