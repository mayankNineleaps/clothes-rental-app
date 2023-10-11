package com.nineleaps.leaps.service.implementation;

import com.nineleaps.leaps.model.RefreshToken;
import com.nineleaps.leaps.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RefreshTokenServiceImplTest {
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private RefreshTokenServiceImpl refreshTokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        refreshTokenService = new RefreshTokenServiceImpl(refreshTokenRepository);
    }

    @Test
    void testGetRefreshToken() {
        // Arrange
        String email = "example@example.com";
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("sampleToken");

        // Mock the behavior of the refreshTokenRepository
        when(refreshTokenRepository.findByEmail(email)).thenReturn(refreshToken);

        // Act
        String resultToken = refreshTokenService.getRefreshToken(email);

        // Assert
        assertEquals("sampleToken", resultToken);
        verify(refreshTokenRepository, times(1)).findByEmail(email);
    }
}
