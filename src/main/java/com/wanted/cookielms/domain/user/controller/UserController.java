package com.wanted.cookielms.domain.user.controller;

import com.wanted.cookielms.domain.user.dto.JoinUserDTO;
import com.wanted.cookielms.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


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


}