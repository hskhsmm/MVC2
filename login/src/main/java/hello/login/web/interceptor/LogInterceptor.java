package hello.login.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Slf4j // Lombok - 로그 사용 가능하게 함 (log.info 등)
public class LogInterceptor implements HandlerInterceptor {

    public static final String LOG_ID = "logId"; // 요청 식별을 위한 UUID 키 이름

    /**
     * 컨트롤러 실행 전에 호출됨 (가장 먼저 실행)
     * 요청 URI와 UUID를 로깅하고 request 속성에 저장
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI(); // 요청 URI 가져오기
        String uuid = UUID.randomUUID().toString();  // 각 요청을 식별하기 위한 고유 ID 생성

        // 이 UUID를 request에 저장해두면 나중에 afterCompletion에서 다시 사용할 수 있음
        request.setAttribute(LOG_ID, uuid);

        // handler가 어떤 타입인지 확인 (핸들러 메서드 or 정적 리소스)
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            // 필요하다면 여기서 컨트롤러 클래스, 메서드 정보 등을 추출 가능
        }

        // 요청 로그 출력
        log.info("REQUEST [{}][{}][{}]", uuid, requestURI, handler);
        return true; // true를 반환하면 다음 단계(컨트롤러 등)로 진행됨
    }

    /**
     * 컨트롤러 실행 후, 뷰 렌더링 전에 호출됨
     * ModelAndView를 통해 뷰에 전달될 데이터나 뷰 이름 확인 가능
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle [{}]", modelAndView); // 컨트롤러가 리턴한 모델 및 뷰 정보 로그 출력
    }

    /**
     * 뷰 렌더링까지 모두 완료된 후 호출됨 (예외가 발생해도 무조건 실행됨)
     * 예외가 발생했다면 해당 정보도 함께 로그로 남김
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        String logId = (String) request.getAttribute(LOG_ID); // preHandle에서 저장한 UUID 불러오기

        // 응답 로그 출력
        log.info("RESPONSE [{}][{}][{}]", logId, requestURI, handler);

        // 예외가 발생했다면 로그에 출력
        if (ex != null) {
            log.error("afterCompletion error!!", ex);
        }
    }
}
