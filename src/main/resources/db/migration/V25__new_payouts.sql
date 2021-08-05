create type payout_tool_type as enum ('russian_bank_account', 'international_bank_account', 'wallet_info', 'payment_institution_account');

create table if not exists mst.payout
(
    id                bigserial            not null,
    payout_id         varchar              not null,
    event_created_at  timestamp            not null,
    sequence_id       int                  not null,
    created_at        timestamp            not null,
    party_id          varchar              not null,
    shop_id           varchar              not null,
    status            mst.payout_status    not null,
    payout_tool_id    varchar              not null,
    amount            bigint               not null,
    fee               bigint default 0     not null,
    currency_code     varchar              not null,
    cancelled_details varchar,
    payout_tool_type  payout_tool_type     not null,
    payout_tool_russian_bank_account_account character varying,
    payout_tool_russian_bank_account_bank_name character varying,
    payout_tool_russian_bank_account_bank_post_account character varying,
    payout_tool_russian_bank_account_bank_bik character varying,
    payout_tool_international_bank_account_number character varying,
    payout_tool_international_bank_account_bank_bic character varying,
    payout_tool_international_bank_account_bank_country_code character varying,
    payout_tool_international_bank_account_bank_name character varying,
    payout_tool_international_bank_account_bank_address character varying,
    payout_tool_international_bank_account_bank_aba_rtn character varying,
    payout_tool_international_bank_account_corr_account character varying,
    payout_tool_international_bank_account_iban character varying,
    payout_tool_wallet_id character varying,
    constraint payout_id_pkey primary key (id),
    constraint payout_payout_id_ukey unique (payout_id)
);