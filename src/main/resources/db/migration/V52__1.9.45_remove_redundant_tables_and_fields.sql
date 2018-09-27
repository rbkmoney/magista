drop table mst.invoice_event_stat;

alter table mst.invoice_data drop column party_contract_id;
alter table mst.payment_data drop column party_contract_id;
alter table mst.refund drop column party_contract_id;
alter table mst.adjustment drop column party_contract_id;
