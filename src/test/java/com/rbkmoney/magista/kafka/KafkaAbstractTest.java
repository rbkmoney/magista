package com.rbkmoney.magista.kafka;

import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.magista.config.KafkaConfig;
import com.rbkmoney.magista.serde.MachineEventSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.KafkaContainer;

import java.util.Properties;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(initializers = KafkaAbstractTest.Initializer.class)
public abstract class KafkaAbstractTest {
    public static final String EVENT_SINK_INVOICE_TOPIC = "event_sink_invoice_topic";

    private static final String CONFLUENT_PLATFORM_VERSION = "5.0.1";

    @ClassRule
    public static KafkaContainer kafka = new KafkaContainer(CONFLUENT_PLATFORM_VERSION).withEmbeddedZookeeper();

    @Value("${kafka.invoice.topic}")
    public String topic;

    public static Producer<String, MachineEvent> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "client_id");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MachineEventSerializer.class.getName());
        return new KafkaProducer<>(props);
    }


    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {


        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues values = TestPropertyValues
                    .of("kafka.bootstrap.servers=" + kafka.getBootstrapServers());
            values.applyTo(configurableApplicationContext);

            Producer<String, MachineEvent> producer = createProducer();
            MachineEvent machineEvent = new MachineEvent();
            com.rbkmoney.machinegun.msgpack.Value data = new com.rbkmoney.machinegun.msgpack.Value();
            data.setBin(new byte[0]);
            machineEvent.setData(data);
            ProducerRecord<String, MachineEvent> producerRecord = new ProducerRecord<>(EVENT_SINK_INVOICE_TOPIC,
                    null, machineEvent);
            try {
                producer.send(producerRecord).get();
            } catch (Exception e) {
                log.error("KafkaAbstractTest initialize e: ", e);
            }
            producer.close();
        }


    }
}
