alter table mst.adjustment add column adjustment_amount bigint;
alter table mst.adjustment add column adjustment_currency_code character varying;

update mst.adjustment
set adjustment_amount = payment_event.payment_fee - adjustment_fee
from mst.payment_event
where adjustment.invoice_id = payment_event.invoice_id
      and adjustment.payment_id = payment_event.payment_id
      and payment_event.payment_status = 'captured'::mst.invoice_payment_status;

update mst.adjustment
set adjustment_currency_code = payment_data.payment_currency_code
from mst.payment_data
where adjustment.invoice_id = payment_data.invoice_id
  and adjustment.payment_id = payment_data.payment_id;

alter table mst.adjustment alter column adjustment_amount set not null;
alter table mst.adjustment alter column adjustment_currency_code set not null;

alter table mst.adjustment drop column adjustment_fee;
alter table mst.adjustment drop column adjustment_provider_fee;
alter table mst.adjustment drop column adjustment_external_fee;