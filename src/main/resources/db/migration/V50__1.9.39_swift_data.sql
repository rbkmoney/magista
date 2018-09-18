ALTER TABLE mst.payout_event_stat
  ADD COLUMN payout_account_bank_number CHARACTER VARYING;
ALTER TABLE mst.payout_event_stat
  ADD COLUMN payout_account_bank_aba_rtn CHARACTER VARYING;
ALTER TABLE mst.payout_event_stat
  ADD COLUMN payout_account_bank_country_code CHARACTER VARYING;


ALTER TABLE mst.payout_event_stat
  ADD COLUMN payout_international_correspondent_account_bank_number CHARACTER VARYING;
ALTER TABLE mst.payout_event_stat
  ADD COLUMN payout_international_correspondent_account_bank_account CHARACTER VARYING;
ALTER TABLE mst.payout_event_stat
  ADD COLUMN payout_international_correspondent_account_bank_name CHARACTER VARYING;
ALTER TABLE mst.payout_event_stat
  ADD COLUMN payout_international_correspondent_account_bank_address CHARACTER VARYING;
ALTER TABLE mst.payout_event_stat
  ADD COLUMN payout_international_correspondent_account_bank_bic CHARACTER VARYING;
ALTER TABLE mst.payout_event_stat
  ADD COLUMN payout_international_correspondent_account_bank_iban CHARACTER VARYING;
ALTER TABLE mst.payout_event_stat
  ADD COLUMN payout_international_correspondent_account_bank_aba_rtn CHARACTER VARYING;
ALTER TABLE mst.payout_event_stat
  ADD COLUMN payout_international_correspondent_account_bank_country_code CHARACTER VARYING;