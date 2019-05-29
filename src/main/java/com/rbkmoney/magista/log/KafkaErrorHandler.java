package com.rbkmoney.magista.log;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.kafka.listener.BatchErrorHandler;
import org.springframework.kafka.listener.ErrorHandler;

@Slf4j
public class KafkaErrorHandler implements BatchErrorHandler {
    @Override
    public void handle(Exception thrownException, ConsumerRecords<?, ?> data) {

    }

//    @Override
//    public void handle(Exception thrownException, ConsumerRecord<?, ?> data) {
//        if (data != null) {
//            log.error("Error while processing: data-key: {}, data-offset: {}, data-partition: {}",
//                    data.key(), data.offset(), data.partition(), thrownException);
//        } else {
//            log.error("Error while processing", thrownException);
//        }
//    }

}
