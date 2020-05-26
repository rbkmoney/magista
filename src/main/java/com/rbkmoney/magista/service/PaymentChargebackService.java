package com.rbkmoney.magista.service;

import com.rbkmoney.magista.dao.ChargebackDao;
import com.rbkmoney.magista.domain.tables.pojos.ChargebackData;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.exception.DaoException;
import com.rbkmoney.magista.exception.NotFoundException;
import com.rbkmoney.magista.exception.StorageException;
import com.rbkmoney.magista.util.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentChargebackService {

    private final ChargebackDao chargebackDao;

    private final PaymentService paymentService;

    public ChargebackData getChargeback(String invoiceId, String paymentId, String chargebackId) {
        try {
            ChargebackData chargeback = chargebackDao.get(invoiceId, paymentId, chargebackId);
            if (chargeback == null) {
                throw new NotFoundException(String.format("Chargeback not found, invoiceId=%s, paymentId=%s, chargebackId=%s", invoiceId, paymentId, chargebackId));
            }
            return chargeback;
        } catch (DaoException ex) {
            throw new StorageException(String.format("Failed to get chargeback, invoiceId=%s, paymentId=%s, chargebackId=%s", invoiceId, paymentId, chargebackId), ex);
        }
    }

    public void savePaymentChargeback(ChargebackData chargeback) {
        log.info("Trying to save chargeback event, eventType={}, invoiceId={}, paymentId={}, chargebackId={}",
                chargeback.getEventType(), chargeback.getInvoiceId(), chargeback.getPaymentId(), chargeback.getChargebackId());
        switch (chargeback.getEventType()) {
            case INVOICE_PAYMENT_CHARGEBACK_CREATED:
                PaymentData paymentData = paymentService.getPaymentData(chargeback.getInvoiceId(), chargeback.getPaymentId());
                chargeback.setPartyId(paymentData.getPartyId().toString());
                chargeback.setPartyShopId(paymentData.getPartyShopId());
                break;
            case INVOICE_PAYMENT_CHARGEBACK_STATUS_CHANGED:
                ChargebackData previousChargebackEvent = getChargeback(chargeback.getInvoiceId(), chargeback.getPaymentId(), chargeback.getChargebackId());
                BeanUtil.merge(previousChargebackEvent, chargeback, "id");
                break;
        }

        try {
            chargebackDao.save(chargeback);
            log.info("Chargeback event have been saved, chargeback={}", chargeback);
        } catch (DaoException ex) {
            throw new StorageException(String.format("Failed to save chargeback, chargeback=%s", chargeback), ex);
        }
    }

}
