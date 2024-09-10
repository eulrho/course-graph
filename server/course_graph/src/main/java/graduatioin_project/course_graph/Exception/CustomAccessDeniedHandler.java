package graduatioin_project.course_graph.Exception;

import graduatioin_project.course_graph.enums.CustomErrorCode;
import graduatioin_project.course_graph.token.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        ErrorCode errorCode = CustomErrorCode.FORBIDDEN_ACCESS;
        JwtAuthenticationFilter.setErrorResponse(response, errorCode);
    }
}
