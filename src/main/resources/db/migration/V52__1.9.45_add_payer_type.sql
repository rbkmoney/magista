create type mst.payment_payer_type as enum ('payment_resource', 'customer', 'recurrent');

alter table mst.payment_data add column payment_payer_type mst.payment_payer_type;
update mst.payment_data set payment_payer_type = 'customer' where payment_customer_id is not null;
update mst.payment_data set payment_payer_type = 'recurrent' where payment_recurrent_payer_parent_invoice_id is not null and payment_recurrent_payer_parent_payment_id is not null;
update mst.payment_data set payment_payer_type = 'payment_resource' where payment_payer_type is null;
alter table mst.payment_data alter column payment_payer_type set not null;