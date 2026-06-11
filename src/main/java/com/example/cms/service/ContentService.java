package com.example.cms.service;

import com.example.cms.domain.Content;
import com.example.cms.repository.ContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 정적 페이지 비즈니스 로직.
 */
@Service
@Transactional(readOnly = true)
public class ContentService {

    private final ContentRepository contentRepository;

    public ContentService(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    /** 노출 가능한 페이지 목록 */
    public List<Content> getContents() {
        return contentRepository.findVisible();
    }

    /** 페이지 단건 조회. 없으면 예외 */
    public Content getContent(Long id) {
        return contentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("페이지가 없습니다. id=" + id));
    }
}
