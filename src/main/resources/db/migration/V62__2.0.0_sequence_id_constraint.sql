alter table mst.invoice_event drop constraint invoice_event_ukey;
alter table mst.invoice_event add constraint invoice_event_ukey unique (invoice_id);

alter table mst.payment_event drop constraint payment_event_ukey;
alter table mst.payment_event add constraint payment_event_ukey unique (invoice_id, payment_id);

alter table mst.refund drop constraint refund_ukey;
alter table mst.refund add constraint refund_ukey unique (invoice_id, payment_id, refund_id);

alter table mst.adjustment drop constraint adjustment_ukey;
alter table mst.adjustment add constraint adjustment_ukey unique (invoice_id, payment_id, adjustment_id);