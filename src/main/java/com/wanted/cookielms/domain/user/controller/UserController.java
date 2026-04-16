package com.wanted.cookielms.domain.user.controller;

import org.springframework.ui.Model;
import com.wanted.cookielms.domain.user.dto.JoinUserDTO;
import com.wanted.cookielms.domain.user.dto.LoginUserDTO;
import com.wanted.cookielms.domain.user.dto.ModifyUserInfo;
import com.wanted.cookielms.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import com.wanted.cookielms.domain.user.dto.ResetPasswordDTO;

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

    @GetMapping("/find_id")
    public String findId() {
        return "user/find_id";
    }

    @PostMapping("/find_id")
    public String findId(@RequestParam String name,
                         @RequestParam String phone,
                         Model model) {
        String loginId = userService.findLoginIdByNameAndPhone(name, phone);
        if (loginId == null) {
            model.addAttribute("error", "일치하는 회원 정보가 없습니다.");
        } else {
            model.addAttribute("loginId", loginId);
        }
        return "user/find_id";
    }


    @GetMapping("/find_password")
    public String findPassword() {
        return "user/find_password";
    }

    @PostMapping("/find_password")
    public String findPassword(@RequestParam String loginId,
                               @RequestParam String name,
                               @RequestParam String phone,
                               HttpSession session,
                               Model model) {
        boolean verified = userService.findByLoginIdAndNameAndPhone(loginId, name, phone);
        if (!verified) {
            model.addAttribute("error", "일치하는 회원 정보가 없습니다.");
            return "user/find_password";
        }
        session.setAttribute("resetLoginId", loginId);
        return "redirect:/user/reset_password";
    }

    @GetMapping("/reset_password")
    public String resetPassword(HttpSession session, Model model) {
        if (session.getAttribute("resetLoginId") == null) {
            return "redirect:/user/find_password";
        }
        model.addAttribute("resetPasswordDTO", new ResetPasswordDTO());
        return "user/reset_password";
    }

    @PostMapping("/reset_password")
    public String resetPassword(@Valid @ModelAttribute ResetPasswordDTO resetPasswordDTO,
                                BindingResult bindingResult,
                                HttpSession session,
                                Model model) {
        if (session.getAttribute("resetLoginId") == null) {
            return "redirect:/user/find_password";
        }
        if (bindingResult.hasErrors()) {
            return "user/reset_password";
        }
        if (!resetPasswordDTO.getNewPassword().equals(resetPasswordDTO.getNewPasswordConfirm())) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "user/reset_password";
        }
        String loginId = (String) session.getAttribute("resetLoginId");
        userService.updatePassword(loginId, resetPasswordDTO.getNewPassword());
        session.removeAttribute("resetLoginId");
        return "redirect:/user/login";
    }

    //마이페이지 정보 조회용
    @GetMapping("/mypage")
    public String mypage(Model model) {
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        LoginUserDTO userInfo = userService.findByUsername(loginId);
        model.addAttribute("userInfo", userInfo);
        return "user/mypage";
    }

    @GetMapping("/verify-password")
    public String verifyPassword() {
        return "user/verify_password";
    }

    @PostMapping("/verify-password")
    public String verifyPassword(@RequestParam String password,
                                 HttpSession session,
                                 Model model) {
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean verified = userService.verifyPassword(loginId, password);
        if (!verified) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "user/verify_password";
        }
        session.setAttribute("verifiedLoginId", loginId);
        return "redirect:/user/mypage/info";
    }

    @GetMapping("/mypage/info")
    public String mypageInfo(HttpSession session, Model model) {
        if (session.getAttribute("verifiedLoginId") == null) {
            return "redirect:/user/verify-password";
        }
        String loginId = (String) session.getAttribute("verifiedLoginId");
        LoginUserDTO userInfo = userService.findByUsername(loginId);
        model.addAttribute("userInfo", userInfo);
        return "user/mypage_info";
    }

    @GetMapping("/mypage/edit")
    public String mypageEdit(HttpSession session, Model model) {
        if (session.getAttribute("verifiedLoginId") == null) {
            return "redirect:/user/verify-password";
        }
        String loginId = (String) session.getAttribute("verifiedLoginId");
        LoginUserDTO userInfo = userService.findByUsername(loginId);
        model.addAttribute("userInfo", userInfo);
        model.addAttribute("modifyUserInfo", new ModifyUserInfo());
        return "user/mypage_edit";
    }

    @PostMapping("/mypage/edit")
    public String mypageEdit(@Valid @ModelAttribute ModifyUserInfo modifyUserInfo,
                             BindingResult bindingResult,
                             HttpSession session,
                             Model model) {
        if (session.getAttribute("verifiedLoginId") == null) {
            return "redirect:/user/verify-password";
        }
        if (bindingResult.hasErrors()) {
            return "user/mypage_edit";
        }
        String loginId = (String) session.getAttribute("verifiedLoginId");
        boolean result = userService.updateUserInfo(loginId, modifyUserInfo);
        if (!result) {
            model.addAttribute("error", "현재 비밀번호가 일치하지 않거나 새 비밀번호가 맞지 않습니다.");
            return "user/mypage_edit";
        }
        return "redirect:/user/mypage/info";
    }



}