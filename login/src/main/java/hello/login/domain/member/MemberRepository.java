package hello.login.domain.member;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j // 로그를 찍을 수 있게 지원 (log.info() 같은거 사용 가능)
@Repository // 스프링이 이 클래스를 빈으로 등록해줌 (DAO/Repository 계층 의미)
public class MemberRepository {

    // 회원 정보를 저장하는 Map (Key: 회원 ID(Long), Value: Member 객체)
    private static Map<Long, Member> store = new HashMap<>();

    // 회원 ID를 생성하기 위한 sequence (1씩 증가)
    private static long sequence = 0L;

    /**
     * 회원 저장
     * @param member 저장할 회원 객체
     * @return 저장된 회원 객체 반환
     */
    public Member save(Member member) {
        member.setId(++sequence); // 시퀀스를 하나 증가시키고 회원 ID로 설정
        log.info("save: member={}", member); // 저장한 회원 정보 로그 출력
        store.put(member.getId(), member); // store(Map)에 저장
        return member;
    }

    /**
     * ID로 회원 조회
     * @param id 회원 ID
     * @return 해당 ID를 가진 회원 객체
     */
    public Member findById(long id) {
        return store.get(id); // Map에서 ID로 바로 조회
    }

    /**
     * 로그인 ID로 회원 조회
     * @param loginId 로그인 ID
     * @return Optional<Member> (회원이 있으면 반환, 없으면 빈 Optional 반환)
     */
    public Optional<Member> findByLoginId(String loginId) {
        // 모든 회원을 다 가져와서, loginId가 일치하는 회원을 찾아서 반환
        return findAll().stream()
                .filter(m -> m.getLoginId().equals(loginId)) // loginId 일치하는지 확인
                .findFirst(); // 처음 일치하는 회원 반환
    }

    /**
     * 저장된 모든 회원 목록 조회
     * @return 모든 회원 리스트
     */
    public List<Member> findAll() {
        return new ArrayList<>(store.values());
        // store에 저장된 Member 객체들을 새 리스트로 반환
    }

    /**
     * 저장소 초기화 (테스트용 등)
     */
    public void clearStore() {
        store.clear(); // Map 비우기
    }
}
