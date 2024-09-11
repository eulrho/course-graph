package graduatioin_project.course_graph.controller;

import graduatioin_project.course_graph.dto.*;
import graduatioin_project.course_graph.entity.UserEntity;
import graduatioin_project.course_graph.service.UserService;
import graduatioin_project.course_graph.token.JwtProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<CommonResponse> signUp(@Valid @RequestBody UserDTO userDTO) {
        userService.checkUserIdDuplicate(userDTO.getUserId());
        userService.checkNumUserId(userDTO.getUserId());
        userService.checkUserPwdMatch(userDTO.getUserPwd(), userDTO.getUserPwdCheck());
        userService.checkUserPwdDuplicate(userDTO.getUserPwd());
        userService.signUp(userDTO);
        return new ResponseEntity<>(new CommonResponse("회원가입에 성공했습니다."), HttpStatus.CREATED);
    }

    @GetMapping("/info")
    public ResponseEntity<InfoDTO> userInfo(Authentication auth) {
        UserEntity userEntity = userService.getLoginUserByUserId(auth.getName());
        return new ResponseEntity<>(InfoDTO.toInfoDTO(userEntity), HttpStatus.OK);
    }

    @GetMapping("/admin")
    public ResponseEntity<CommonResponse> adminPage() {
        return new ResponseEntity<>(new CommonResponse("관리자 페이지 접근 성공"), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<CommonResponse> login(@Valid @RequestBody LoginDTO loginDTO) {
        UserEntity userEntity = userService.login(loginDTO);
        String jwtToken = JwtProvider.createToken(userEntity.getUserId());
        return new ResponseEntity<>(new CommonResponse(jwtToken), HttpStatus.OK);
    }

    @PostMapping("/info-edit")
    public ResponseEntity<CommonResponse> infoEdit(@Valid @RequestBody EditDTO editDTO, Authentication auth) {
        UserEntity userEntity = userService.getLoginUserByUserId(auth.getName());
        userService.edit(editDTO, userEntity);
        return new ResponseEntity<>(new CommonResponse("회원 정보가 수정되었습니다."), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<CommonResponse> userDelete(@RequestBody DeleteRequest deleteRequest, Authentication auth) {
        UserEntity userEntity = userService.getLoginUserByUserId(auth.getName());
        userService.delete(deleteRequest.getUserPwd(), userEntity);
        return new ResponseEntity<>(new CommonResponse("회원 탈퇴되었습니다."), HttpStatus.OK);
    }
}
