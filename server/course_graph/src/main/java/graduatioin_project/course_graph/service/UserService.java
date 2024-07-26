package graduatioin_project.course_graph.service;

import graduatioin_project.course_graph.dto.EditDTO;
import graduatioin_project.course_graph.dto.LoginDTO;
import graduatioin_project.course_graph.dto.UserDTO;
import graduatioin_project.course_graph.entity.UserEntity;
import graduatioin_project.course_graph.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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
            System.out.println("학번은 0-9까지의 숫자로 이루어져 있어야 합니다.");
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

    public boolean checkUPresentPassword(UserEntity userEntity, EditDTO editDTO) {
        if (!encoder.matches(editDTO.getUPresentPwd(), userEntity.getUPwd())) {
            return false;
        }
        return true;
    }

    public boolean checkEditPassword(EditDTO editDTO) {
        if (!editDTO.getUNewPwd().isEmpty()) {
            if (checkUPwdDuplicate(editDTO.getUNewPwd())) {
                return false;
            }
            if (!editDTO.getUNewPwd().equals(editDTO.getUNewPwdCheck())) {
                return false;
            }
            int length = editDTO.getUNewPwd().length();

            if (length < 4 || length > 10) {
                return false;
            }
        }
        return true;
    }

    @Transactional
    public boolean edit(EditDTO editDTO, UserEntity userEntity) {
        if (!checkEditPassword(editDTO))
            return false;
        if (editDTO.getUNewPwd().isEmpty())
            userEntity.edit(userEntity.getUPwd(), editDTO.getTrId());
        else
            userEntity.edit(encoder.encode(editDTO.getUNewPwd()), editDTO.getTrId());
        return true;
    }
}
