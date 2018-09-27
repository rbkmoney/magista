create type mst.payment_payer_type as enum ('payment_resource', 'customer', 'recurrent');

alter table mst.payment_data add column payment_payer_type mst.payment_payer_type;
update mst.payment_data set payment_payer_type = 'payment_resource' where payment_session_id is not null;
update mst.payment_data set payment_payer_type = 'customer' where payment_customer_id is not null;
update mst.payment_data set payment_payer_type = 'recurrent' where payment_session_id is null and payment_customer_id is null;
alter table mst.payment_data alter column payment_payer_type set not null;

alter table payment_data add column payment_country_id int;
alter table payment_data add column payment_city_id int;

drop table mst.invoice_event_stat;

alter table mst.invoice_data drop column party_contract_id;
alter table mst.payment_data drop column party_contract_id;
alter table mst.refund drop column party_contract_id;
alter table mst.adjustment drop column party_contract_id;
