package graduatioin_project.course_graph.entity;

import graduatioin_project.course_graph.dto.UserDTO;
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

    @Column(length = 10)
    private String uPwd;

    @Column
    private int trId;

    @Builder
    public static UserEntity toUserEntity(UserDTO userDTO) {
        UserEntity userEntity = new UserEntity();

        userEntity.uId = userDTO.getUId();
        userEntity.uPwd = userDTO.getUPwd();
        userEntity.trId = userDTO.getTrId();

        return userEntity;
    }
}
