ALTER TABLE mst.payout_event_stat
  ADD COLUMN payout_account_legal_name CHARACTER VARYING;
ALTER TABLE mst.payout_event_stat
  ADD COLUMN payout_account_trading_name CHARACTER VARYING;
ALTER TABLE mst.payout_event_stat
  ADD COLUMN payout_account_registered_address CHARACTER VARYING;
ALTER TABLE mst.payout_event_stat
  ADD COLUMN payout_account_actual_address CHARACTER VARYING;
ALTER TABLE mst.payout_event_stat
  ADD COLUMN payout_account_registered_number CHARACTER VARYING;
ALTER TABLE mst.payout_event_stat
  ADD COLUMN payout_account_bank_address CHARACTER VARYING;
ALTER TABLE mst.payout_event_stat
  ADD COLUMN payout_account_bank_iban CHARACTER VARYING;
ALTER TABLE mst.payout_event_stat
  ADD COLUMN payout_account_bank_bic CHARACTER VARYING;
ALTER TABLE mst.payout_event_stat
    RENAME payout_account_bank_bik TO payout_account_bank_local_code;