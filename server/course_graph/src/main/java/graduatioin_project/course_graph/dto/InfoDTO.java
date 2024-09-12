package graduatioin_project.course_graph.dto;

import graduatioin_project.course_graph.entity.UserEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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

    public static List<InfoDTO> toInfoDTOList(List<UserEntity> userEntityList) {
        List<InfoDTO> infoDTOList = new ArrayList<>();
        for (UserEntity userEntity : userEntityList) {
            InfoDTO infoDTO = toInfoDTO(userEntity);
            if (infoDTO.getUserId().equals("admin"))
                continue;
            infoDTOList.add(infoDTO);
        }
        return infoDTOList;
    }
}
