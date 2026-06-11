package com.example.cms.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 게시글 (기존 테이블 bbs_board 매핑).
 *
 * <p>bbs_board 에는 80여 개의 컬럼이 있지만, 학습/조회에 필요한 핵심 컬럼만 매핑한다.
 * ddl-auto=none 이므로 매핑하지 않은 컬럼이 있어도 문제없다.
 *
 * <p>주의: 이 테이블은 날짜를 datetime 이 아니라 varchar(30) 으로 저장한다(레거시).
 * 그래서 writeDate 는 LocalDateTime 이 아니라 String 으로 받는다.
 */
@Entity
@Table(name = "bbs_board")
@Getter
@Setter
public class Post {

    @Id
    @Column(name = "B_IDX")
    private Long id;                 // 게시글 PK

    @Column(name = "BS_IDX")
    private Integer boardId;         // 어느 게시판(bbs_setting)에 속하는지

    @Column(name = "BC_IDX")
    private Integer categoryId;      // 카테고리(bbs_category)

    @Column(name = "SUBJECT")
    private String subject;          // 제목

    @Column(name = "REMARK")
    private String content;          // 내용 (longtext)

    @Column(name = "WRITER")
    private String writer;           // 작성자

    @Column(name = "VIEW_CNT")
    private Integer viewCount;       // 조회수

    @Column(name = "COMMENT_CNT")
    private Integer commentCount;    // 댓글 수

    @Column(name = "FILE_CNT")
    private Integer fileCount;       // 첨부파일 수

    @Column(name = "NOTICE_YN")
    private String noticeYn;         // 공지 여부 (Y/N)

    @Column(name = "WRITE_DATE")
    private String writeDate;        // 작성일 (레거시: varchar)

    @Column(name = "DEL_YN")
    private String delYn;            // 삭제 여부 (소프트 삭제)

    /** 공지글 여부 편의 메서드 */
    public boolean isNotice() {
        return "Y".equalsIgnoreCase(noticeYn);
    }
}
