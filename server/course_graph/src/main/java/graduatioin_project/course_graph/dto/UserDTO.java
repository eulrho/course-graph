package graduatioin_project.course_graph.dto;

import graduatioin_project.course_graph.entity.UserEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserDTO {
    private String uId;
    private String uPwd;
    private int trId;

    public static UserDTO toUserDTO(UserEntity userEntity) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUId(userEntity.getUId());
        userDTO.setUPwd(userEntity.getUPwd());
        userDTO.setTrId(userEntity.getTrId());

        return userDTO;
    }
}
