package graduatioin_project.course_graph.entity;

import graduatioin_project.course_graph.dto.UserDTO;
import graduatioin_project.course_graph.enums.UserRole;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "user")
public class UserEntity {
    @Id
    private String uId;
    private String uPwd;
    private int trId;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Builder
    public static UserEntity toUserEntity(UserDTO userDTO) {
        UserEntity userEntity = new UserEntity();

        userEntity.uId = userDTO.getUId();
        userEntity.uPwd = userDTO.getUPwd();
        userEntity.trId = userDTO.getTrId();
        userEntity.role = UserRole.USER;

        return userEntity;
    }

    public void edit(String newUPwd, int newTrId) {
        this.uPwd = newUPwd;
        this.trId = newTrId;
    }
}
