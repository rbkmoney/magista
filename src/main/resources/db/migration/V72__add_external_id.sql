alter table mst.invoice_data add column external_id character varying;
alter table mst.payment_data add column external_id character varying;
alter table mst.refund add column external_id character varying;
alter table mst.chargeback_data add column external_id character varying;
