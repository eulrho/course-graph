package graduatioin_project.course_graph.controller;

import graduatioin_project.course_graph.dto.LoginDTO;
import graduatioin_project.course_graph.dto.UserDTO;
import graduatioin_project.course_graph.entity.UserEntity;
import graduatioin_project.course_graph.service.UserService;
import graduatioin_project.course_graph.token.JwtProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.bind.annotation.ModelAttribute;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public String signUp(@Valid @RequestBody UserDTO userDTO) {
        if (userService.checkUIdDuplicate(userDTO.getUId())) {
            return "이미 존재하는 회원입니다.";
        } else if (!userService.checkNumUId(userDTO.getUId())) {
            return "유효하지 않은 학번입니다.";
        }

        if (!userDTO.getUPwd().equals(userDTO.getUPwdCheck())) {
            return "비밀번호가 일치하지 않습니다.";
        } else if (userService.checkUPwdDuplicate(userDTO.getUPwd())) {
            return "사용할 수 없는 비밀번호입니다.";
        }

        userService.signUp(userDTO);
        return "회원가입 성공";
    }

    @GetMapping("/info")
    public String userInfo(Authentication auth) {
        UserEntity userEntity = userService.getLoginUserByUId(auth.getName());

        return String.format("uId : %s\nrole : %s\ntrId : %d",
                userEntity.getUId(), userEntity.getRole(), userEntity.getTrId());
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "admin";
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginDTO loginDTO) {
        UserEntity userEntity = userService.login(loginDTO);

        // invalid id or password
        if (userEntity == null) {
            return "학번 또는 비밀번호가 틀렸습니다.";
        }

        String jwtToken = JwtProvider.createToken(userEntity.getUId());

        return jwtToken;
    }

}
