package graduatioin_project.course_graph.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class LoginDTO {
    @NotBlank(message = "학번을 입력해주세요.")
    @JsonProperty("userId")
    private String userId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @JsonProperty("userPwd")
    private String userPwd;
}
