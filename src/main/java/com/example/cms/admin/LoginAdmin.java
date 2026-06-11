package com.example.cms.admin;

import java.io.Serializable;

/**
 * 로그인한 관리자 정보 (세션 보관용).
 *
 * <p>엔티티(Member)를 세션에 그대로 넣지 않고, 화면에 필요한 최소 정보만
 * 불변 record 로 담는다. 비밀번호 같은 민감 정보가 세션에 남지 않는다.
 */
public record LoginAdmin(
        Long memberId,      // MEM_IDX
        String userId,      // 로그인 아이디
        String userName,    // 이름
        Integer groupId     // MG_IDX (1=슈퍼관리자, 16=관리자)
) implements Serializable {

    /** 세션에 저장할 때 쓰는 키 */
    public static final String SESSION_KEY = "LOGIN_ADMIN";

    /** 슈퍼관리자 여부 */
    public boolean isSuper() {
        return groupId != null && groupId == 1;
    }

    /** 화면 표시용 그룹 이름 */
    public String groupName() {
        return isSuper() ? "슈퍼관리자" : "관리자";
    }
}
