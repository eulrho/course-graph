package graduatioin_project.course_graph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class EditDTO {
    @JsonProperty("uId")
    private String uId;

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    @JsonProperty("uPresentPwd")
    private String uPresentPwd;

    @JsonProperty("uNewPwd")
    private String uNewPwd;

    @JsonProperty("uNewPwdCheck")
    private String uNewPwdCheck;

    private int trId;
}
