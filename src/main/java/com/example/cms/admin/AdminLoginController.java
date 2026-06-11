package com.example.cms.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 관리자 로그인/로그아웃 화면 컨트롤러.
 *
 * <p>로그인 성공 시 세션에 {@link LoginAdmin} 을 저장하고,
 * 이후 /admin/** 접근은 {@link AdminAuthInterceptor} 가 세션을 검사한다.
 */
@Controller
public class AdminLoginController {

    private final AdminLoginService adminLoginService;

    public AdminLoginController(AdminLoginService adminLoginService) {
        this.adminLoginService = adminLoginService;
    }

    /** 로그인 화면. 이미 로그인했다면 대시보드로 보낸다. */
    @GetMapping("/admin/login")
    public String loginForm(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(LoginAdmin.SESSION_KEY) != null) {
            return "redirect:/admin";
        }
        return "admin/login";
    }

    /** 로그인 처리 */
    @PostMapping("/admin/login")
    public String login(@RequestParam String userId,
                        @RequestParam String userPwd,
                        HttpServletRequest request,
                        Model model) {
        try {
            LoginAdmin admin = adminLoginService.login(userId, userPwd);

            // 세션 고정 공격 방지: 로그인 성공 시 세션을 새로 발급한다
            request.getSession().invalidate();
            HttpSession session = request.getSession(true);
            session.setAttribute(LoginAdmin.SESSION_KEY, admin);
            session.setMaxInactiveInterval(30 * 60); // 30분 유휴 시 자동 로그아웃

            return "redirect:/admin";

        } catch (IllegalArgumentException e) {
            // 실패: 입력한 아이디는 유지하고 메시지만 보여준다 (비밀번호는 되돌려주지 않음)
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("userId", userId);
            return "admin/login";
        }
    }

    /** 로그아웃 */
    @PostMapping("/admin/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/admin/login";
    }
}
