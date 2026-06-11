package com.example.cms.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 관리자 인증 인터셉터.
 *
 * <p>레거시(HCMS)의 ManageInfoCheckInterceptor 와 같은 역할.
 * /admin/** 요청이 컨트롤러에 도달하기 전에 세션에 로그인 정보가 있는지 검사하고,
 * 없으면 로그인 화면으로 돌려보낸다. 등록은 WebConfig 에서 한다.
 */
public class AdminAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(LoginAdmin.SESSION_KEY) == null) {
            response.sendRedirect("/admin/login");
            return false; // 컨트롤러로 진행하지 않는다
        }
        return true;
    }
}
