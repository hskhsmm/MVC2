package hello.login.domain.login;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service // 서비스 계층임을 나타냄 (Spring이 관리)
@RequiredArgsConstructor // final 필드 생성자 자동 생성
public class LoginService {

    private final MemberRepository memberRepository; // 회원 정보 저장소 의존

    /**
     * 로그인 기능
     * @param loginId 사용자가 입력한 아이디
     * @param password 사용자가 입력한 비밀번호
     * @return 로그인 성공하면 Member 객체 반환, 실패하면 null 반환
     */
    public Member login(String loginId, String password) {
        return memberRepository.findByLoginId(loginId) // 아이디로 회원 조회
                .filter(m -> m.getPassword().equals(password)) // 비밀번호 일치하면
                .orElse(null); // 없으면 null 반환
    }
}
