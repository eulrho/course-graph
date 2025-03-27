package com.course_graph.service;

import com.course_graph.Exception.RestApiException;
//import com.course_graph.dto.InfoResponse;
import com.course_graph.dto.LoginRequest;
import com.course_graph.dto.UserDTO;
//import com.course_graph.entity.TokenEntity;
//import com.course_graph.entity.UserEntity;
import com.course_graph.enums.CustomErrorCode;

import com.course_graph.token.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
//    private final UserRepository userRepository;
//    private final TokenRepository tokenRepository;
    //private final MemberService memberService;
    private final BCryptPasswordEncoder encoder;

    //@Transactional
    public void join(UserDTO userDTO) {
        //checkEmail(userDTO.getEmail());
        checkPasswordMatch(userDTO.getPassword(), userDTO.getPasswordCheck());
        //memberService.checkVerification(userDTO.getEmail());
        userDTO.setPassword(encoder.encode(userDTO.getPassword()));
        System.out.println(userDTO);
        //UserEntity userEntity = UserEntity.toUserEntity(userDTO);
        //userRepository.save(userEntity);
    }
//
//    public void checkEmail(String email)
//    {
//        userRepository.findByEmail(email).ifPresent(m -> {
//            throw new RestApiException(CustomErrorCode.DUPLICATE_EMAIL);});
//    }

    public void checkPasswordMatch(String password, String passwordCheck)
    {
        if (!password.equals(passwordCheck)) {
            throw new RestApiException(CustomErrorCode.NO_MATCH_PASSWORD);
        }
    }

    //@Transactional
    public String login(LoginRequest loginRequest) {
//        UserEntity userEntity = getLoginUserByEmail(loginRequest.getEmail());
//        if (userEntity == null)
//            throw new RestApiException(CustomErrorCode.NO_MATCH_EMAIL);
//        checkPresentPassword(loginRequest.getPassword(), userEntity.getPassword());
        //String jwtToken = JwtProvider.createToken(userEntity.getEmail());
        System.out.println(loginRequest);
        String jwtToken = JwtProvider.createToken(loginRequest.getEmail());
//        tokenRepository.findByEmail(userEntity.getEmail()).ifPresent(m -> {
//            tokenRepository.deleteByEmail(userEntity.getEmail());});
//        TokenEntity tokenEntity = TokenEntity.toTokenEntity(jwtToken, userEntity.getEmail(), JwtProvider.getExpirationDate(jwtToken));
//        tokenRepository.save(tokenEntity);
        return jwtToken;
    }

//    public UserEntity getLoginUserByEmail(String email) {
//        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
//        if (optionalUser.isEmpty()) return null;
//
//        return optionalUser.get();
//    }
//
//    public void checkPresentPassword(String password, String encodedPassword) {
//        if (!encoder.matches(password, encodedPassword))
//            throw new RestApiException(CustomErrorCode.NO_MATCH_PASSWORD);
//    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        UserEntity userEntity = getLoginUserByEmail(email);

//        UserEntity userEntity = UserEntity.toUserEntity(userDTO);
//
//        if (userEntity == null)
//            throw new UsernameNotFoundException("User not found with email: " + email);

        return User.builder()
                .username(email)
                .password("1234")
                .build();
    }

    //public InfoResponse getInfo(UserEntity userEntity) {
//        return InfoResponse.toInfoResponse(userEntity);
//    }

//    @Transactional
//    public void edit(EditRequest editRequest, UserEntity userEntity) {
//        String newPassword = editRequest.getNewPassword();
//        String newPasswordCheck = editRequest.getNewPasswordCheck();
//        if (!newPassword.isEmpty() || !newPasswordCheck.isEmpty()) {
//            if (!(newPassword.length() >= 4 && newPassword.length() <= 10))
//                throw new RestApiException(CustomErrorCode.INVALID_PASSWORD_FORMAT);
//            checkPasswordMatch(newPassword, editRequest.getNewPasswordCheck());
//            userEntity.edit(encoder.encode(newPassword), editRequest.getYear());
//        }
//        else userEntity.edit(userEntity.getPassword(), editRequest.getYear());
//    }
//
//    @Transactional
//    public void delete(String userPwd, UserEntity userEntity) {
//        checkPresentPassword(userPwd, userEntity.getPassword());
//        logout(userEntity);
//        userRepository.delete(userEntity);
//    }

//    @Transactional
//    public void logout(UserEntity userEntity) {
//        System.out.println("success");
//        Optional<TokenEntity> optionalToken = tokenRepository.findByEmail(userEntity.getEmail());
//        if (optionalToken.isEmpty()) return ;
//
//        TokenEntity tokenEntity = optionalToken.get();
//        tokenRepository.deleteByToken(tokenEntity.getToken());
//    }

//    @Transactional
//    @Scheduled(cron = "0 0 12 * * ?")
//    public void deleteExpiredVerificationCodeEntity() {
//        tokenRepository.deleteByExpiredAt(new Date());
//    }
}
