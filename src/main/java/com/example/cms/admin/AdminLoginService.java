package com.example.cms.admin;

import com.example.cms.domain.Member;
import com.example.cms.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Set;

/**
 * 관리자 로그인 비즈니스 로직.
 *
 * <p>레거시(HCMS)와 동일한 규칙으로 검증한다:
 * <ul>
 *   <li>member_info 에서 아이디로 조회 (DEL_YN='Y' 제외)</li>
 *   <li>비밀번호는 salt 없는 SHA-256 hex 비교 (기존 데이터와의 호환을 위해 유지)</li>
 *   <li>관리자 그룹(MG_IDX 1, 16)만 로그인 허용</li>
 *   <li>LOGIN_YN='N' 인 차단 계정 거부</li>
 * </ul>
 *
 * <p>1단계는 읽기 전용이라 레거시가 하던 LAST_DATE/LOGIN_FAIL_CNT 갱신은
 * 하지 않는다. (쓰기 단계에서 추가 예정)
 *
 * <p>⚠️ 무염 SHA-256 은 현대 기준으로 약한 방식이다. 기존 운영 데이터를 깨지 않기
 * 위해 유지하는 것이며, 추후 BCrypt 마이그레이션(로그인 성공 시 재해싱)이 과제다.
 */
@Service
@Transactional(readOnly = true)
public class AdminLoginService {

    /** 관리자로 인정하는 member_group: 1=슈퍼관리자, 16=관리자 */
    private static final Set<Integer> ADMIN_GROUP_IDS = Set.of(1, 16);

    /** 아이디/비밀번호 불일치 시 공통 메시지 (어느 쪽이 틀렸는지 노출하지 않는다) */
    private static final String FAIL_MESSAGE = "아이디 또는 비밀번호가 올바르지 않습니다.";

    private final MemberRepository memberRepository;

    public AdminLoginService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * 로그인 검증. 성공하면 세션에 담을 LoginAdmin 을 돌려주고,
     * 실패하면 사용자에게 보여줄 메시지를 담아 예외를 던진다.
     */
    public LoginAdmin login(String userId, String rawPassword) {
        Member member = memberRepository.findActiveByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException(FAIL_MESSAGE));

        // 비밀번호 비교: 평문을 SHA-256 hex 로 바꿔서 저장값과 비교
        String hashed = sha256Hex(rawPassword);
        if (member.getUserPwd() == null || !member.getUserPwd().equalsIgnoreCase(hashed)) {
            throw new IllegalArgumentException(FAIL_MESSAGE);
        }

        // 관리자 그룹이 아니면 거부 (일반 회원은 관리자 화면에 못 들어온다)
        if (member.getGroupId() == null || !ADMIN_GROUP_IDS.contains(member.getGroupId())) {
            throw new IllegalArgumentException("관리자 권한이 없는 계정입니다.");
        }

        // 명시적으로 차단된 계정
        if (member.isLoginBlocked()) {
            throw new IllegalArgumentException("로그인이 차단된 계정입니다. 관리자에게 문의하세요.");
        }

        return new LoginAdmin(member.getId(), member.getUserId(),
                member.getUserName(), member.getGroupId());
    }

    /** SHA-256 해시 후 소문자 hex 문자열로 변환 (레거시 저장 형식) */
    private static String sha256Hex(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 은 모든 JVM 에 내장되어 있어 사실상 발생하지 않는다
            throw new IllegalStateException("SHA-256 을 사용할 수 없습니다.", e);
        }
    }
}
