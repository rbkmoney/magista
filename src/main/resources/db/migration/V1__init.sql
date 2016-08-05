create schema if not exists mst;

create table mst.invoice (
  id character varying(64) not null,
  created_at character varying(64) not null,
  constraint invoice_pkey primary key (id)
);

create table mst.payment (
  id character varying(64) not null,
  invoice_id character varying(64) not null,
  created_at character varying(64) not null,
  constraint payment_pkey primary key (id)
);

create table mst.customer (

);

