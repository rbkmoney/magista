create schema if not exists mst;

create table mst.invoice (
  id character varying not null,
  event_id bigint not null,
  merchant_id character varying not null,
  shop_id character varying not null,
  status character varying not null,
  amount bigint not null,
  currency_code character varying not null,
  created_at timestamp without time zone not null,
  changed_at timestamp without time zone not null,
  model character varying not null,
  data character varying not null,
  constraint invoice_pkey primary key (id)
);

create table mst.payment (
  id character varying not null,
  event_id bigint not null,
  invoice_id character varying not null,
  merchant_id character varying not null,
  shop_id character varying not null,
  customer_id character varying,
  masked_pan character varying not null,
  status character varying not null,
  amount bigint not null,
  currency_code character varying not null,
  payment_system character varying not null,
  country_id int not null,
  city_id int not null,
  ip character varying,
  created_at timestamp without time zone not null,
  changed_at timestamp without time zone not null,
  model character varying not null,
  data character varying not null,
  constraint payment_pkey primary key (invoice_id, id)
);

create table mst.customer (
  id character varying not null,
  shop_id character varying not null,
  merchant_id character varying not null,
  created_at timestamp without time zone not null,
  constraint customer_pkey primary key (id, shop_id, merchant_id)
);

