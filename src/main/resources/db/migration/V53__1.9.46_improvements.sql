alter table mst.invoice_data drop column party_contract_id;
alter table mst.payment_data drop column party_contract_id;
alter table mst.refund drop column party_contract_id;
alter table mst.adjustment drop column party_contract_id;

alter table mst.payment_data add column payment_country_id int;
alter table mst.payment_data add column payment_city_id int;

alter table mst.invoice_event_stat drop column payment_country_id;
alter table mst.invoice_event_stat drop column payment_city_id;