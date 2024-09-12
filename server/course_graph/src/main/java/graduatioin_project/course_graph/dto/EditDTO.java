package graduatioin_project.course_graph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class EditDTO {
    private String userId;

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String userPresentPwd;

    private String userNewPwd;
    private String userNewPwdCheck;
    private int trackId;
}
