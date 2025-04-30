package hello.login.web.filter;

import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
public class LoginCheckFilter implements Filter {

    // 인증이 필요 없는 경로 목록 (화이트리스트)
    private static final String[] whitelist = {"/", "/members/add", "/login", "/logout", "/css/*"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // ServletRequest와 ServletResponse를 HTTP 기반으로 다운캐스팅
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI(); // 현재 요청 URI 추출

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            log.info("인증 체크 필터 시작 {}", requestURI);

            // 화이트리스트에 없는 경로면 인증 체크 수행
            if (isLoginCheckPath(requestURI)) {
                log.info("인증 체크 로직 실행 {}", requestURI);

                // 현재 세션이 없거나, 세션에 로그인 정보가 없으면
                HttpSession session = httpRequest.getSession(false);
                if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
                    log.info("미인증 사용자 요청 {}", requestURI);

                    // 로그인 페이지로 리다이렉트 + 로그인 성공 후 다시 요청했던 URI로 보내기 위해 redirectURL 파라미터 추가
                    httpResponse.sendRedirect("/login?redirectURL=" + requestURI);
                    return; // 더 이상 다음 필터나 컨트롤러로 진행하지 않고 중단
                }
            }

            // 인증된 사용자거나 화이트리스트에 있는 경우 다음 필터 또는 컨트롤러로 요청 전달
            chain.doFilter(request, response);

        } catch (Exception e) {
            throw e; // 예외는 톰캣까지 던져야 에러 페이지 등에서 처리할 수 있음
        } finally {
            log.info("인증 체크 필터 종료 {}", requestURI); // 요청이 끝나고 나서 무조건 출력되는 로그
        }
    }

    /**
     * 화이트리스트에 포함되지 않는 URI만 로그인 체크 대상이 됨
     * 즉, 로그인 체크가 필요한 경로인지 판단하는 메서드
     */
    private boolean isLoginCheckPath(String requestURI) {
        return !PatternMatchUtils.simpleMatch(whitelist, requestURI);
    }
}
