create type mobile_operator_type as enum ('mts', 'beeline', 'megafone', 'tele2', 'yota');

alter table mst.payment_data add column payment_mobile_operator mst.mobile_operator_type;

alter table mst.payment_data add column payment_mobile_cc character varying;

alter table mst.payment_data add column payment_mobile_ctn character varying;
