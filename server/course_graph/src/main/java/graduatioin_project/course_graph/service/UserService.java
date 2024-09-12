package graduatioin_project.course_graph.service;

import graduatioin_project.course_graph.Exception.RestApiException;
import graduatioin_project.course_graph.dto.EditDTO;
import graduatioin_project.course_graph.dto.LoginDTO;
import graduatioin_project.course_graph.dto.UserDTO;
import graduatioin_project.course_graph.entity.UserEntity;
import graduatioin_project.course_graph.enums.CustomErrorCode;
import graduatioin_project.course_graph.repository.UserRepository;
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

    public void signUp(UserDTO userDTO) {
        userDTO.setUserPwd(encoder.encode(userDTO.getUserPwd()));
        UserEntity userEntity = UserEntity.toUserEntity(userDTO);
        userRepository.save(userEntity);
    }

    public void checkNumUserId(String uId) {

        try {
            Long.parseLong(uId);
        }
        catch (NumberFormatException e) {
            throw new RestApiException(CustomErrorCode.INVALID_USER_ID);
        }
        int tempInt = Integer.parseInt(uId.substring(0, 2));
        if (!(tempInt >= 20 && tempInt <= 24))
            throw new RestApiException(CustomErrorCode.OUT_OF_BOUND_USER_ID);
    }

    public void checkUserIdDuplicate(String userId)
    {
        userRepository.findByUserId(userId).ifPresent(m -> {
            throw new RestApiException(CustomErrorCode.DUPLICATE_USER_ID);});
    }

    public void checkUserPwdMatch(String userPwd, String userPwdCheck)
    {
        if (!userPwd.equals(userPwdCheck))
            throw new RestApiException(CustomErrorCode.NO_MATCH_USER_PASSWORD);
    }

    public void checkUserPwdDuplicate(String userPwd, String userNewPwd)
    {
        if (userPwd.equals(userNewPwd))
            throw new RestApiException(CustomErrorCode.DUPLICATE_USER_PASSWORD);
    }

    public void checkUserPwdLength(String userPwd)
    {
        if (userPwd.length() < 4 || userPwd.length() > 10)
            throw new RestApiException(CustomErrorCode.INVALID_USER_PASSWORD);
    }

    public UserEntity login(LoginDTO loginDTO) {
        Optional<UserEntity> optionalUser = userRepository.findByUserId(loginDTO.getUserId());
        if (optionalUser.isEmpty())
            throw new RestApiException(CustomErrorCode.NO_MATCH_USER_ID);

        UserEntity userEntity = optionalUser.get();
        checkUserPresentPassword(loginDTO.getUserPwd(), userEntity.getUserPwd());
        return userEntity;
    }

    public UserEntity getLoginUserByUserId(String userId) {
        if (userId == null) return null;

        Optional<UserEntity> optionalUser = userRepository.findByUserId(userId);
        if (optionalUser.isEmpty()) return null;

        return optionalUser.get();
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        UserEntity userEntity = getLoginUserByUserId(userId);

        if (userEntity == null) {
            throw new UsernameNotFoundException(userId);
        }
        return User.builder()
                .username(userEntity.getUserId())
                .password(userEntity.getUserPwd())
                .roles(userEntity.getRole().toString())
                .build();
    }

    public void checkUserPresentPassword(String userPwd, String encodedPwd) {
        if (!encoder.matches(userPwd, encodedPwd))
            throw new RestApiException(CustomErrorCode.NO_MATCH_USER_PASSWORD);
    }

    @Transactional
    public void edit(EditDTO editDTO, UserEntity userEntity) {
        checkUserPresentPassword(editDTO.getUserPresentPwd(), userEntity.getUserPwd());
        if (!editDTO.getUserNewPwd().isEmpty()) {
            checkUserPwdLength(editDTO.getUserNewPwd());
            checkUserPwdMatch(editDTO.getUserNewPwd(), editDTO.getUserNewPwdCheck());
            checkUserPwdDuplicate(editDTO.getUserPresentPwd(), editDTO.getUserNewPwd());
            userEntity.edit(encoder.encode(editDTO.getUserNewPwd()), editDTO.getTrackId());
        }
        else userEntity.edit(userEntity.getUserPwd(), editDTO.getTrackId());
    }

    @Transactional
    public void delete(String userPwd, UserEntity userEntity) {
        checkUserPresentPassword(userPwd, userEntity.getUserPwd());
        userRepository.delete(userEntity);
    }
}
