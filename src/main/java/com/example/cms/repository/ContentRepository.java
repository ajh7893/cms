package com.example.cms.repository;

import com.example.cms.domain.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 정적 페이지 저장소.
 */
public interface ContentRepository extends JpaRepository<Content, Long> {

    /** 삭제되지 않고 노출 설정된 페이지를 정렬순으로 조회 */
    @Query("select c from Content c " +
           "where (c.delYn is null or c.delYn <> 'Y') " +
           "  and (c.viewYn is null or c.viewYn = 'Y') " +
           "order by c.sort asc, c.id asc")
    List<Content> findVisible();
}
