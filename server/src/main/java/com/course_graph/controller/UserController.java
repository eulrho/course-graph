package com.course_graph.controller;

import com.course_graph.dto.*;
import com.course_graph.token.JwtProvider;
import com.course_graph.entity.UserEntity;
import com.course_graph.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/api/join")
    public ResponseEntity<CommonResponse> join(@Valid @RequestBody UserDTO userDTO) {
        userService.join(userDTO);
        return new ResponseEntity<>(new CommonResponse("회원가입에 성공했습니다."), HttpStatus.CREATED);
    }

    @PostMapping("/api/login")
    public ResponseEntity<CommonResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        String jwtToken = userService.login(loginRequest);;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtProvider.authorizationHeader, "Bearer " + jwtToken);
        return new ResponseEntity<>(new CommonResponse(jwtToken), httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/api/info")
    public ResponseEntity<InfoResponse> userInfo(Authentication auth) {
        UserEntity userEntity = userService.getLoginUserByEmail(auth.getName());
        return new ResponseEntity<>(userService.getInfo(userEntity), HttpStatus.OK);
    }

    @PatchMapping("/api/info")
    public ResponseEntity<CommonResponse> infoEdit(@Valid @RequestBody EditRequest editRequest, Authentication auth) {
        UserEntity userEntity = userService.getLoginUserByEmail(auth.getName());
        userService.edit(editRequest, userEntity);
        return new ResponseEntity<>(new CommonResponse("회원 정보가 수정되었습니다."), HttpStatus.OK);
    }

    @DeleteMapping("/api/delete")
    public ResponseEntity<CommonResponse> userDelete(@Valid @RequestBody DeleteRequest deleteRequest, Authentication auth) {
        UserEntity userEntity = userService.getLoginUserByEmail(auth.getName());
        userService.delete(deleteRequest.getPassword(), userEntity);
        return new ResponseEntity<>(new CommonResponse("회원 탈퇴되었습니다."), HttpStatus.OK);
    }

    @PostMapping("/api/logout")
    public ResponseEntity<CommonResponse> userLogout(Authentication auth) {
        System.out.println("test");
        UserEntity userEntity = userService.getLoginUserByEmail(auth.getName());
        userService.logout(userEntity);
        return new ResponseEntity<>(new CommonResponse("로그아웃되었습니다."), HttpStatus.OK);
    }
}
