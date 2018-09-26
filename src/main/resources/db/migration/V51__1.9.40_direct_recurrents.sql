ALTER TABLE mst.payment_data
  ADD COLUMN payment_make_recurrent_flag BOOLEAN;
ALTER TABLE mst.payment_data
  ADD COLUMN payment_recurrent_payer_parent_invoice_id CHARACTER VARYING;
ALTER TABLE mst.payment_data
  ADD COLUMN payment_recurrent_payer_parent_payment_id CHARACTER VARYING;
