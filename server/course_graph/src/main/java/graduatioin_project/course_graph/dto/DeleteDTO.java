package graduatioin_project.course_graph.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeleteDTO {
    @NotBlank
    private String userPwd;
}
