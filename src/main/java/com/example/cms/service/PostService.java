package com.example.cms.service;

import com.example.cms.domain.Post;
import com.example.cms.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 게시글 비즈니스 로직.
 *
 * <p>Controller 와 Repository 사이에서 "무엇을 할지"를 담당한다.
 * 지금은 조회만 하므로 readOnly 트랜잭션으로 둔다.
 */
@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /** 특정 게시판의 글 목록 */
    public List<Post> getPosts(Integer boardId) {
        return postRepository.findVisibleByBoard(boardId);
    }

    /** 게시글 단건 조회. 없으면 예외 */
    public Post getPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다. id=" + id));
    }
}
