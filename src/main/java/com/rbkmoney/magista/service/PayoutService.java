package com.rbkmoney.magista.service;

import com.rbkmoney.magista.dao.PayoutDao;
import com.rbkmoney.magista.domain.tables.pojos.PayoutData;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.StorageException;
import com.rbkmoney.magista.util.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.rbkmoney.magista.domain.enums.PayoutEventType.PAYOUT_CREATED;

@Service
public class PayoutService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final PayoutDao payoutEventDao;

    @Autowired
    public PayoutService(PayoutDao payoutEventDao) {
        this.payoutEventDao = payoutEventDao;
    }

    public PayoutData getPayoutData(String payoutId) throws NotFoundException {
        try {
            PayoutData payoutData = payoutEventDao.get(payoutId);
            if (payoutData == null) {
                throw new NotFoundException(String.format("Payout not found, payoutId='%s'", payoutId));
            }
            return payoutData;
        } catch (DaoException ex) {
            throw new StorageException(String.format("Failed to get payout, payoutId='%s'", payoutId), ex);
        }
    }

    public void savePayout(PayoutData payoutData) throws StorageException {
        log.debug("Save payout, event='{}'", payoutData);
        try {
            if (payoutData.getEventType() != PAYOUT_CREATED) {
                PayoutData previousPayoutData = getPayoutData(payoutData.getPayoutId());
                BeanUtil.merge(previousPayoutData, payoutData);
            }
            payoutEventDao.save(payoutData);
            log.info("Payout have been saved, event='{}'", payoutData);
        } catch (DaoException ex) {
            String message = String.format("Failed to save payout, payout='%s'", payoutData);
            throw new StorageException(message, ex);
        }
    }

}
