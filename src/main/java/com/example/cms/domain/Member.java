package com.example.cms.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 회원/관리자 (기존 테이블 member_info 매핑).
 *
 * <p>member_info 에는 60여 개의 컬럼이 있지만, 관리자 로그인에 필요한
 * 핵심 컬럼만 매핑한다. ddl-auto=none 이므로 문제없다.
 *
 * <p>관리자 구분: 별도 관리자 테이블이 없고, member_group(MG_IDX)으로 구분한다.
 * MG_IDX 1 = 슈퍼관리자, 16 = 관리자.
 */
@Entity
@Table(name = "member_info")
@Getter
@Setter
public class Member {

    @Id
    @Column(name = "MEM_IDX")
    private Long id;                 // 회원 PK

    @Column(name = "USER_ID")
    private String userId;           // 로그인 아이디

    @Column(name = "USER_PWD")
    private String userPwd;          // 비밀번호 (SHA-256 hex, salt 없음 — 레거시 방식)

    @Column(name = "USER_NAME")
    private String userName;         // 이름

    @Column(name = "MG_IDX")
    private Integer groupId;         // 회원 그룹 (member_group FK)

    @Column(name = "LOGIN_YN")
    private String loginYn;          // 로그인 허용 여부 (NULL 인 계정도 많음)

    @Column(name = "DEL_YN")
    private String delYn;            // 삭제 여부 (소프트 삭제)

    @Column(name = "LAST_DATE")
    private LocalDateTime lastDate;  // 마지막 접속일

    @Column(name = "LOGIN_FAIL_CNT")
    private Integer loginFailCount;  // 로그인 실패 횟수 (레거시가 관리하던 값)

    /** 명시적으로 차단(N)된 계정인지 여부 */
    public boolean isLoginBlocked() {
        return "N".equalsIgnoreCase(loginYn);
    }

    /** 삭제된 계정인지 여부 */
    public boolean isDeleted() {
        return "Y".equalsIgnoreCase(delYn);
    }
}
