package graduatioin_project.course_graph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class EditDTO {
    @JsonProperty("userId")
    private String userId;

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    @JsonProperty("userPresentPwd")
    private String userPresentPwd;

    @JsonProperty("userNewPwd")
    private String userNewPwd;

    @JsonProperty("userNewPwdCheck")
    private String userNewPwdCheck;

    private int trackId;
}
