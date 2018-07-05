UPDATE mst.payout_event_stat
SET payout_account_purpose =
'Agr ' || payout_account_legal_agreement_id || ' ' || to_char(payout_account_legal_agreement_signed_at, 'dd.MM.yyyy') ||
', ' || payout_id || ' FOR accepted payments.'
WHERE payout_account_type = 'INTERNATIONAL_PAYOUT_ACCOUNT' AND payout_account_purpose IS NULL;
