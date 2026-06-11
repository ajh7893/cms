package com.example.cms.repository;

import com.example.cms.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 게시글 저장소.
 *
 * <p>JpaRepository 를 상속하면 findById/findAll/save 등 기본 CRUD 메서드가
 * 자동으로 생긴다. 구현체는 Spring 이 런타임에 만들어 준다.
 *
 * <p>이 DB 는 실제 DELETE 대신 DEL_YN='Y' 로 소프트 삭제하므로,
 * 조회 시 삭제되지 않은 글만 가져오도록 직접 쿼리를 작성한다.
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    /** 특정 게시판의 삭제되지 않은 글을 최신순으로 조회 */
    @Query("select p from Post p " +
           "where p.boardId = :boardId " +
           "  and (p.delYn is null or p.delYn <> 'Y') " +
           "order by p.id desc")
    List<Post> findVisibleByBoard(@Param("boardId") Integer boardId);
}
