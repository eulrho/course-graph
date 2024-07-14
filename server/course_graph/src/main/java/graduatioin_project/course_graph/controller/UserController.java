package graduatioin_project.course_graph.controller;

import graduatioin_project.course_graph.dto.UserDTO;
import graduatioin_project.course_graph.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor // UserService에 대한 멤버 사용 가능
public class UserController {
    private final UserService userService;

    @GetMapping("/signup")
    public String signUpForm() {
        return "signup";
    }

    @PostMapping("/signup")
    public String save(@ModelAttribute UserDTO userDTO) {
        System.out.println("UserController.signUp");
        System.out.println("userDTO = " + userDTO);
        userService.signUp(userDTO);

        return "index";
    }
}
