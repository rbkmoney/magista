delete from mst.invoice_event_stat WHERE event_id >= (select min(event_id) from mst.adjustment);
delete from mst.invoice_event_stat WHERE event_id >= (select min(event_id) from mst.refund);

ALTER TABLE mst.refund
  ADD COLUMN refund_domain_revision BIGINT NOT NULL;
ALTER TABLE mst.adjustment
  ADD COLUMN adjustment_domain_revision BIGINT NOT NULL;
