package com.course_graph.controller;

import com.course_graph.dto.CommonResponse;
import com.course_graph.dto.MailRequest;
import com.course_graph.dto.MemberMailVerifyRequest;
import com.course_graph.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/api/send-mail")
    public ResponseEntity<CommonResponse> sendMessage(@Valid @RequestBody MailRequest mailRequest) {
        memberService.sendMail(mailRequest.getEmail());
        return new ResponseEntity<>(new CommonResponse("이메일이 발송되었습니다."), HttpStatus.OK);
    }

    @PostMapping("/api/verify-mail")
    public ResponseEntity<CommonResponse> verifyMail(@Valid @RequestBody MemberMailVerifyRequest memberMailVerifyRequest) {
        memberService.verifyCode(memberMailVerifyRequest);
        return new ResponseEntity<>(new CommonResponse("인증되었습니다."), HttpStatus.OK);
    }
}
