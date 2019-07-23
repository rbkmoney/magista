alter table mst.refund drop constraint refund_ukey;
alter table mst.adjustment drop constraint adjustment_ukey;
alter table mst.invoice_event drop constraint invoice_event_ukey;
alter table mst.payment_event drop constraint payment_event_ukey;

create unique index if not exists refund_new_ukey on mst.refund using btree (invoice_id, payment_id, refund_id, event_id, event_type, refund_status);
create unique index if not exists adjustment_new_ukey on mst.adjustment using btree (invoice_id, payment_id, adjustment_id, event_id, event_type, adjustment_status);
create unique index if not exists invoice_event_new_ukey on mst.invoice_event using btree (invoice_id, event_id, event_type, invoice_status);
create unique index if not exists payment_event_new_ukey on mst.payment_event using btree (invoice_id, payment_id, event_id, event_type, payment_status);
