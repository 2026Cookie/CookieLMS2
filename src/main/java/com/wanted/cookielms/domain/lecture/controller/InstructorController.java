package com.wanted.cookielms.domain.lecture.controller;

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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
            Model model) {
        Long instructorId = 1L; // TODO: 로그인 연동 후 세션 기반으로 변경 예정"
        // 서비스에서 Page 객체를 받아옵니다.
        Page<LectureStuDTO> lectures = instructorService.getMyLectures(instructorId, pageable);

        model.addAttribute("lectures", lectures);
        model.addAttribute("instructorId", instructorId);


        return "role/instructor/lecture_list";
    }
    @GetMapping("/lecture/detail/{id}")
    public String lectureDetail(@PathVariable("id") Long id, Model model) {

        // 이때 DB의 'lecture' 테이블에서 id로 데이터를 한 건 조회합니다.
        LectureStuDTO lecture = lectureStuService.getLectureDetail(id);

        // HTML(lecture_detail.html)에서 사용할 수 있도록 모델에 담습니다.
        model.addAttribute("lecture", lecture);
        return "role/student/lecture_detail";
    }
    /**
     * 강의 등록 페이지 이동
     */
    @GetMapping("/lecture/regist")
    public String registPage() {
        return "role/instructor/lecture_regist"; // templates/lectureRegist.html과 매핑
    }

    /**
     * 강의 등록 실행
     * [이미지 예시 스타일] 파라미터에서 DTO를 직접 선언하여 수신
     */
    @PostMapping("/lecture/regist")
    public String registLecture(
            LectureInsDTO lectureInsDTO,


            RedirectAttributes redirectAttributes) {
        try {
            Long instructorId = 1L;
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
           //return "redirect:/instructor/lecture/regist";
           return "redirect:/instructor/lectures?instructorId=1";
        }

    }

}