package com.nineleaps.leaps.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.nineleaps.leaps.model.RefreshToken;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.repository.RefreshTokenRepository;
import com.nineleaps.leaps.service.UserServiceInterface;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

@Component
@AllArgsConstructor
public class SecurityUtility {
    private final UserServiceInterface userServiceInterface;
    private RefreshTokenRepository refreshTokenRepository;

    public void setRefreshTokenRepository(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }
    public boolean isAccessTokenExpired(String accessToken) {
        DecodedJWT decodedAccessToken = JWT.decode(accessToken);
        Date expirationDate = decodedAccessToken.getExpiresAt();
        return expirationDate.before(new Date());
    }


    public boolean saveTokens(String rtoken, String email) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(rtoken);
        refreshToken.setEmail(email);
        refreshTokenRepository.save(refreshToken);
        return true;
    }

    public String updateAccessToken(String email2, HttpServletRequest request) throws IOException {
        RefreshToken refreshToken = refreshTokenRepository.findByEmail(email2);
        String token = refreshToken.getToken();
        String secretFilePath = "/Desktop"+"/leaps"+"/secret"+"/secret.txt";
        String absolutePath = System.getProperty("user.home") + File.separator + secretFilePath;
        String secret = readSecretFromFile(absolutePath);
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
        DecodedJWT decodedRefreshToken = JWT.decode(token);
        String email = decodedRefreshToken.getSubject();
        User user = userServiceInterface.getUser(email);
        String role = user.getRole().toString();
        String[] roles = new String[]{role};
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime accessTokenExpirationTime = now.plusMinutes(1440); // Update to desired expiration time
        Date accessTokenExpirationDate = Date.from(accessTokenExpirationTime.atZone(ZoneId.systemDefault()).toInstant());
        return JWT.create()
                .withSubject(email)
                .withExpiresAt(accessTokenExpirationDate)
                .withIssuer(request.getRequestURL().toString()) // Update to the appropriate issuer
                .withClaim("roles", Arrays.asList(roles))
                .sign(algorithm);

    }

    private String readSecretFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            return reader.readLine();
        }
    }
}









