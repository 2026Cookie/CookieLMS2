package com.wanted.cookielms.domain.lecture.controller;

import com.wanted.cookielms.domain.auth.dto.AuthDetails;
import com.wanted.cookielms.domain.lecture.dto.LectureInsDTO;
import com.wanted.cookielms.domain.lecture.dto.LectureStuDTO;
import com.wanted.cookielms.domain.lecture.service.InstructorService;
import com.wanted.cookielms.domain.lecture.service.LectureStuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

/**
 * [규칙 6] 강사 관련 요청 처리 전용 컨트롤러
 */
@Slf4j
@Controller
@RequestMapping("/instructor")
@RequiredArgsConstructor
public class InstructorController {

    private final InstructorService instructorService;
    private final LectureStuService lectureStuService;


    @GetMapping("/lectures")
    public String lectureList(

            @PageableDefault(size = 6, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal AuthDetails authDetails,
            Model model) {
        Long instructorId = authDetails.getLoginUserDTO().getUserId();
        // 서비스에서 Page 객체를 받아옵니다.
        Page<LectureStuDTO> lectures = instructorService.getMyLectures(instructorId, pageable);

        model.addAttribute("lectures", lectures);
        model.addAttribute("instructorId", instructorId);


        return "instructor/lecture_list";
    }
    @GetMapping("/main")
    public String main(){
        return "instructor/main";
    }

    @GetMapping("/lecture/detail/{id}")
    public String lectureDetail(@PathVariable("id") Long id, Model model) {

        // 이때 DB의 'lecture' 테이블에서 id로 데이터를 한 건 조회합니다.
        LectureStuDTO lecture = lectureStuService.getLectureDetail(id);

        // HTML(lecture_detail.html)에서 사용할 수 있도록 모델에 담습니다.
        model.addAttribute("lecture", lecture);
        return "user/lecture_detail";
    }
    /**
     * 강의 등록 페이지 이동
     */
    @GetMapping("/lecture/regist")
    public String registPage(Model model) {
        model.addAttribute("lecture", new LectureInsDTO()); // 빈 바구니를 넣어줘야 에러가 안 납니다!
        model.addAttribute("isEdit", false);
        return "instructor/lecture_regist";
    }

    /**
     * 강의 등록 실행
     * [이미지 예시 스타일] 파라미터에서 DTO를 직접 선언하여 수신
     */
    @PostMapping("/lecture/regist")
    public String registLecture(
            LectureInsDTO lectureInsDTO,

            @AuthenticationPrincipal AuthDetails authDetails,
            RedirectAttributes redirectAttributes) {
        try {
            Long instructorId = authDetails.getLoginUserDTO().getUserId();
            instructorService.registLecture(lectureInsDTO,instructorId);
            log.info("✅ 강의 등록 성공: {}", lectureInsDTO.getTitle());
            // 성공 시 FlashAttribute 사용
            redirectAttributes.addFlashAttribute("message", "✅ 강의 등록이 완료되었습니다!");
            return "redirect:/instructor/lectures";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/instructor/lecture/regist";

        } catch (IOException e) {
            log.error("❌ 파일 저장 에러", e);
            redirectAttributes.addFlashAttribute("errorMessage", "파일 저장 중 오류가 발생했습니다.");
            return "redirect:/instructor/lecture/regist";
            //return "redirect:/instructor/lectures";
        }


    }


    /**
     * 1. 수정 페이지로 이동 (GET)
     */
    @GetMapping("/lecture/edit/{id}")
    public String inseditPage(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal AuthDetails authDetails, // 시큐리티 세션 추가
            Model model) {

        // 세션에서 로그인한 강사 ID 추출
        Long instructorId = authDetails.getLoginUserDTO().getUserId();

        // 서비스 호출 시 instructorId 추가 전달
        LectureInsDTO lecture = instructorService.getLectureForEdit(id, instructorId);

        model.addAttribute("lecture", lecture);
        model.addAttribute("isEdit", true);
        return "instructor/lecture_regist";
    }

    /**
     * 2. 수정 실행 (POST)
     */
    @PostMapping("/lecture/edit/{id}")
    public String updateLecture(
            @PathVariable("id") Long id,
            @ModelAttribute LectureInsDTO lectureInsDTO,
            @AuthenticationPrincipal AuthDetails authDetails, // 시큐리티 세션 추가
            RedirectAttributes redirectAttributes) {
        try {
            // 세션에서 로그인한 강사 ID 추출
            Long instructorId = authDetails.getLoginUserDTO().getUserId();

            // 서비스 호출 시 instructorId 추가 전달
            instructorService.updateLecture(id, lectureInsDTO, instructorId);

            redirectAttributes.addFlashAttribute("message", "강의 수정이 완료되었습니다!");
            return "redirect:/instructor/lectures";

        } catch (IllegalArgumentException e) {
            log.error("수정 중 보안/검증 오류 발생: ", e);
            // 서비스에서 던진 에러 메시지("본인의 강의만 수정할 수 있습니다.")를 화면에 전달
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/instructor/lecture/edit/" + id;
        } catch (Exception e) {
            log.error("수정 중 시스템 오류 발생: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "수정 중 오류가 발생했습니다.");
            return "redirect:/instructor/lecture/edit/" + id;
        }
    }

}