ALTER TABLE payment_data ADD payment_rrn CHARACTER VARYING;
ALTER TABLE payment_data ADD payment_approval_code CHARACTER VARYING;

CREATE INDEX payment_data_payment_rrn_idx ON payment_data (payment_rrn);
CREATE INDEX payment_data_payment_approval_code_idx ON payment_data (payment_approval_code);
