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

    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Size(min = 4, max = 10, message = "비밀번호는 4-10자여야 합니다.")
    @JsonProperty("userNewPwd")
    private String userNewPwd;

    @NotBlank(message = "비밀번호를 다시 입력해주세요.")
    @JsonProperty("userNewPwdCheck")
    private String userNewPwdCheck;

    private int trackId;
}
