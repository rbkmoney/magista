package com.rbkmoney.magista.query.impl;

import com.rbkmoney.damsel.domain.InvoicePayment;
import com.rbkmoney.damsel.merch_stat.GeoInfo;
import com.rbkmoney.damsel.merch_stat.StatPayment;
import com.rbkmoney.damsel.merch_stat.StatResponse;
import com.rbkmoney.damsel.merch_stat.StatResponseData;
import com.rbkmoney.magista.model.Payment;
import com.rbkmoney.magista.query.QueryExecutionException;
import com.rbkmoney.magista.query.QueryResult;
import com.rbkmoney.magista.repository.DaoException;
import javafx.util.Pair;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by vpankrashkin on 03.08.16.
 */
public class PaymentsFunction extends CursorScopedBaseFunction {
    public static final String PAYMENT_ID_PARAM = "payment_id";
    public static final String INVOICE_ID_PARAM = "invoice_id";
    public static final String PAYMENT_STATUS_PARAM = "payment_status";
    public static final String PAN_MASK_PARAM = "pan_mask";

    public static final String FUNC_NAME = "payments";

    public PaymentsFunction(Map<String, Object> params) {
        super(params, InvoicePayment.class, FUNC_NAME);
    }

    public String getInvoiceId() {
        return getStringParameter(INVOICE_ID_PARAM, false);
    }

    public String getPaymentId() {
        return getStringParameter(PAYMENT_ID_PARAM, false);
    }

    public String getPaymentStats() {
        return getStringParameter(PAYMENT_STATUS_PARAM, false);
    }

    public String getPanMask() {
        return getStringParameter(PAN_MASK_PARAM, false);
    }

    private BiFunction<Stream<Payment>, QueryResult, Supplier<StatResponse>> dataCollectorFunction = (st, qr) -> {
        StatResponseData statResponseData = StatResponseData.payments(st.map(payment -> {
            StatPayment statPayment = new StatPayment(payment.getInvoiceId(), payment.get);
            GeoInfo geoInfo = new GeoInfo();
            geoInfo.setCityName(payment.getCityName());
            statPayment.setGeoInfo(geoInfo);
            return statPayment;
        }).collect(Collectors.toList()));
        StatResponse statResponse = new StatResponse(statResponseData);
        statResponse.setTotalCount((qr).expectedTotalCount());
        return statResponse;
    };

    @Override
    public QueryResult execute(FunctionQueryContext context) throws QueryExecutionException {
        try {
            Pair<Integer, Collection<Payment>> result = context.getDao().getPayments(
                    getMerchantId(),
                    getShopId(),
                    Optional.ofNullable(getInvoiceId()),
                    Optional.ofNullable(getPaymentId()),
                    Optional.ofNullable(getPaymentStats()),
                    Optional.ofNullable(getPanMask()),
                    Optional.ofNullable(Instant.from(getFromTime())),
                    Optional.ofNullable(Instant.from(getToTime())),
                    Optional.ofNullable(getSize() == null ? null : getSize()),
                    Optional.ofNullable(getFrom() == null ? null : getFrom())
                    );
            return new BaseQueryResult<>(result.getKey(), () -> result.getValue().stream(), dataCollectorFunction);
        } catch (DaoException e) {
            throw new QueryExecutionException(e);
        }
    }

    @Override
    protected boolean checkParams(Map<String, Object> params, boolean throwOnError) {
        if (!super.checkParams(params, throwOnError)) {
            return false;
        }
        /*if (!StringUtils.hasLength(getStringParameter(PAYMENT_ID_PARAM, false))) {
            checkParamsResult(throwOnError, true, PAYMENT_ID_PARAM + " not found");
        }
        if (!StringUtils.hasLength(getStringParameter(INVOICE_ID_PARAM, false))) {
            checkParamsResult(throwOnError, true, INVOICE_ID_PARAM + " not found");
        }*/

        /*if (!StringUtils.hasLength(getStringParameter(PAYMENT_STATUS_PARAM, false))) {
            checkParamsResult(throwOnError, true, PAYMENT_STATUS_PARAM + " not found");
        }*/
        String val = getStringParameter(PAN_MASK_PARAM, false);
        if (val != null && !val.matches("[\\d*]+")) {
            checkParamsResult(throwOnError, true, PAN_MASK_PARAM + " is not valid");
        }
        return true;
    }

    @Override
    protected boolean isTimeRequired() {
        return false;
    }
}
