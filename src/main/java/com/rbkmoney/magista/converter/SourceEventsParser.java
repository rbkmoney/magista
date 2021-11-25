package com.rbkmoney.magista.converter;

import com.rbkmoney.damsel.domain.InvoiceTemplate;
import com.rbkmoney.damsel.payment_processing.*;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.machinegun.msgpack.Value;
import com.rbkmoney.magista.exception.ParseException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TUnion;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SourceEventsParser {

    public static final String TPL = "tpl";
    public static final String ID = "id";

    private final UserInfo userInfo = new UserInfo("admin", UserType.internal_user(new InternalUser()));

    private final BinaryConverter<EventPayload> converter;
    private final InvoiceTemplatingSrv.Iface invoiceTemplatingClient;

    public List<EventPayload> parseEvents(MachineEvent message) {
        try {
            if (message.getData().isSetBin()) {
                var bin = message.getData().getBin();
                return List.of(converter.convert(bin, EventPayload.class));
            } else if (message.getData().isSetArr()) {
                return handleArray(message);
            } else {
                throw new ParseException(String.format("Cant parse type of message=%s", message.getData()));
            }
        } catch (Exception e) {
            log.error("Exception when parse message e: ", e);
            throw new ParseException(e);
        }
    }

    private List<EventPayload> handleArray(MachineEvent message) {
        var thriftArray = message.getData().getArr().stream()
                .map(TUnion::toString)
                .collect(Collectors.joining(", "));
        log.error("MachineEvent contains an ARRAY! instead of a binary, topic typing error, " +
                "thriftArray={}", thriftArray);
        return Stream.concat(getBinaries(message), getObjects(message))
                .collect(Collectors.toList());
    }

    private Stream<EventPayload> getBinaries(MachineEvent message) {
        return message.getData().getArr().stream()
                .filter(Value::isSetBin)
                .map(Value::getBin)
                .map(bytes -> converter.convert(bytes, EventPayload.class));
    }

    private Stream<EventPayload> getObjects(MachineEvent message) {
        return message.getData().getArr().stream()
                .filter(Value::isSetObj)
                .map(value -> value.getObj().get(Value.str(TPL)))
                .filter(Value::isSetObj)
                .map(tpl -> tpl.getObj().get(Value.str(ID)))
                .filter(Value::isSetStr)
                .map(Value::getStr)
                .map(this::getInvoiceTemplate)
                .map(this::getEventPayload);
    }

    @SneakyThrows
    private InvoiceTemplate getInvoiceTemplate(String id) {
        return invoiceTemplatingClient.get(userInfo, id);
    }

    private EventPayload getEventPayload(InvoiceTemplate invoiceTemplate) {
        return new EventPayload(
                EventPayload._Fields.INVOICE_TEMPLATE_CHANGES,
                List.of(InvoiceTemplateChange.invoice_template_created(new InvoiceTemplateCreated(invoiceTemplate))));
    }
}
