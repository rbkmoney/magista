CREATE TYPE mst.FAILURE_CLASS AS ENUM ('operation_timeout', 'failure');

ALTER TABLE mst.invoice_event_stat
  ALTER COLUMN payment_failure_class TYPE mst.FAILURE_CLASS USING payment_failure_class :: TEXT :: mst.FAILURE_CLASS;
ALTER TABLE mst.invoice_event_stat
  RENAME payment_failure_class TO payment_operation_failure_class;

ALTER TABLE mst.refund
  ALTER COLUMN refund_operation_failure_class TYPE mst.FAILURE_CLASS USING refund_operation_failure_class :: TEXT :: mst.FAILURE_CLASS;

ALTER TABLE mst.invoice_event_stat
  RENAME payment_external_failure_code TO payment_external_failure;