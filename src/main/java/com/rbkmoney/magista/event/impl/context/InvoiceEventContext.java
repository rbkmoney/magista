package com.rbkmoney.magista.event.impl.context;

import com.rbkmoney.damsel.event_stock.StockEvent;
import com.rbkmoney.magista.event.EventContext;
import com.rbkmoney.magista.model.Invoice;
import com.rbkmoney.magista.model.InvoiceStatusChange;
import com.rbkmoney.magista.model.Payment;
import com.rbkmoney.magista.model.PaymentStatusChange;

/**
 * Created by tolkonepiu on 10/11/2016.
 */
public class InvoiceEventContext implements EventContext<StockEvent> {

    private StockEvent stockEvent;
    private Invoice invoice;
    private InvoiceStatusChange invoiceStatusChange;
    private Payment payment;
    private PaymentStatusChange paymentStatusChange;

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

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public InvoiceStatusChange getInvoiceStatusChange() {
        return invoiceStatusChange;
    }

    public void setInvoiceStatusChange(InvoiceStatusChange invoiceStatusChange) {
        this.invoiceStatusChange = invoiceStatusChange;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public PaymentStatusChange getPaymentStatusChange() {
        return paymentStatusChange;
    }

    public void setPaymentStatusChange(PaymentStatusChange paymentStatusChange) {
        this.paymentStatusChange = paymentStatusChange;
    }
}
