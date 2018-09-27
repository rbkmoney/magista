package com.rbkmoney.magista.event.impl.mapper;

/**
 * Created by tolkonepiu on 10/11/2016.
 */
//TODO
public class PaymentGeoMapper { //implements Mapper<InvoiceEventContext> {

//    private final Logger log = LoggerFactory.getLogger(this.getClass());
//
//    private final GeoProvider geoProvider;
//
//    public PaymentGeoMapper(GeoProvider geoProvider) {
//        this.geoProvider = geoProvider;
//    }
//
//    @Override
//    public InvoiceEventContext fill(InvoiceEventContext value) {
//        InvoiceEventStat invoiceEventStat = value.getInvoiceEventStat();
//
//        log.debug("Start geo enrichment, paymentId='{}', invoiceId='{}', eventId='{}', ip='{}'",
//                invoiceEventStat.getPaymentId(), invoiceEventStat.getInvoiceId(), invoiceEventStat.getEventId(), invoiceEventStat.getPaymentIp());
//
//        try {
//            if (invoiceEventStat.getPaymentIp() != null) {
//                LocationInfo locationInfo = geoProvider.getLocationInfo(invoiceEventStat.getPaymentIp());
//                invoiceEventStat.setPaymentCityId(locationInfo.getCityGeoId());
//                invoiceEventStat.setPaymentCountryId(locationInfo.getCountryGeoId());
//            }
//        } catch (ProviderException ex) {
//            log.warn("Failed to find city or country by ip:" + invoiceEventStat.getPaymentIp(), ex);
//            invoiceEventStat.setPaymentCityId(geo_ipConstants.GEO_ID_UNKNOWN);
//            invoiceEventStat.setPaymentCountryId(geo_ipConstants.GEO_ID_UNKNOWN);
//        } finally {
//            log.debug("End geo enrichment, paymentId='{}', invoiceId='{}', eventId='{}', ip='{}'",
//                    invoiceEventStat.getPaymentId(), invoiceEventStat.getInvoiceId(), invoiceEventStat.getEventId(), invoiceEventStat.getPaymentIp());
//        }
//
//        return value.setInvoiceEventStat(invoiceEventStat);
//    }
}
