package com.rbkmoney.magista.handler;

import com.rbkmoney.eventstock.client.poll.Pair;
import com.rbkmoney.magista.model.Invoice;
import com.rbkmoney.magista.model.InvoiceStatusChange;
import com.rbkmoney.magista.model.Payment;
import com.rbkmoney.magista.model.PaymentStatusChange;
import com.rbkmoney.magista.service.InvoiceService;
import com.rbkmoney.magista.service.PaymentService;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;

/**
 * Created by tolkonepiu on 31/10/2016.
 */
public class EventSaver implements Runnable {

    private BlockingQueue<Future<Pair>> queue;

    private PaymentService paymentService;
    private InvoiceService invoiceService;

    public EventSaver(BlockingQueue<Future<Pair>> queue, PaymentService paymentService, InvoiceService invoiceService) {
        this.queue = queue;
        this.paymentService = paymentService;
        this.invoiceService = invoiceService;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Future<Pair> future = queue.take();
                Pair<Long, Object> pair = future.get();

                Object object = pair.getValue();
                if (object != null) {
                    if (object instanceof Invoice) {
                        invoiceService.saveInvoice((Invoice) object);
                    }
                    if (object instanceof InvoiceStatusChange) {
                        invoiceService.changeInvoiceStatus((InvoiceStatusChange) object);
                    }
                    if (object instanceof Payment) {
                        paymentService.savePayment((Payment) object);
                    }
                    if (object instanceof PaymentStatusChange) {
                        paymentService.changePaymentStatus((PaymentStatusChange) object);
                    }
                }
            } catch (Exception e) {
                //ALARM! DON'T APPROVE!
            }
        }
    }
}
