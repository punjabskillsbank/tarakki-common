package com.tarakki.common.audit.config;

import com.tarakki.common.audit.aspect.AuditLoggingAspect;
import com.tarakki.common.audit.kafka.AuditKafkaProducer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({AuditLoggingAspect.class, AuditKafkaProducer.class})
public class AuditAutoConfiguration {
}
