alter table mst.payment_event drop constraint payment_event_invoice_id_fkey;
alter table mst.payment_data drop constraint payment_data_pkey;
alter table mst.payment_data drop constraint payment_data_invoice_id_fkey;
alter table mst.invoice_event drop constraint invoice_event_invoice_id_fkey;
alter table mst.invoice_data drop constraint invoice_data_pkey;

alter table mst.invoice_data add constraint invoice_data_pkey primary key (id);
alter table mst.invoice_data add constraint invoice_data_ukey unique (invoice_id);
alter table mst.payment_data add constraint payment_data_pkey primary key (id);
alter table mst.payment_data add constraint payment_data_ukey unique (invoice_id, payment_id);