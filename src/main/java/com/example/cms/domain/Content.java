package com.example.cms.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 정적 페이지 (기존 테이블 contents 매핑).
 *
 * <p>회사소개 등 게시판이 아닌 단일 콘텐츠 페이지. bbs_board 와 달리
 * 날짜 컬럼이 datetime 이라 LocalDateTime 으로 매핑할 수 있다.
 */
@Entity
@Table(name = "contents")
@Getter
@Setter
public class Content {

    @Id
    @Column(name = "CI_IDX")
    private Long id;                 // 콘텐츠 PK

    @Column(name = "SITE_IDX")
    private Integer siteId;          // 멀티사이트 구분

    @Column(name = "SUBJECT")
    private String subject;          // 제목

    @Column(name = "REMARK")
    private String content;          // 본문 (longtext)

    @Column(name = "CONTENTS_TYPE")
    private String contentsType;     // 콘텐츠 유형

    @Column(name = "VIEW_YN")
    private String viewYn;           // 노출 여부 (Y/N)

    @Column(name = "SORT")
    private Integer sort;            // 정렬 순서

    @Column(name = "REG_DATE")
    private LocalDateTime regDate;   // 등록일

    @Column(name = "DEL_YN")
    private String delYn;            // 삭제 여부 (소프트 삭제)
}
