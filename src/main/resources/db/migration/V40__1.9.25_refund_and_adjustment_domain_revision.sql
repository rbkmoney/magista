ALTER TABLE mst.refund
  ADD COLUMN refund_domain_revision BIGINT;
ALTER TABLE mst.adjustment
  ADD COLUMN adjustment_domain_revision BIGINT;
