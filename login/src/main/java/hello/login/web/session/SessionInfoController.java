package hello.login.web.session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Slf4j
@RestController
public class SessionInfoController {

    @GetMapping("/session-info")
    public String sessionInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "세션이 없습니다.";
        }

        session.getAttributeNames().asIterator()
                .forEachRemaining(name -> log.info("session name = {},value={}", name, session.getAttribute(name)));

        // 현재 세션의 ID를 출력 (서버가 부여한 고유한 세션 식별자)
        log.info("session id = {}", session.getId());

        // 세션의 최대 유효 시간(초)을 출력 (기본 1800초 = 30분, 이 시간 안에 요청 없으면 세션 만료됨)
        log.info("getMaxInactiveInterval = {}", session.getMaxInactiveInterval());

        // 세션이 처음 생성된 시간(타임스탬프)을 출력 (Date 객체로 변환해서 사람이 읽을 수 있는 형태로 출력)
        log.info("creationTime = {}", new Date(session.getCreationTime()));

        // 세션에 마지막으로 접근한 시간(타임스탬프)을 출력 (최근에 이 세션을 사용한 시간)
        log.info("lastAccessedTime = {}", new Date(session.getLastAccessedTime()));

        // 이 세션이 "새로 생성된 세션"인지 여부를 출력 (true: 이번 요청에서 처음 만들어진 세션, false: 기존 세션 재사용)
        log.info("isNew = {}", session.isNew());


        return "세션 출력";
    }

}
