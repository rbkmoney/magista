package com.rbkmoney.magista.event.impl.handler;

import com.rbkmoney.damsel.payment_processing.InvoiceChange;
import com.rbkmoney.damsel.payment_processing.InvoicePaymentChange;
import com.rbkmoney.damsel.user_interaction.PaymentTerminalReceipt;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.domain.enums.InvoiceEventType;
import com.rbkmoney.magista.domain.tables.pojos.PaymentData;
import com.rbkmoney.magista.event.ChangeType;
import com.rbkmoney.magista.event.Handler;
import com.rbkmoney.magista.event.Processor;
import com.rbkmoney.magista.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentTerminalRecieptEventHandler implements Handler<InvoiceChange, MachineEvent> {

    private final PaymentService paymentService;

    @Autowired
    public PaymentTerminalRecieptEventHandler(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public Processor handle(InvoiceChange change, MachineEvent machineEvent) {

        PaymentData paymentData = new PaymentData();
        paymentData.setEventId(machineEvent.getEventId());
        paymentData.setEventType(InvoiceEventType.PAYMENT_TERMINAL_RECIEPT);
        paymentData.setEventCreatedAt(TypeUtil.stringToLocalDateTime(machineEvent.getCreatedAt()));
        paymentData.setInvoiceId(machineEvent.getSourceId());

        InvoicePaymentChange invoicePaymentChange = change.getInvoicePaymentChange();

        String paymentId = invoicePaymentChange.getId();
        paymentData.setPaymentId(paymentId);

        PaymentTerminalReceipt paymentTerminalReceipt = invoicePaymentChange
                .getPayload()
                .getInvoicePaymentSessionChange()
                .getPayload()
                .getSessionInteractionRequested()
                .getInteraction()
                .getPaymentTerminalReciept();

        paymentData.setPaymentShortId(paymentTerminalReceipt.getShortPaymentId());

        return () -> paymentService.savePayment(paymentData);
    }

    @Override
    public ChangeType getChangeType() {
        return ChangeType.INVOICE_PAYMENT_TERMINAL_RECIEPT;
    }
}
