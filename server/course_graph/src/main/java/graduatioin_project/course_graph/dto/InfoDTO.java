package graduatioin_project.course_graph.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class InfoDTO {
    private String userId;
    private String trackName;

    public static InfoDTO toInfoDTO(String userID, String trackName) {
        InfoDTO infoDTO = new InfoDTO();
        infoDTO.setUserId(userID);
        infoDTO.setTrackName(trackName);
        return infoDTO;
    }
}
