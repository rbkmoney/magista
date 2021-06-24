package com.rbkmoney.magista.service;

import com.rbkmoney.magista.dao.PayoutDao;
import com.rbkmoney.magista.domain.tables.pojos.Payout;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.StorageException;
import com.rbkmoney.magista.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PayoutService {

    private final PayoutDao payoutEventDao;

    @Autowired
    public PayoutService(PayoutDao payoutEventDao) {
        this.payoutEventDao = payoutEventDao;
    }

    public Payout getPayout(String payoutId) throws NotFoundException {
        try {
            Payout payoutData = payoutEventDao.get(payoutId);
            if (payoutData == null) {
                throw new NotFoundException(String.format("Payout not found, payoutId='%s'", payoutId));
            }
            return payoutData;
        } catch (DaoException ex) {
            throw new StorageException(String.format("Failed to get payout, payoutId='%s'", payoutId), ex);
        }
    }

    public void savePayout(Payout payout) throws StorageException {
        log.debug("Save payout, event='{}'", payout);
        try {
            payoutEventDao.save(payout);
            log.info("Payout have been saved, event='{}'", payout);
        } catch (DaoException ex) {
            String message = String.format("Failed to save payout, payout='%s'", payout);
            throw new StorageException(message, ex);
        }
    }

    public void updatePayout(Payout payout) throws StorageException {
        log.debug("Update payout, payout='{}'", payout);
        try {
            payoutEventDao.update(payout);
            log.info("Payout have been updated, payout='{}'", payout);
        } catch (DaoException ex) {
            String message = String.format("Failed to update payout, payout='%s'", payout);
            throw new StorageException(message, ex);
        }
    }
}
