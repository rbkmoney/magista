package com.rbkmoney.magista.service;

import com.rbkmoney.magista.dao.PayoutEventDao;
import com.rbkmoney.magista.domain.tables.pojos.PayoutEventStat;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PayoutEventService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final PayoutEventDao payoutEventDao;

    @Autowired
    public PayoutEventService(PayoutEventDao payoutEventDao) {
        this.payoutEventDao = payoutEventDao;
    }

    public Optional<Long> getLastEventId() throws StorageException {
        try {
            return Optional.ofNullable(payoutEventDao.getLastEventId());
        } catch (DaoException ex) {
            throw new StorageException("Failed to get last event id", ex);
        }
    }

    public PayoutEventStat getPayoutEventById(String payoutId) throws NotFoundException {
        PayoutEventStat payoutEvent = payoutEventDao.findPayoutById(payoutId);
        if (payoutEvent == null) {
            throw new NotFoundException(String.format("Payout not found, payoutId='%s'", payoutId));
        }
        return payoutEvent;
    }

    public void savePayoutEvent(PayoutEventStat payoutEvent) throws StorageException {
        log.debug("Save payout event, event='{}'", payoutEvent);

        try {
            payoutEventDao.insert(payoutEvent);
            log.info("Payout event have been saved, event='{}'", payoutEvent);
        } catch (DaoException ex) {
            String message = String.format("Failed to save payout event, event='%s'", payoutEvent);
            throw new StorageException(message, ex);
        }

    }

    public void changePayoutEventStatus(PayoutEventStat payoutStatusEvent) throws NotFoundException, StorageException {
        log.debug("Change payout event status, payoutId='{}', eventId='{}', payoutStatus='{}'",
                payoutStatusEvent.getPayoutId(), payoutStatusEvent.getEventId(), payoutStatusEvent.getPayoutStatus());

        try {
            PayoutEventStat payoutEvent = getPayoutEventById(payoutStatusEvent.getPayoutId());

            payoutEvent.setEventType(payoutStatusEvent.getEventType());
            payoutEvent.setEventId(payoutStatusEvent.getEventId());
            payoutEvent.setEventCreatedAt(payoutStatusEvent.getEventCreatedAt());

            payoutEvent.setPayoutStatus(payoutStatusEvent.getPayoutStatus());
            payoutEvent.setPayoutCancelDetails(payoutStatusEvent.getPayoutCancelDetails());

            payoutEventDao.update(payoutEvent);
            log.info("Payout event status have been changed, payoutId='{}', eventId='{}', payoutStatus='{}'",
                    payoutEvent.getPayoutId(), payoutEvent.getEventId(), payoutEvent.getPayoutStatus());

        } catch (DaoException ex) {
            String message = String.format("Failed to change payout event status, payoutId='%s', eventId='%d', payoutStatus='%s'",
                    payoutStatusEvent.getPayoutId(), payoutStatusEvent.getEventId(), payoutStatusEvent.getPayoutStatus());
            throw new StorageException(message, ex);
        }
    }
}
