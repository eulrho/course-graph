package graduatioin_project.course_graph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import graduatioin_project.course_graph.entity.UserEntity;
import graduatioin_project.course_graph.enums.UserRole;
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
    @NotBlank(message = "학번을 입력해주세요.")
    @Size(min = 10, max = 10, message = "학번은 10자여야 합니다.")
    @JsonProperty("uId")
    private String uId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 4, max = 10, message = "비밀번호는 4-10자여야 합니다.")
    @JsonProperty("uPwd")
    private String uPwd;

    @NotBlank(message = "비밀번호를 다시 입력해주세요.")
    @JsonProperty("uPwdCheck")
    private String uPwdCheck;
    private int trId;
    private UserRole role;

    public static UserDTO toUserDTO(UserEntity userEntity) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUId(userEntity.getUId());
        userDTO.setUPwd(userEntity.getUPwd());
        userDTO.setTrId(userEntity.getTrId());
        userDTO.setRole(userEntity.getRole());
        return userDTO;
    }
}
