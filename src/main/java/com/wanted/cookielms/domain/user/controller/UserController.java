package com.wanted.cookielms.domain.user.controller;

import org.springframework.ui.Model;
import com.wanted.cookielms.domain.user.dto.JoinUserDTO;
import com.wanted.cookielms.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @GetMapping("/join")
    public String join(){
        return("user/join");
    }


    @PostMapping("/join")
    public String join(@Valid @ModelAttribute JoinUserDTO joinUserDTO, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return "user/join";
        }
        String result = userService.join(joinUserDTO);
        if (result == null) {
            return "redirect:/user/join";
        }
        return "redirect:/user/joinsuccess";
    }



    @GetMapping("/joinsuccess")
    public String joinSuccess(){
        return("user/joinsuccess");
    }

    @GetMapping("/login")
    public String login(){
        return "user/login";
    }


    @GetMapping("/main")
    public String main(){
        return "user/main";
    }

    @GetMapping("/enrollments")
    public String enrollment() {
        return "user/enrollments";
    }

    // GET: 아이디 찾기 폼 페이지
    @GetMapping("/find_id")
    public String findIdForm() {
        return "user/find_id";
    }

    // POST: 이름 + 전화번호로 아이디 조회
    @PostMapping("/find_id")
    public String findId(@RequestParam String name,
                         @RequestParam String phone,
                         Model model) {
        String loginId = userService.findLoginIdByNameAndPhone(name, phone);
        if (loginId == null) {
            model.addAttribute("error", "일치하는 정보가 없습니다.");
        } else {
            model.addAttribute("loginId", loginId);
        }
        return "user/find_id";
    }


}