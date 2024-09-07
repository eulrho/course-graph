package graduatioin_project.course_graph.entity;

import graduatioin_project.course_graph.dto.UserDTO;
import graduatioin_project.course_graph.enums.UserRole;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Table(name = "user")
public class UserEntity {
    @Id
    private String userId;
    private String userPwd;
    private int trackId;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Builder
    public static UserEntity toUserEntity(UserDTO userDTO) {
        UserEntity userEntity = new UserEntity();

        userEntity.userId = userDTO.getUserId();
        userEntity.userPwd = userDTO.getUserPwd();
        userEntity.trackId = userDTO.getTrackId();
        userEntity.role = userDTO.getRole();
        return userEntity;
    }

    public void edit(String newUserPwd, int newTrackId) {
        this.userPwd = newUserPwd;
        this.trackId = newTrackId;
    }
}
