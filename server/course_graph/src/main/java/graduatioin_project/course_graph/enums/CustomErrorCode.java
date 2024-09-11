package graduatioin_project.course_graph.enums;

import graduatioin_project.course_graph.Exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CustomErrorCode implements ErrorCode {
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "요청한 값이 올바르지 않습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생하였습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    INVALID_PERMISSION(HttpStatus.UNAUTHORIZED, "권한이 없습니다."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    DUPLICATE_USER_ID(HttpStatus.CONFLICT, "이미 존재하는 학번입니다."),
    INVALID_USER_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 학번입니다."),
    INVALID_USER_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    DUPLICATE_USER_PASSWORD(HttpStatus.CONFLICT, "사용할 수 없는 비밀번호입니다."),
    NO_MATCH_USER_ID(HttpStatus.BAD_REQUEST, "학번이 일치하지 않습니다."),
    NO_MATCH_USER_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    OUT_OF_BOUND_USER_ID(HttpStatus.BAD_REQUEST, "학번은 20-24학년도 범위 내여야 합니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
