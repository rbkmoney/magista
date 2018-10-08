alter table mst.invoice_event add constraint invoice_event_ukey unique (event_id, event_type, invoice_status);
alter table mst.payment_event add constraint payment_event_ukey unique (event_id, event_type, payment_status);
alter table mst.refund add constraint refund_ukey unique (event_id, event_type, refund_status);
alter table mst.adjustment add constraint adjustment_ukey unique (event_id, event_type, adjustment_status);
alter table mst.payout_event_stat add constraint payout_event_stat_ukey unique (event_id, event_type, payout_status);