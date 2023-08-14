package com.nineleaps.leaps.service.implementation;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nineleaps.leaps.common.ApiResponse;
import com.nineleaps.leaps.dto.ResponseDto;
import com.nineleaps.leaps.dto.user.ProfileUpdateDto;
import com.nineleaps.leaps.dto.user.SignupDto;
import com.nineleaps.leaps.dto.user.UserDto;
import com.nineleaps.leaps.enums.ResponseStatus;
import com.nineleaps.leaps.enums.Role;
import com.nineleaps.leaps.exceptions.CustomException;
import com.nineleaps.leaps.model.DeviceToken;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.model.tokens.AccessToken;
import com.nineleaps.leaps.repository.AccessTokenRepository;
import com.nineleaps.leaps.repository.DeviceTokenRepository;
import com.nineleaps.leaps.repository.UserRepository;
import com.nineleaps.leaps.service.UserServiceInterface;
import com.nineleaps.leaps.utils.Helper;
import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.nineleaps.leaps.LeapsApplication.NGROK;
import static com.nineleaps.leaps.config.MessageStrings.USER_CREATED;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserServiceInterface, UserDetailsService {
    private final UserRepository userRepository;
    private final AccessTokenRepository accessTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final DeviceTokenRepository deviceTokenRepository;
    @Override
    public void saveDeviceTokenToUser(String email, String deviceToken) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        } else {
            DeviceToken existingDeviceToken = deviceTokenRepository.findByUserEmail(email).orElse(null);

            if (existingDeviceToken != null) {
                existingDeviceToken.setToken(deviceToken);
                deviceTokenRepository.save(existingDeviceToken);
                log.info("Device token updated for user: {} and token is : {} ", email,deviceToken);

            } else {
                DeviceToken newDeviceToken = new DeviceToken(deviceToken, user);
                deviceTokenRepository.save(newDeviceToken);
                log.info("Device token saved for user: {}", email);
            }
        }
    }

    @Override
    public String getDeviceTokenByUserId(Long userId) {
        Optional<DeviceToken> deviceTokenOptional = deviceTokenRepository.findByUserId(userId);
        return deviceTokenOptional.map(DeviceToken::getToken).orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("user not found in the database");
        } else {
            log.info("user found in the database: {}", email);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    @Override
    public ResponseDto signUp(SignupDto signupDto) throws CustomException {
        //Check if the current email has already been registered. i.e. User already exists
        if (Helper.notNull(userRepository.findByEmail(signupDto.getEmail()))) {
            //if email already registered throw custom exception
            throw new CustomException("Email already associated with other user");
        }
        //add exception for registered phone number
        if (Helper.notNull(userRepository.findByPhoneNumber(signupDto.getPhoneNumber()))) {
            throw new CustomException("Phone number already associated with other user");
        }
        //encrypting the password
        String encryptedPassword = passwordEncoder.encode(signupDto.getPassword());
        User user = new User(signupDto.getFirstName(), signupDto.getLastName(), signupDto.getEmail(), signupDto.getPhoneNumber(), encryptedPassword, signupDto.getRole());
        try {
            userRepository.save(user);
            return new ResponseDto(ResponseStatus.SUCCESS.toString(), USER_CREATED);
        } catch (Exception e) {
            //handle signup error
            throw new CustomException(e.getMessage());
        }
    }


    @Override
    public void saveProfile(User user) {
        userRepository.save(user);
    }

    @Override
    public User getUser(String email) {
        log.info("getting user{} from the database", email);
        return userRepository.findByEmail(email);
    }

    @Override
    public UserDto getUser(User user) {
        return new UserDto(user);
    }

    @Override
    public User getGuest() {
        User user = userRepository.findByRole(Role.GUEST);
        if (!Helper.notNull(user)) {
            return null; //create a guest user call that function
        } else {
            return user;
        }
    }

    @Override
    public void updateProfile(User oldUser, ProfileUpdateDto profileUpdateDto) {
        User user = new User(profileUpdateDto, oldUser);
        userRepository.save(user);
    }

    @Override
    public void updateProfileImage(String profileImageUrl, User user) {
//if url does not contain ngrok url, directly save it
        String imageUrl = profileImageUrl;
//remove ngrok link
        if (profileImageUrl.contains(NGROK)) {
            imageUrl = profileImageUrl.substring(NGROK.length());
        }
        user.setProfileImageUrl(imageUrl);
        userRepository.save(user);
    }

    @Override
    public User getUserViaPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);

    }

    @Override
    public List<User> getUsers() {
        log.info("getting all user from the database");

        return userRepository.findAll();
    }

    @Override
    public ApiResponse logout(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            String email = decodedJWT.getSubject();

            User user = userRepository.findByEmail(email);

            if (user != null) {
                AccessToken tokenEntity = accessTokenRepository.findByJwtTokenAndUser_Email(token, email);
                if (tokenEntity != null) {
                    tokenEntity.setRevoked(true);
                    tokenEntity.setExpired(true);
                    accessTokenRepository.save(tokenEntity);
                    return new ApiResponse(true, "Logged out successfully.");
                } else {
                    return new ApiResponse(false, "Token not found.");
                }
            } else {
                return new ApiResponse(false, "User not found.");
            }
        } catch (Exception e) {
            return new ApiResponse(false, "Logout failed.");
        }
    }
}
