create schema if not exists mst;

create type invoice_event_category as enum ('INVOICE', 'PAYMENT', 'REFUND', 'ADJUSTMENT');

create type invoice_payment_status as enum ('pending', 'processed', 'captured', 'cancelled', 'failed', 'refunded');

create type invoice_status as enum ('unpaid', 'paid', 'cancelled', 'fulfilled');

create type adjustment_status as enum ('pending', 'captured', 'cancelled');

create type invoice_event_type as enum ('INVOICE_CREATED', 'INVOICE_STATUS_CHANGED', 'INVOICE_PAYMENT_STARTED', 'INVOICE_PAYMENT_STATUS_CHANGED', 'INVOICE_PAYMENT_ADJUSTMENT_CREATED', 'INVOICE_PAYMENT_ADJUSTMENT_STATUS_CHANGED', 'INVOICE_PAYMENT_REFUND_CREATED', 'INVOICE_PAYMENT_REFUND_STATUS_CHANGED', 'INVOICE_PAYMENT_ADJUSTED', 'PAYMENT_TERMINAL_RECIEPT', 'INVOICE_PAYMENT_ROUTE_CHANGED', 'INVOICE_PAYMENT_CASH_FLOW_CHANGED');

create type refund_status as enum ('pending', 'succeeded', 'failed');

create type payout_event_type as enum ('PAYOUT_CREATED', 'PAYOUT_STATUS_CHANGED');

create type payout_status as enum ('unpaid', 'paid', 'cancelled', 'confirmed');

create type payout_type as enum ('bank_account', 'wallet');

create type payout_account_type as enum ('RUSSIAN_PAYOUT_ACCOUNT', 'INTERNATIONAL_PAYOUT_ACCOUNT');

create type failure_class as enum ('operation_timeout', 'failure');

create type bank_card_token_provider as enum ('applepay', 'googlepay', 'samsungpay');

create type payment_tool as enum ('bank_card', 'payment_terminal', 'digital_wallet');

create type payment_flow as enum ('instant', 'hold');

create type on_hold_expiration as enum ('cancel', 'capture');

create type payment_payer_type as enum ('payment_resource', 'customer', 'recurrent');

create table payout_data
(
  id bigserial not null,
  event_id bigint not null,
  event_type mst.payout_event_type not null,
  event_created_at timestamp without time zone not null,
  party_id character varying not null,
  party_shop_id character varying not null,
  payout_id character varying not null,
  payout_created_at timestamp without time zone not null,
  payout_status mst.payout_status not null,
  payout_amount bigint default 0 not null,
  payout_fee bigint default 0,
  payout_currency_code character varying not null,
  payout_type mst.payout_type not null,
  payout_card_token character varying,
  payout_card_masked_pan character varying,
  payout_card_bin character varying,
  payout_card_payment_system character varying,
  payout_account_bank_id character varying,
  payout_account_bank_corr_id character varying,
  payout_account_bank_local_code character varying,
  payout_account_bank_name character varying,
  payout_account_inn character varying,
  payout_account_legal_agreement_id character varying,
  payout_account_legal_agreement_signed_at timestamp without time zone,
  payout_account_purpose character varying,
  payout_cancel_details character varying,
  payout_account_legal_name character varying,
  payout_account_trading_name character varying,
  payout_account_registered_address character varying,
  payout_account_actual_address character varying,
  payout_account_registered_number character varying,
  payout_account_bank_address character varying,
  payout_account_bank_iban character varying,
  payout_account_bank_bic character varying,
  payout_account_type mst.payout_account_type,
  payout_summary character varying,
  payout_account_bank_number character varying,
  payout_account_bank_aba_rtn character varying,
  payout_account_bank_country_code character varying,
  payout_international_correspondent_account_bank_number character varying,
  payout_international_correspondent_account_bank_account character varying,
  payout_international_correspondent_account_bank_name character varying,
  payout_international_correspondent_account_bank_address character varying,
  payout_international_correspondent_account_bank_bic character varying,
  payout_international_correspondent_account_bank_iban character varying,
  payout_international_correspondent_account_bank_aba_rtn character varying,
  payout_international_correspondent_account_bank_country_code character varying,
  payout_wallet_id character varying,
  constraint payout_data_pkey primary key (id),
  constraint payout_data_ukey unique (payout_id)
);

create table refund_data
(
  id bigserial not null,
  event_id bigint not null,
  event_created_at timestamp without time zone not null,
  event_type mst.invoice_event_type not null,
  invoice_id character varying not null,
  payment_id character varying not null,
  refund_id character varying not null,
  party_id character varying not null,
  party_shop_id character varying not null,
  refund_status mst.refund_status not null,
  refund_operation_failure_class mst.failure_class,
  refund_external_failure character varying,
  refund_external_failure_reason character varying,
  refund_created_at timestamp without time zone not null,
  refund_reason character varying,
  refund_currency_code character varying not null,
  refund_amount bigint not null,
  refund_fee bigint default 0 not null,
  refund_provider_fee bigint default 0 not null,
  refund_external_fee bigint default 0 not null,
  refund_domain_revision bigint,
  constraint refund_data_pkey unique (id),
  constraint refund_data_ukey unique (invoice_id, payment_id, refund_id)
);

create table adjustment_data
(
  id bigserial not null,
  event_id bigint not null,
  event_created_at timestamp without time zone not null,
  event_type mst.invoice_event_type not null,
  invoice_id character varying not null,
  payment_id character varying not null,
  adjustment_id character varying not null,
  party_id character varying not null,
  party_shop_id character varying not null,
  adjustment_status mst.adjustment_status not null,
  adjustment_status_created_at timestamp without time zone,
  adjustment_created_at timestamp without time zone not null,
  adjustment_reason character varying not null,
  adjustment_fee bigint default 0 not null,
  adjustment_provider_fee bigint default 0 not null,
  adjustment_external_fee bigint default 0 not null,
  adjustment_domain_revision bigint,
  constraint adjustment_data_pkey primary key (id),
  constraint adjustment_data_ukey unique (invoice_id, payment_id, adjustment_id)
);


create table invoice_data
(
  id bigserial not null,
  party_id uuid not null,
  party_shop_id character varying not null,
  event_id bigint not null,
  event_created_at timestamp not null,
  event_type mst.invoice_event_type not null,
  invoice_id character varying not null,
  invoice_product character varying not null,
  invoice_description varchar,
  invoice_amount bigint not null,
  invoice_currency_code varchar not null,
  invoice_due timestamp not null,
  invoice_created_at timestamp not null,
  invoice_status mst.invoice_status not null,
  invoice_status_details varchar,
  invoice_party_revision bigint,
  invoice_template_id varchar,
  invoice_cart_json varchar,
  invoice_context_type varchar,
  invoice_context bytea,
  constraint invoice_data_pkey primary key (id),
  constraint invoice_data_ukey unique (invoice_id)
);

create index invoice_data_invoice_created_at_idx
  on invoice_data (invoice_created_at);

-- create index invoice_event_invoice_id_event_created_at_idx
--   on invoice_event (invoice_id, event_created_at);

create table payment_data
(
  id bigserial not null,
  invoice_id character varying not null,
  payment_id character varying not null,
  party_id uuid not null,
  party_shop_id character varying not null,
  event_id bigint not null,
  event_created_at timestamp not null,
  event_type mst.invoice_event_type not null,
  payment_currency_code character varying not null,
  payment_origin_amount bigint not null,
  payment_amount bigint not null,
  payment_status mst.invoice_payment_status not null,
  payment_operation_failure_class mst.failure_class,
  payment_external_failure character varying,
  payment_external_failure_reason character varying,
  payment_fee bigint,
  payment_provider_fee bigint,
  payment_external_fee bigint,
  payment_domain_revision bigint not null,
  payment_short_id character varying,
  payment_provider_id integer,
  payment_terminal_id integer,
  payment_customer_id character varying,
  payment_tool mst.payment_tool not null,
  payment_bank_card_masked_pan character varying,
  payment_bank_card_bin character varying,
  payment_bank_card_token character varying,
  payment_bank_card_system character varying,
  payment_bank_card_token_provider mst.bank_card_token_provider,
  payment_terminal_provider character varying,
  payment_digital_wallet_id character varying,
  payment_digital_wallet_provider character varying,
  payment_flow mst.payment_flow not null,
  payment_hold_on_expiration mst.on_hold_expiration,
  payment_hold_until timestamp without time zone,
  payment_session_id character varying,
  payment_fingerprint character varying,
  payment_ip character varying,
  payment_phone_number character varying,
  payment_email character varying,
  payment_created_at timestamp without time zone not null,
  payment_party_revision bigint,
  payment_context_type character varying,
  payment_context bytea,
  payment_make_recurrent_flag boolean,
  payment_recurrent_payer_parent_invoice_id character varying,
  payment_recurrent_payer_parent_payment_id character varying,
  payment_payer_type mst.payment_payer_type not null,
  payment_country_id integer,
  payment_city_id integer,
  constraint payment_data_pkey primary key (id),
  constraint payment_data_ukey unique (invoice_id, payment_id)
);

create index payment_data_payment_created_at_idx
  on payment_data (payment_created_at);

-- create index payment_event_invoice_id_payment_id_event_created_at_idx
--   on payment_event (invoice_id, payment_id, event_created_at);

