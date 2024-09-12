package graduatioin_project.course_graph.dto;

import graduatioin_project.course_graph.entity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserDTO {
    @NotBlank
    @Size(min = 10, max = 10, message = "학번은 10자여야 합니다.")
    private String userId;

    @NotBlank
    @Size(min = 4, max = 10, message = "비밀번호는 4-10자여야 합니다.")
    private String userPwd;

    @NotBlank
    private String userPwdCheck;
    private int trackId;

    public static UserDTO toUserDTO(UserEntity userEntity) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userEntity.getUserId());
        userDTO.setUserPwd(userEntity.getUserPwd());
        userDTO.setTrackId(userEntity.getTrackId());
        return userDTO;
    }
}
