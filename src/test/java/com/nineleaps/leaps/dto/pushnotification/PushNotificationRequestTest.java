package com.nineleaps.leaps.dto.pushnotification;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
@Tag("unit_tests")
@DisplayName("PushNotificationRequest Tests")
class PushNotificationRequestTest {

    @Test
    @DisplayName("Test setter for topic")
    void testSetterForTopic() {
        // Arrange
        PushNotificationRequest request = new PushNotificationRequest();
        String topic = "TestTopic";

        // Act
        request.setTopic(topic);

        // Assert
        assertEquals(topic, request.getTopic());
    }

    @Test
    @DisplayName("Test setter for token")
    void testSetterForToken() {
        // Arrange
        PushNotificationRequest request = new PushNotificationRequest();
        String token = "TestToken";

        // Act
        request.setToken(token);

        // Assert
        assertEquals(token, request.getToken());
    }

    @Test
    @DisplayName("Test no-args constructor")
    void testNoArgsConstructor() {
        // Arrange and Act
        PushNotificationRequest request = new PushNotificationRequest();

        // Assert
        // Verify that properties are initialized to default values
        assertEquals("Order info", request.getTitle());
        assertNull(request.getTopic());
        assertNull(request.getToken());
    }
}
