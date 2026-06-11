package com.example.cms.controller;

import com.example.cms.domain.Post;
import com.example.cms.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 게시글 화면 컨트롤러 (읽기 전용).
 *
 * <p>요청을 받아 Service 를 호출하고, 결과를 Model 에 담아 뷰 이름을 반환한다.
 * 비즈니스 로직은 넣지 않고 얇게 유지한다.
 */
@Controller
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /** 목록: /posts?boardId=41 (기본 41번 게시판) */
    @GetMapping("/posts")
    public String list(@RequestParam(defaultValue = "41") Integer boardId, Model model) {
        List<Post> posts = postService.getPosts(boardId);
        model.addAttribute("posts", posts);
        model.addAttribute("boardId", boardId);
        return "post/list";   // templates/post/list.html
    }

    /** 상세: /posts/123 */
    @GetMapping("/posts/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("post", postService.getPost(id));
        return "post/detail"; // templates/post/detail.html
    }
}
