package graduatioin_project.course_graph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import graduatioin_project.course_graph.entity.UserEntity;
import graduatioin_project.course_graph.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
