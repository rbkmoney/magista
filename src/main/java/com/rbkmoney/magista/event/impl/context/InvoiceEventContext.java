package com.rbkmoney.magista.event.impl.context;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.magista.event.EventContext;
import com.rbkmoney.magista.model.*;

/**
 * Created by tolkonepiu on 10/11/2016.
 */
public class InvoiceEventContext implements EventContext<StockEvent> {

    private StockEvent stockEvent;
    private Invoice invoice;
    private InvoiceStatusChange invoiceStatusChange;
    private Payment payment;
    private PaymentStatusChange paymentStatusChange;
    private InvoiceEvent invoiceEvent;

    public InvoiceEventContext(StockEvent stockEvent) {
        this.stockEvent = stockEvent;
    }

    @Override
    public StockEvent getSource() {
        return stockEvent;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public InvoiceEventContext setInvoice(Invoice invoice) {
        this.invoice = invoice;
        return this;
    }

    public InvoiceStatusChange getInvoiceStatusChange() {
        return invoiceStatusChange;
    }

    public InvoiceEventContext setInvoiceStatusChange(InvoiceStatusChange invoiceStatusChange) {
        this.invoiceStatusChange = invoiceStatusChange;
        return this;
    }

    public Payment getPayment() {
        return payment;
    }

    public InvoiceEventContext setPayment(Payment payment) {
        this.payment = payment;
        return this;
    }

    public PaymentStatusChange getPaymentStatusChange() {
        return paymentStatusChange;
    }

    public InvoiceEventContext setPaymentStatusChange(PaymentStatusChange paymentStatusChange) {
        this.paymentStatusChange = paymentStatusChange;
        return this;
    }

    public InvoiceEvent getInvoiceEvent() {
        return invoiceEvent;
    }

    public void setInvoiceEvent(InvoiceEvent invoiceEvent) {
        this.invoiceEvent = invoiceEvent;
    }
}
