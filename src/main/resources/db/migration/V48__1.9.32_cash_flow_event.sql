ALTER TABLE payment_event ALTER payment_fee DROP NOT NULL;
ALTER TABLE payment_event ALTER payment_provider_fee DROP NOT NULL;
ALTER TABLE payment_event ALTER payment_external_fee DROP NOT NULL;
