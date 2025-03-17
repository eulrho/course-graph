package com.course_graph.enums;

import com.course_graph.Exception.ErrorCode;
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
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    INVALID_USER_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 학번입니다."),
    INVALID_USER_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.CONFLICT, "비밀번호는 4-10자여야 합니다."),
    NO_MATCH_EMAIL(HttpStatus.BAD_REQUEST, "존재하지 않는 이메일입니다."),
    NO_MATCH_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    DUPLICATE_LOGIN(HttpStatus.CONFLICT, "이미 로그인 중입니다."),
    FAILED_TO_SEND_MAIL(HttpStatus.INTERNAL_SERVER_ERROR, "메일 전송에 실패하였습니다."),
    NO_MATCH_CODE(HttpStatus.BAD_REQUEST, "인증 코드가 일치하지 않습니다."),
    EXPIRED_CODE(HttpStatus.UNAUTHORIZED, "인증 코드가 만료되었습니다."),
    NOT_VERIFIED(HttpStatus.UNAUTHORIZED, "이메일 인증이 필요합니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
