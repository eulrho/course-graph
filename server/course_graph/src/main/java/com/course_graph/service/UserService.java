package com.course_graph.service;

import com.course_graph.Exception.RestApiException;
import com.course_graph.dto.EditRequest;
import com.course_graph.dto.InfoResponse;
import com.course_graph.dto.LoginRequest;
import com.course_graph.dto.UserDTO;
import com.course_graph.entity.UserEntity;
import com.course_graph.enums.CustomErrorCode;
import com.course_graph.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public void join(UserDTO userDTO) {
        checkEmail(userDTO.getEmail());
        checkPasswordMatch(userDTO.getPassword(), userDTO.getPasswordCheck());
        userDTO.setPassword(encoder.encode(userDTO.getPassword()));
        UserEntity userEntity = UserEntity.toUserEntity(userDTO);
        userRepository.save(userEntity);
    }

    public void checkEmail(String email)
    {
        userRepository.findByEmail(email).ifPresent(m -> {
            throw new RestApiException(CustomErrorCode.DUPLICATE_EMAIL);});

        if (!email.endsWith("@chungbuk.ac.kr") && !email.endsWith("@cbnu.ac.kr"))
            throw new RestApiException(CustomErrorCode.INVALID_PARAMETER);
    }

    public void checkPasswordMatch(String password, String passwordCheck)
    {
        if (!password.equals(passwordCheck)) {
            throw new RestApiException(CustomErrorCode.NO_MATCH_PASSWORD);
        }
    }

    public UserEntity login(LoginRequest loginRequest) {
        UserEntity userEntity = getLoginUserByEmail(loginRequest.getEmail());
        if (userEntity == null)
            throw new RestApiException(CustomErrorCode.NO_MATCH_EMAIL);
        checkPresentPassword(loginRequest.getPassword(), userEntity.getPassword());
        return userEntity;
    }

    public UserEntity getLoginUserByEmail(String email) {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) return null;

        return optionalUser.get();
    }

    public void checkPresentPassword(String password, String encodedPassword) {
        if (!encoder.matches(password, encodedPassword))
            throw new RestApiException(CustomErrorCode.NO_MATCH_PASSWORD);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = getLoginUserByEmail(email);

        if (userEntity == null)
            throw new UsernameNotFoundException("User not found with email: " + email);

        return User.builder()
                .username(userEntity.getEmail())
                .password(userEntity.getPassword())
                .build();
    }

    public InfoResponse getInfo(UserEntity userEntity) {
        return InfoResponse.toInfoResponse(userEntity);
    }

    @Transactional
    public void edit(EditRequest editRequest, UserEntity userEntity) {
        String newPassword = editRequest.getNewPassword();
        if (!newPassword.isEmpty()) {
            if (!(newPassword.length() >= 4 && newPassword.length() <= 10))
                throw new RestApiException(CustomErrorCode.INVALID_PASSWORD_FORMAT);
            checkPasswordMatch(newPassword, editRequest.getNewPasswordCheck());
            userEntity.edit(encoder.encode(newPassword), editRequest.getYear());
        }
        else userEntity.edit(userEntity.getPassword(), editRequest.getYear());
    }

    @Transactional
    public void delete(String userPwd, UserEntity userEntity) {
        checkPresentPassword(userPwd, userEntity.getPassword());
        userRepository.delete(userEntity);
    }

    public void logout() {
        // do nothing
    }
}
