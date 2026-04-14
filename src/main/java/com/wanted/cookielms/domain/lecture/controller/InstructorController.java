package com.wanted.cookielms.domain.lecture.controller;

import com.wanted.cookielms.domain.lecture.dto.LectureInsDTO;
import com.wanted.cookielms.domain.lecture.dto.LectureStuDTO;
import com.wanted.cookielms.domain.lecture.service.InstructorService;
import com.wanted.cookielms.domain.lecture.service.LectureStuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @GetMapping("/lecture/detail/{id}")
    public String lectureDetail(@PathVariable("id") Long id, Model model) {
        // 친구가 만든 서비스 로직을 사용하여 데이터를 가져옵니다.
        // 이때 DB의 'lecture' 테이블에서 id로 데이터를 한 건 조회합니다.
        LectureStuDTO lecture = lectureStuService.getLectureDetail(id);

        // 친구의 HTML(lecture_detail.html)에서 사용할 수 있도록 모델에 담습니다.
        model.addAttribute("lecture", lecture);

        // 친구가 만든 상세 페이지 HTML 경로로 연결합니다.
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
    public String registLecture(LectureInsDTO lectureInsDTO, RedirectAttributes redirectAttributes) {
        try {
            instructorService.registLecture(lectureInsDTO);
            log.info("✅ 강의 등록 성공: {}", lectureInsDTO.getTitle());
            // 성공 시 FlashAttribute 사용
            redirectAttributes.addFlashAttribute("message", "✅ 강의 등록이 완료되었습니다!");
            return "redirect:/instructor/lecture/regist?status=success";

        } catch (IllegalArgumentException e) {
            // [수정] 서비스에서 던진 메시지("5MB를 초과합니다...")를 그대로 전달
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/instructor/lecture/regist";

        } catch (IOException e) {
            log.error("❌ 파일 저장 에러", e);
            redirectAttributes.addFlashAttribute("errorMessage", "파일 저장 중 오류가 발생했습니다.");
            return "redirect:/instructor/lecture/regist";
        }

    }
}