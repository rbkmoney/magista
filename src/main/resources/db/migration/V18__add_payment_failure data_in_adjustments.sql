ALTER TABLE mst.adjustment_data ADD payment_operation_failure_class mst.failure_class;
ALTER TABLE mst.adjustment_data ADD payment_external_failure character varying;
ALTER TABLE mst.adjustment_data ADD payment_external_failure_reason character varying;
