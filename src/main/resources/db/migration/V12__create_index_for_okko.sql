CREATE INDEX IF NOT EXISTS payment_data_okko_special
    ON mst.payment_data USING BTREE (party_shop_id, event_created_at)
    WHERE party_shop_id in ('17d2e7eb-250a-4aef-b7a2-6ec1b03b8596',
    'e16574fc-8689-46fc-9962-c39577486796',
    '05191d5d-0e7d-4eb0-8068-12e4cd0b7e31',
    '128e4995-d364-43ef-a1a4-3286809223f4',
    '31637869-9400-4192-b337-06c122a1717a',
    '86193978-37ec-11e8-9a7b-54e1adfb0dee1',
    '4ca8016c-9406-434a-88c0-39309c209fd2',
    '86193978-37ec-11e8-9a7b-54e1adfb0dee');


CREATE INDEX IF NOT EXISTS refund_data_okko_special
    ON mst.refund_data USING BTREE (party_shop_id, event_created_at)
    WHERE party_shop_id in ('17d2e7eb-250a-4aef-b7a2-6ec1b03b8596',
        'e16574fc-8689-46fc-9962-c39577486796',
        '05191d5d-0e7d-4eb0-8068-12e4cd0b7e31',
        '128e4995-d364-43ef-a1a4-3286809223f4',
        '31637869-9400-4192-b337-06c122a1717a',
        '86193978-37ec-11e8-9a7b-54e1adfb0dee1',
        '4ca8016c-9406-434a-88c0-39309c209fd2',
        '86193978-37ec-11e8-9a7b-54e1adfb0dee');
