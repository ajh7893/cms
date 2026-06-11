package com.example.cms.controller;

import com.example.cms.service.ContentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 정적 페이지 화면 컨트롤러 (읽기 전용).
 */
@Controller
public class ContentController {

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    /** 목록: /contents */
    @GetMapping("/contents")
    public String list(Model model) {
        model.addAttribute("contents", contentService.getContents());
        return "content/list";
    }

    /** 상세: /contents/12 */
    @GetMapping("/contents/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("content", contentService.getContent(id));
        return "content/detail";
    }
}
