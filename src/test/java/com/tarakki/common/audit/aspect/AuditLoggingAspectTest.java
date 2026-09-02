package com.tarakki.common.audit.aspect;

import com.tarakki.common.audit.AuditTestDataFactory;
import com.tarakki.common.audit.annotation.Auditable;
import com.tarakki.common.audit.kafka.AuditKafkaProducer;
import tools.jackson.databind.ObjectMapper;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.EntityManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(
    classes = {
        AuditLoggingAspectTest.TestController.class,
        AuditLoggingAspect.class,
        org.springframework.boot.autoconfigure.aop.AopAutoConfiguration.class,
        org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration.class
    }
)
class AuditLoggingAspectTest {

    @Component
    public static class TestController {
        @Auditable(eventName = "ENTITY_DELETED", entityName = "TEST_ENTITY", entityClass = TestEntity.class, entityIdArgSpel = "#id")
        public void deleteEntity(Long id) {}
    }

    @Entity
    public static class TestEntity {
        @Id
        private Long id;
        private String name;
        public TestEntity() {}
        public TestEntity(Long id, String name) { this.id = id; this.name = name; }
        public Long getId() { return id; }
        public String getName() { return name; }
    }

    @Autowired
    private TestController testController;

    @MockitoBean
    private AuditKafkaProducer auditKafkaProducer;

    @MockitoBean
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper;

    private AuditLoggingAspect unitAspect;
    private AuditKafkaProducer mockProducer;
    private EntityManager mockEntityManager;
    private Auditable mockAuditable;

    @BeforeEach
    void setUp() {
        RequestContextHolder.resetRequestAttributes();
        mockProducer = mock(AuditKafkaProducer.class);
        mockEntityManager = mock(EntityManager.class);
        unitAspect = new AuditLoggingAspect(mockProducer, objectMapper, mockEntityManager);
        org.springframework.test.util.ReflectionTestUtils.setField(unitAspect, "serviceName", "test-service");
        
        mockAuditable = mock(Auditable.class);
        when(mockAuditable.eventName()).thenReturn("ENTITY_DELETED");
        when(mockAuditable.entityName()).thenReturn("TEST_ENTITY");
        doReturn(TestEntity.class).when(mockAuditable).entityClass();
        when(mockAuditable.entityIdArgSpel()).thenReturn("#id");
        when(mockAuditable.entityIdResultSpel()).thenReturn("");
    }

    private ProceedingJoinPoint mockJoinPointWithArgs(Object... args) throws NoSuchMethodException {
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);
        Method method = TestController.class.getMethod("deleteEntity", Long.class);
        when(signature.getMethod()).thenReturn(method);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(args);
        return joinPoint;
    }

    @Test
    void shouldPopulateOldValueFromDbOnDelete() throws Throwable {
        Long entityId = 1L;
        TestEntity existingEntity = new TestEntity(entityId, "Test Name");

        when(mockEntityManager.find(TestEntity.class, entityId)).thenReturn(existingEntity).thenReturn(null);

        ProceedingJoinPoint joinPoint = mockJoinPointWithArgs(entityId);
        when(joinPoint.proceed()).thenReturn(null);

        unitAspect.logAuditActivity(joinPoint, mockAuditable);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockProducer).sendAuditLog(messageCaptor.capture());

        String capturedMessage = messageCaptor.getValue();
        assertNotNull(capturedMessage);

        Map<?, ?> payload = objectMapper.readValue(capturedMessage, Map.class);
        assertEquals("test-service", payload.get("service_name"));
        assertEquals("TEST_ENTITY", payload.get("entity_name"));
        assertEquals(entityId.toString(), payload.get("entity_id"));
        assertEquals("ENTITY_DELETED", payload.get("event_name"));
        assertEquals("SYSTEM", payload.get("performed_by"));

        assertNotNull(payload.get("old_value"));
        String oldValueJson = (String) payload.get("old_value");
        Map<?, ?> oldValueMap = objectMapper.readValue(oldValueJson, Map.class);
        assertEquals("Test Name", oldValueMap.get("name"));

        assertNull(payload.get("new_value"));
        assertNotNull(payload.get("event_time"));
    }

    @Test
    void shouldHandleMissingEntityGracefully() throws Throwable {
        Long entityId = 999L;
        when(mockEntityManager.find(TestEntity.class, entityId)).thenReturn(null);

        ProceedingJoinPoint joinPoint = mockJoinPointWithArgs(entityId);
        when(joinPoint.proceed()).thenReturn(null);

        unitAspect.logAuditActivity(joinPoint, mockAuditable);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockProducer).sendAuditLog(messageCaptor.capture());

        Map<?, ?> payload = objectMapper.readValue(messageCaptor.getValue(), Map.class);
        assertNull(payload.get("old_value"));
        assertNull(payload.get("new_value"));
    }

    @Test
    void shouldUseMemberIdFromRequestHeader() throws Throwable {
        Long entityId = 1L;
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AuditTestDataFactory.MEMBER_ID_HEADER, AuditTestDataFactory.MEMBER_ID);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        when(mockEntityManager.find(TestEntity.class, entityId)).thenReturn(null);

        ProceedingJoinPoint joinPoint = mockJoinPointWithArgs(entityId);
        when(joinPoint.proceed()).thenReturn(null);

        unitAspect.logAuditActivity(joinPoint, mockAuditable);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockProducer).sendAuditLog(messageCaptor.capture());
        Map<?, ?> payload = objectMapper.readValue(messageCaptor.getValue(), Map.class);
        assertEquals(AuditTestDataFactory.MEMBER_ID, payload.get("performed_by"));
    }

    @Test
    void shouldUseSystemForBlankMemberId() throws Throwable {
        Long entityId = 1L;
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AuditTestDataFactory.MEMBER_ID_HEADER, " ");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        when(mockEntityManager.find(TestEntity.class, entityId)).thenReturn(null);

        ProceedingJoinPoint joinPoint = mockJoinPointWithArgs(entityId);
        when(joinPoint.proceed()).thenReturn(null);

        unitAspect.logAuditActivity(joinPoint, mockAuditable);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockProducer).sendAuditLog(messageCaptor.capture());
        Map<?, ?> payload = objectMapper.readValue(messageCaptor.getValue(), Map.class);
        assertEquals(AuditTestDataFactory.SYSTEM_ACTOR, payload.get("performed_by"));
    }
}
