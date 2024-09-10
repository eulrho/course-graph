package graduatioin_project.course_graph.dto;

import graduatioin_project.course_graph.entity.UserEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class InfoDTO {
    private String userId;
    private int trackId;

    public static InfoDTO toInfoDTO(UserEntity userEntity) {
        InfoDTO infoDTO = new InfoDTO();
        infoDTO.setUserId(userEntity.getUserId());
        infoDTO.setTrackId(userEntity.getTrackId());
        return infoDTO;
    }
}
