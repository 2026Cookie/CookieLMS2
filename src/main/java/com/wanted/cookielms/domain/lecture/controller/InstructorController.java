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
import java.util.Map;

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
     */
    @PostMapping("/lecture/regist")
    public String registLecture(
            @ModelAttribute("lecture") LectureInsDTO lectureInsDTO,
            @AuthenticationPrincipal AuthDetails authDetails,
            RedirectAttributes redirectAttributes) throws IOException {
        Long instructorId = authDetails.getLoginUserDTO().getUserId();
        instructorService.registLecture(lectureInsDTO, instructorId);
        log.info("✅ 강의 등록 성공: {}", lectureInsDTO.getTitle());
        redirectAttributes.addFlashAttribute("message", "✅ 강의 등록이 완료되었습니다!");
        return "redirect:/instructor/lectures";
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
            @ModelAttribute("lecture") LectureInsDTO lectureInsDTO,
            @AuthenticationPrincipal AuthDetails authDetails,
            RedirectAttributes redirectAttributes) throws IOException {
        Long instructorId = authDetails.getLoginUserDTO().getUserId();
        instructorService.updateLecture(id, lectureInsDTO, instructorId);
        redirectAttributes.addFlashAttribute("message", "강의 수정이 완료되었습니다!");
        return "redirect:/instructor/lectures";
    }
    @GetMapping("/lecture/{lectureId}/assignment/{assignmentId}/status")
    public String getAssignmentStatus(
            @PathVariable Long lectureId,
            @PathVariable Long assignmentId,
            Model model) {

        // 1. 서비스 호출하여 대시보드 데이터(학생 목록, 통계, 과제 정보) 가져오기
        Map<String, Object> dashboardData = instructorService.getAssignmentDashboard(assignmentId, lectureId);

        // 2. 맵에 담긴 모든 데이터를 모델에 추가 (Thymeleaf에서 바로 사용 가능)
        // students, assignment, totalCount, submittedCount 등이 들어있습니다.
        model.addAllAttributes(dashboardData);

        // 3. 과제 제출 현황 화면(HTML)으로 이동
        return "instructor/assignment_status";
    }
}