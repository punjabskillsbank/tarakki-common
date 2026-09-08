package com.tarakki.common.audit.kafka;

import com.tarakki.common.audit.AuditTestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuditKafkaProducerTest {

    @Test
    void shouldSendMessageToConfiguredTopic() {
        KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);
        when(kafkaTemplate.send(AuditTestDataFactory.AUDIT_TOPIC, AuditTestDataFactory.AUDIT_MESSAGE))
                .thenReturn(CompletableFuture.completedFuture(null));
        AuditKafkaProducer producer = new AuditKafkaProducer(kafkaTemplate);
        ReflectionTestUtils.setField(producer, "topic", AuditTestDataFactory.AUDIT_TOPIC);

        producer.sendAuditLog(AuditTestDataFactory.AUDIT_MESSAGE);

        verify(kafkaTemplate).send(AuditTestDataFactory.AUDIT_TOPIC, AuditTestDataFactory.AUDIT_MESSAGE);
    }
}
