package com.rbkmoney.magista.event;

import com.rbkmoney.magista.event.impl.context.InvoiceEventContext;
import com.rbkmoney.magista.service.InvoiceService;
import com.rbkmoney.magista.service.PaymentService;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;

/**
 * Created by tolkonepiu on 31/10/2016.
 */
public class EventSaver implements Runnable {

    private BlockingQueue<Future<EventContext>> queue;

    private PaymentService paymentService;
    private InvoiceService invoiceService;

    public EventSaver(BlockingQueue<Future<EventContext>> queue, PaymentService paymentService, InvoiceService invoiceService) {
        this.queue = queue;
        this.paymentService = paymentService;
        this.invoiceService = invoiceService;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Future<EventContext> future = queue.take();
                EventContext eventContext = future.get();

                if (eventContext instanceof InvoiceEventContext) {
                    InvoiceEventContext invoiceEventContext = (InvoiceEventContext) eventContext;
                    if (invoiceEventContext.getInvoice() != null) {
                        invoiceService.saveInvoice(invoiceEventContext.getInvoice());
                    }

                    if (invoiceEventContext.getInvoiceStatusChange() != null) {
                        invoiceService.changeInvoiceStatus(invoiceEventContext.getInvoiceStatusChange());
                    }

                    if (invoiceEventContext.getPayment() != null) {
                        paymentService.savePayment(invoiceEventContext.getPayment());
                    }

                    if (invoiceEventContext.getPaymentStatusChange() != null) {
                        paymentService.changePaymentStatus(invoiceEventContext.getPaymentStatusChange());
                    }
                }
            } catch (Exception e) {
                //ALARM! DON'T APPROVE!
            }
        }
    }
}
