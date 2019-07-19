drop index if exists refund_ukey;
drop index if exists adjustment_ukey;
drop index if exists invoice_event_ukey;
drop index if exists payment_event_ukey;

alter table mst.refund add constraint refund_new_ukey unique (invoice_id, payment_id, refund_id, event_id, event_type, refund_status);
alter table mst.adjustment add constraint adjustment_new_ukey unique (invoice_id, payment_id, adjustment_id, event_id, event_type, adjustment_status);
alter table mst.invoice_event add constraint invoice_event_new_ukey unique (invoice_id, event_id, event_type, invoice_status);
alter table mst.payment_event add constraint payment_event_new_ukey unique (invoice_id, payment_id, event_id, event_type, payment_status);
