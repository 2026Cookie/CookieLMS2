package com.wanted.cookielms.domain.user.controller;

import com.wanted.cookielms.domain.auth.annotation.CurrentLoginId;
import org.springframework.ui.Model;
import com.wanted.cookielms.domain.user.dto.JoinUserDTO;
import com.wanted.cookielms.domain.user.dto.ModifyUserInfo;
import com.wanted.cookielms.domain.user.dto.MypageDTO;
import com.wanted.cookielms.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpSession;
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
        userService.join(joinUserDTO);
        return "redirect:/user/joinsuccess";
    }



    @GetMapping("/joinsuccess")
    public String joinSuccess(){
        return("user/joinsuccess");
    }

    @GetMapping("/login")
    public String login(HttpSession session, Model model){
        String loginError = (String) session.getAttribute("loginError");
        if (loginError != null) {
            model.addAttribute("loginError", loginError);
            session.removeAttribute("loginError");
        }
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


    @GetMapping("/find_id")
    public String findIdForm() {
        return "user/find_id";
    }


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


    @GetMapping("/mypage")
    public String mypage(@CurrentLoginId String loginId, Model model) {
        MypageDTO mypage = userService.getMypage(loginId);
        model.addAttribute("userInfo", mypage);
        return "user/mypage";
    }

    @GetMapping("/verify-password")
    public String verifyPassword() {
        return "user/verify_password";
    }

    @PostMapping("/verify-password")
    public String verifyPassword(@RequestParam String password,
                                 @CurrentLoginId String loginId,
                                 HttpSession session,
                                 Model model) {
        boolean verified = userService.verifyPassword(loginId, password);
        if (!verified) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "user/verify_password";
        }
        session.setAttribute("verifiedLoginId", loginId);
        return "redirect:/user/mypage/edit";
    }

    @GetMapping("/mypage/info")
    public String mypageInfo(@CurrentLoginId String loginId, Model model) {
        MypageDTO mypage = userService.getMypage(loginId);
        model.addAttribute("userInfo", mypage);
        return "user/mypage_info";
    }

    @GetMapping("/mypage/edit")
    public String mypageEdit(HttpSession session, Model model) {
        if (session.getAttribute("verifiedLoginId") == null) {
            return "redirect:/user/verify-password";
        }
        String loginId = (String) session.getAttribute("verifiedLoginId");
        MypageDTO mypage = userService.getMypage(loginId);

        ModifyUserInfo modifyUserInfo = new ModifyUserInfo();
        modifyUserInfo.setName(mypage.getName());
        modifyUserInfo.setNickname(mypage.getNickname());
        modifyUserInfo.setEmail(mypage.getEmail());
        modifyUserInfo.setPhone(mypage.getPhone());
        model.addAttribute("modifyUserInfo", modifyUserInfo);
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
        userService.updateUserInfo(loginId, modifyUserInfo);
        return "redirect:/user/mypage/info";
    }



    @GetMapping("/withdraw")
    public String withdraw() {
        return "user/withdraw";
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestParam String password,
                           @CurrentLoginId String loginId,
                           HttpSession session,
                           Model model) {
        userService.withdrawUser(loginId, password);
        session.invalidate();
        return "redirect:/";
    }


}
