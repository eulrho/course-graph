package graduatioin_project.course_graph.service;

import graduatioin_project.course_graph.dto.LoginDTO;
import graduatioin_project.course_graph.dto.UserDTO;
import graduatioin_project.course_graph.entity.UserEntity;
import graduatioin_project.course_graph.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public void signUp(UserDTO userDTO) {
        userDTO.setUPwd(encoder.encode(userDTO.getUPwd()));
        UserEntity userEntity = UserEntity.toUserEntity(userDTO);
        userRepository.save(userEntity);
    }

    public boolean checkNumUId(String uId) {
        try {
            Integer.parseInt(uId);

            int tempInt = Integer.parseInt(uId.substring(0, 2));

            if (!(tempInt >= 20 && tempInt <= 24))
                throw new IllegalStateException("학번은 20-24학년도 범위 내여야 합니다.");
            return true;
        } catch (NumberFormatException ex) {
            System.out.println("유효하지 않은 학번입니다.");
            return false;
        }
    }

    public boolean checkUIdDuplicate(String uId)
    {
        return userRepository.findByuId(uId).isPresent();
    }

    public boolean checkUPwdDuplicate(String uPwd)
    {
        return userRepository.findByuPwd(uPwd).isPresent();
    }

    public UserEntity login(LoginDTO loginDTO) {
        Optional<UserEntity> optionalUser = userRepository.findByuId(loginDTO.getUId());
        if (optionalUser.isEmpty()) {
            return null;
        }

        UserEntity userEntity = optionalUser.get();
        if (!encoder.matches(loginDTO.getUPwd(), userEntity.getUPwd())) {
            return null;
        }

        return userEntity;
    }

    public UserEntity getLoginUserByUId(String uId) {
        if (uId == null) return null;

        Optional<UserEntity> optionalUser = userRepository.findByuId(uId);
        if (optionalUser.isEmpty()) return null;

        return optionalUser.get();
    }

    @Override
    public UserDetails loadUserByUsername(String uId) throws UsernameNotFoundException {
        UserEntity userEntity = getLoginUserByUId(uId);

        if (userEntity == null) {
            throw new UsernameNotFoundException(uId);
        }
        return User.builder()
                .username(userEntity.getUId())
                .password(userEntity.getUPwd())
                .roles(userEntity.getRole().toString())
                .build();
    }
}
