package com.example.cms.admin;

import com.example.cms.repository.ContentRepository;
import com.example.cms.repository.MemberRepository;
import com.example.cms.repository.PostRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

/**
 * 관리자 대시보드.
 *
 * <p>인터셉터를 통과한 뒤이므로 세션에 LoginAdmin 이 반드시 있다.
 * {@code @SessionAttribute} 로 꺼내 화면에 넘긴다.
 */
@Controller
public class AdminHomeController {

    private final PostRepository postRepository;
    private final ContentRepository contentRepository;
    private final MemberRepository memberRepository;

    public AdminHomeController(PostRepository postRepository,
                               ContentRepository contentRepository,
                               MemberRepository memberRepository) {
        this.postRepository = postRepository;
        this.contentRepository = contentRepository;
        this.memberRepository = memberRepository;
    }

    /** 대시보드: 로그인 후 첫 화면 */
    @GetMapping("/admin")
    public String dashboard(@SessionAttribute(LoginAdmin.SESSION_KEY) LoginAdmin admin,
                            Model model) {
        model.addAttribute("admin", admin);

        // 운영 DB 현황 (소프트 삭제 포함 전체 건수 — 빠른 파악용)
        model.addAttribute("postCount", postRepository.count());
        model.addAttribute("contentCount", contentRepository.count());
        model.addAttribute("memberCount", memberRepository.count());

        return "admin/dashboard";
    }
}
