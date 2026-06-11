package com.example.cms.repository;

import com.example.cms.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * 회원/관리자 저장소.
 *
 * <p>USER_ID 컬럼은 유니크 인덱스가 아니라서 (탈퇴 후 재가입 등으로 중복 가능)
 * 삭제되지 않은 계정 중 가장 최근 것 하나만 가져오도록 직접 쿼리를 작성한다.
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    /** 아이디로 삭제되지 않은 회원 1명 조회 (최근 가입 우선) */
    @Query("select m from Member m " +
           "where m.userId = :userId " +
           "  and (m.delYn is null or m.delYn <> 'Y') " +
           "order by m.id desc " +
           "limit 1")
    Optional<Member> findActiveByUserId(@Param("userId") String userId);
}
