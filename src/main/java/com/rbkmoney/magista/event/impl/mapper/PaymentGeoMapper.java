package com.rbkmoney.magista.event.impl.mapper;

import com.rbkmoney.damsel.geo_ip.LocationInfo;
import com.rbkmoney.damsel.geo_ip.geo_ipConstants;
import com.rbkmoney.magista.domain.tables.pojos.InvoiceEventStat;
import com.rbkmoney.magista.event.Mapper;
import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.model.Payment;
import com.rbkmoney.magista.provider.GeoProvider;
import com.rbkmoney.magista.provider.ProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by tolkonepiu on 10/11/2016.
 */
public class PaymentGeoMapper implements Mapper<InvoiceEventContext> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    GeoProvider geoProvider;

    public PaymentGeoMapper(GeoProvider geoProvider) {
        this.geoProvider = geoProvider;
    }

    @Override
    public InvoiceEventContext fill(InvoiceEventContext value) {
        Payment payment = value.getPayment();
        InvoiceEventStat invoiceEventStat = value.getInvoiceEventStat();

        log.debug("Start geo enrichment, paymentId='{}', invoiceId='{}', eventId='{}', ip='{}'",
                invoiceEventStat.getPaymentId(), invoiceEventStat.getInvoiceId(), invoiceEventStat.getEventId(), invoiceEventStat.getPaymentIp());

        try {
            LocationInfo locationInfo = geoProvider.getLocationInfo(invoiceEventStat.getPaymentIp());
            payment.setCityId(locationInfo.getCityGeoId());
            payment.setCountryId(locationInfo.getCountryGeoId());

            invoiceEventStat.setPaymentCityId(locationInfo.getCityGeoId());
            invoiceEventStat.setPaymentCountryId(locationInfo.getCountryGeoId());
        } catch (ProviderException ex) {
            log.warn("Failed to find city or country by ip:" + invoiceEventStat.getPaymentIp(), ex);
            payment.setCityId(geo_ipConstants.GEO_ID_UNKNOWN);
            payment.setCountryId(geo_ipConstants.GEO_ID_UNKNOWN);

            invoiceEventStat.setPaymentCityId(geo_ipConstants.GEO_ID_UNKNOWN);
            invoiceEventStat.setPaymentCountryId(geo_ipConstants.GEO_ID_UNKNOWN);
        } finally {
            log.debug("End geo enrichment, paymentId='{}', invoiceId='{}', eventId='{}', ip='{}'",
                    invoiceEventStat.getPaymentId(), invoiceEventStat.getInvoiceId(), invoiceEventStat.getEventId(), invoiceEventStat.getPaymentIp());
        }

        value.setPayment(payment);

        return value;
    }
}
